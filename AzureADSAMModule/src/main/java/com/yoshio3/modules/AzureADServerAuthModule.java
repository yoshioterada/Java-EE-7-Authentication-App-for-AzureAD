/*
* Copyright 2016 Yoshio Terada
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
 */
package com.yoshio3.modules;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.openid.connect.sdk.AuthenticationErrorResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponseParser;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.ServiceUnavailableException;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.callback.CallerPrincipalCallback;
import javax.security.auth.message.module.ServerAuthModule;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Yoshio Terada
 */
public class AzureADServerAuthModule implements ServerAuthModule {

    private static final Logger LOGGER = Logger.getLogger(AzureADServerAuthModule.class.getName());

    public final static String ERROR = "error";
    public final static String ERROR_DESCRIPTION = "error_description";
    public final static String ERROR_URI = "error_uri";
    public final static String ID_TOKEN = "id_token";
    public final static String CODE = "code";

    private static final String SAVED_SUBJECT = "saved_subject";
    public static final String PRINCIPAL_SESSION_NAME = "principal";

    private String authority = "";
    private String tenant = "";
    private String clientId = "";
    private String secretKey = "";
    private final static String loginContextName = "AzureAD-Login";

    static final String AUTHORIZATION_HEADER = "authorization";

    private static final Class[] SUPPORTED_MESSAGE_TYPE = new Class[]{
        HttpServletRequest.class,
        HttpServletResponse.class};

    private MessagePolicy requestPolicy;
    private MessagePolicy responsePolicy;
    private CallbackHandler handler;
    private Map<String, String> options;

    private LoginContext loginContext = null;

    public MessagePolicy getRequestPolicy() {
        return requestPolicy;
    }

    public MessagePolicy getResponsePolicy() {
        return responsePolicy;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    @Override
    public void initialize(MessagePolicy requestPolicy, MessagePolicy responsePolicy, CallbackHandler handler, Map options) throws AuthException {
        this.requestPolicy = requestPolicy;
        this.responsePolicy = responsePolicy;
        this.handler = handler;
        this.options = options;
        if (options == null) {
            return;
        }
        if (options.containsKey("authority")) {
            authority = (String) options.get("authority");
        }
        if (options.containsKey("tenant")) {
            tenant = (String) options.get("tenant");
        }
        if (options.containsKey("client_id")) {
            clientId = (String) options.get("client_id");
        }
        if (options.containsKey("secret_key")) {
            secretKey = (String) options.get("secret_key");
        }
    }

    @Override
    public Class[] getSupportedMessageTypes() {
        return SUPPORTED_MESSAGE_TYPE;
    }

    @Override
    public AuthStatus validateRequest(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject) throws AuthException {
        HttpServletRequest httpRequest = (HttpServletRequest) messageInfo.getRequestMessage();
        HttpServletResponse httpResponse = (HttpServletResponse) messageInfo.getResponseMessage();

        Callback[] callbacks;

        //Azure AD の認証後、リダイレクトで返ってきた場合
        //プリンシパル情報はないので、認証に成功している場合、プリンシパルに追加
        Map<String, String> params = new HashMap<>();
        httpRequest.getParameterMap().keySet().stream().forEach(key -> {
            params.put(key, httpRequest.getParameterMap().get(key)[0]);
        });
        String currentUri = getCurrentUri(httpRequest);

        //セッション情報に認証結果が含まれない場合        
        if (!getSessionPrincipal(httpRequest)) {
            if (!isRedirectedRequestFromAuthServer(httpRequest, params)) {
                try {
                    // Azure AD に Redirect 
                    return redirectOpenIDServer(httpResponse, currentUri);
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, "Invalid redirect URL", ex);
                    return AuthStatus.SEND_FAILURE;
                }
            } else {
                // Azure AD から返ってきたリクエストの場合
                messageInfo.getMap().put("javax.servlet.http.registerSession", Boolean.TRUE.toString());
                messageInfo.getMap().put("javax.servlet.http.authType", "AzureADServerAuthModule");
                return getAuthResultFromServerAndSetSession(clientSubject, httpRequest, params, currentUri);
            }
        } else {
            try {
                //セッション情報に認証結果が含まれる場合
                AzureADUserPrincipal sessionPrincipal = (AzureADUserPrincipal) httpRequest.getUserPrincipal();
                AuthenticationResult authenticationResult = sessionPrincipal.getAuthenticationResult();
                if (authenticationResult.getExpiresOnDate().before(new Date())) {
                    //認証の日付が古い場合・リフレッシュトークンからアクセストークン取得
                    AuthenticationResult authResult = getAccessTokenFromRefreshToken(
                            authenticationResult.getRefreshToken(), currentUri);
                    setSessionPrincipal(httpRequest, new AzureADUserPrincipal(authResult));
                }
                CallerPrincipalCallback callerCallBack = new CallerPrincipalCallback(clientSubject, sessionPrincipal);
                callbacks = new Callback[]{callerCallBack};
                handler.handle(callbacks);
                return AuthStatus.SUCCESS;
            } catch (Throwable ex) {
                LOGGER.log(Level.SEVERE, "Invalid Session Info", ex);
                return AuthStatus.SEND_FAILURE;
            }
        }
    }

    @Override
    public AuthStatus secureResponse(MessageInfo messageInfo, Subject serviceSubject) throws AuthException {
        return AuthStatus.SEND_SUCCESS;
    }

    @Override
    public void cleanSubject(MessageInfo messageInfo, Subject subject) throws AuthException {
        try {
            if (subject != null) {
                subject.getPrincipals().clear();
            }
            loginContext.logout();
        } catch (LoginException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    /* ステップ１ 認証されていな最初のリクエストの場合 */
    private AuthStatus redirectOpenIDServer(HttpServletResponse httpResponse, String currentUri) throws UnsupportedEncodingException, IOException {
        //認証しておらず、認証データを持っていない場合
        // 認証していない場合は Azure AD の認証画面にリダイレクト
        String redirectUrl = getRedirectUrl(currentUri);
        httpResponse.setStatus(302);
        httpResponse.sendRedirect(getRedirectUrl(currentUri));
        return AuthStatus.SEND_CONTINUE;
    }

    /* ステップ２ リダイレクトされ code, id_token などが含まれる場合
       この時、認証結果をセッションに保存
     */
    private AuthStatus getAuthResultFromServerAndSetSession(Subject clientSubject, HttpServletRequest httpRequest, Map<String, String> params, String currentUri) {
        try {
            String fullUrl = currentUri
                    + (httpRequest.getQueryString() != null ? "?"
                            + httpRequest.getQueryString() : "");
            AuthenticationResponse authResponse = AuthenticationResponseParser
                    .parse(new URI(fullUrl), params);
            //params の中に error が含まれている場合、AuthenticationErrorResponse
            //成功の場合 AuthenticationSuccessResponse が返る

            //認証に成功した場合
            if (authResponse instanceof AuthenticationSuccessResponse) {
                //レスポンスから結果を取得しセッションに保存
                AuthenticationSuccessResponse authSuccessResponse = (AuthenticationSuccessResponse) authResponse;
                AuthenticationResult result = getAccessToken(authSuccessResponse.getAuthorizationCode(), currentUri);
                AzureADUserPrincipal userPrincipal = new AzureADUserPrincipal(result);
                setSessionPrincipal(httpRequest, userPrincipal);

                //ユーザ・プリンシパルの設定 ///////////  ここを修正する必要あり
                String userID = userPrincipal.getName();
                System.out.println("認証済みユーザID:" + userID);

                AzureADCallbackHandler azureCallBackHandler = new AzureADCallbackHandler(clientSubject, httpRequest, userPrincipal);

                loginContext = new LoginContext(loginContextName, azureCallBackHandler);
                loginContext.login();
                Subject subject = loginContext.getSubject();

                CallerPrincipalCallback callerCallBack = new CallerPrincipalCallback(clientSubject, userPrincipal);
                Callback[] callbacks = new Callback[]{callerCallBack};
                handler.handle(callbacks);

                return AuthStatus.SUCCESS;
            } else {
                // 認証に失敗した場合
                AuthenticationErrorResponse authErrorResponse = (AuthenticationErrorResponse) authResponse;
                CallerPrincipalCallback callerCallBack = new CallerPrincipalCallback(clientSubject, (Principal) null);
                Callback[] callbacks = new Callback[]{callerCallBack};
                handler.handle(callbacks);

                return AuthStatus.FAILURE;
            }
        } catch (Throwable ex) {
            CallerPrincipalCallback callerCallBack = new CallerPrincipalCallback(clientSubject, (Principal) null);
            Callback[] callbacks = new Callback[]{callerCallBack};
            try {
                handler.handle(callbacks);
            } catch (IOException | UnsupportedCallbackException ex1) {
                LOGGER.log(Level.SEVERE, null, ex1);
            }
            LOGGER.log(Level.SEVERE, null, ex);
            return AuthStatus.FAILURE;
        }
    }


    /* リフレッシュ・トークンからアクセス・トークンの取得  */
    private AuthenticationResult getAccessTokenFromRefreshToken(
            String refreshToken, String currentUri) throws Throwable {
        AuthenticationContext context;
        AuthenticationResult result;
        ExecutorService service = null;
        try {
            service = Executors.newFixedThreadPool(1);
            context = new AuthenticationContext(authority + tenant + "/", true, service);
            Future<AuthenticationResult> future = context
                    .acquireTokenByRefreshToken(refreshToken,
                            new ClientCredential(clientId, secretKey), null,
                            null);
            result = future.get();
        } catch (ExecutionException e) {
            throw e.getCause();
        } finally {
            if (service != null) {
                service.shutdown();
            }
        }

        if (result == null) {
            throw new ServiceUnavailableException(
                    "authentication result was null");
        }
        return result;

    }

    /* アクセス・トークンの取得*/
    private AuthenticationResult getAccessToken(
            AuthorizationCode authorizationCode, String currentUri)
            throws Throwable {
        String authCode = authorizationCode.getValue();
        ClientCredential credential = new ClientCredential(clientId, secretKey);
        AuthenticationContext context;
        AuthenticationResult result;
        ExecutorService service = null;
        try {
            service = Executors.newFixedThreadPool(1);
            context = new AuthenticationContext(authority + tenant + "/", true,
                    service);
            Future<AuthenticationResult> future = context
                    .acquireTokenByAuthorizationCode(authCode, new URI(
                            currentUri), credential, null);
            result = future.get();
        } catch (ExecutionException e) {
            throw e.getCause();
        } finally {
            if (service != null) {
                service.shutdown();
            }
        }

        if (result == null) {
            throw new ServiceUnavailableException(
                    "authentication result was null");
        }
        return result;
    }

    /* HTTP セッションに認証情報を設定 */
    private void setSessionPrincipal(HttpServletRequest httpRequest,
            AzureADUserPrincipal principal) throws Exception {
        httpRequest.getSession().setAttribute(PRINCIPAL_SESSION_NAME, principal);
    }

    /* HTTP セッションから認証結果の取得 */
    public boolean getSessionPrincipal(HttpServletRequest request) {
        return request.getUserPrincipal() != null;
//        return (AzureADUserPrincipal) request.getSession().getAttribute(PRINCIPAL_SESSION_NAME);
    }

    private void setSessionSubject(HttpServletRequest httpRequest, final Subject clientSubject) {
        if (clientSubject == null) {
            return;
        }
        httpRequest.getSession().setAttribute(SAVED_SUBJECT, clientSubject);
        LOGGER.log(Level.FINE, "Saved subject {0}", clientSubject);
    }

    private Subject getSessionSubject(HttpServletRequest httpRequest) {
        return (Subject) httpRequest.getSession().getAttribute(SAVED_SUBJECT);
    }


    /* リダイレクト URL の取得 */
    private String getRedirectUrl(String currentUri)
            throws UnsupportedEncodingException {
        String redirectUrl = authority
                + this.tenant
                + "/oauth2/authorize?response_type=code%20id_token&scope=openid&response_mode=form_post&redirect_uri="
                + URLEncoder.encode(currentUri, "UTF-8") + "&client_id="
                + clientId + "&resource=https%3a%2f%2fgraph.windows.net"
                + "&nonce=" + UUID.randomUUID() + "&site_id=500879";
        return redirectUrl;
    }

    /* HTTP セッションから認証済みか否かのチェック */
    public boolean isRedirectedRequestFromAuthServer(HttpServletRequest httpRequest, Map<String, String> params) {
        return httpRequest.getMethod().equalsIgnoreCase("POST")
                && (httpRequest.getParameterMap().containsKey(ERROR)
                || httpRequest.getParameterMap().containsKey(
                        ID_TOKEN) || httpRequest.getParameterMap().containsKey(CODE));
    }


    /* 認証データが含まれるか否かのチェック */
    public boolean containsAuthenticationData(HttpServletRequest httpRequest) {
//        System.out.println("containsAuthenticationData プリンシパル名：" + httpRequest.getUserPrincipal().getName());

        Map<String, String[]> map = httpRequest.getParameterMap();

        return httpRequest.getMethod().equalsIgnoreCase("POST")
                && (httpRequest.getParameterMap().containsKey(ERROR)
                || httpRequest.getParameterMap().containsKey(
                        ID_TOKEN) || httpRequest.getParameterMap().containsKey(CODE));
    }


    /* リクエストの URI を取得 */
    private String getCurrentUri(HttpServletRequest request) {
        String scheme = request.getScheme();
        int serverPort = request.getServerPort();
        String portNumberString = "";
        if (!((scheme.equals("http") && serverPort == 80) || (scheme.equals("https") && serverPort == 443))) {
            portNumberString = ":" + String.valueOf(serverPort);
        }
        String uri = scheme + "://"
                + request.getServerName()
                + portNumberString
                + request.getRequestURI();
        return uri;
    }

}
