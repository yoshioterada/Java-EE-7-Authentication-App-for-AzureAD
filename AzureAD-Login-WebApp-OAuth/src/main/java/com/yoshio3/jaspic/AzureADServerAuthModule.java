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
package com.yoshio3.jaspic;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.openid.connect.sdk.AuthenticationErrorResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponseParser;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;
import com.yoshio3.azuread.graph.GraphAPIImpl;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
import javax.security.auth.message.callback.GroupPrincipalCallback;
import javax.security.auth.message.module.ServerAuthModule;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Yoshio Terada
 */
public class AzureADServerAuthModule implements ServerAuthModule {

    public AzureADServerAuthModule(Map<String, String> options) {
        this.options = options;
    }

    private static final Logger LOGGER = Logger.getLogger(AzureADServerAuthModule.class.getName());

    public final static String ERROR = "error";
    public final static String ERROR_DESCRIPTION = "error_description";
    public final static String ERROR_URI = "error_uri";
    public final static String ID_TOKEN = "id_token";
    public final static String CODE = "code";

    private static final String SAVED_SUBJECT = "saved_subject";
    public static final String PRINCIPAL_SESSION_NAME = "principal";

    /* web.xml で記載した設定情報の取得 */
    /* get the configuration entered in web.xml */
    private String authority = "";
    private String tenant = "";
    private String clientId = "";
    private String secretKey = "";
    private String graphServer = "";
    private String logContext = "";

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
        if (options.containsKey("graph_server")) {
            graphServer = (String) options.get("graph_server");
        }
        if (options.containsKey("javax.security.auth.login.LoginContext")) {
            logContext = (String) options.get("javax.security.auth.login.LoginContext");
        }
    }

    @Override
    public Class[] getSupportedMessageTypes() {
        return SUPPORTED_MESSAGE_TYPE;
    }

    /*
    参考：
    Reference:
    https://github.com/javaee-samples/javaee7-samples/blob/master/jaspic/custom-principal/src/main/java/org/javaee7/jaspic/customprincipal/sam/TestServerAuthModule.java
    Communicate the details of the authenticated user to the container. In many
    cases the handler will just store the details and the container will actually handle
    the login after we return from this method.
     */
    @Override
    public AuthStatus validateRequest(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject) throws AuthException {
        HttpServletRequest httpRequest = (HttpServletRequest) messageInfo.getRequestMessage();
        HttpServletResponse httpResponse = (HttpServletResponse) messageInfo.getResponseMessage();
        Callback[] callbacks;

        //Azure AD の認証後、リダイレクトで返ってきた場合
        // if returning via redirect after authentication on Azure AD
        //プリンシパル情報はないので、認証に成功している場合、プリンシパルに追加
        // because there is no principal information, if authentication was successful add into to the principal
        Map<String, String> params = new HashMap<>();
        httpRequest.getParameterMap().keySet().stream().forEach(key -> {
            params.put(key, httpRequest.getParameterMap().get(key)[0]);
        });
        String currentUri = getCurrentUri(httpRequest);
        try {
            //セッション情報に認証結果が含まれない場合
            // if the authentication result is not included in the session
            if (!getSessionPrincipal(httpRequest)) {
                if (!isRedirectedRequestFromAuthServer(httpRequest, params)) {
                    // 最初のリクエストの場合は Azure AD に Redirect
                    // if it is the initial request, redirect to Azure AD
                    redirectOpenIDServer(httpResponse, currentUri);
                    return AuthStatus.SEND_CONTINUE;

                } else {
                    // 認証結果が含まれず Azure AD から返ってきたリクエストの場合
                    // if it's a request coming back from Azure AD without authentication results
                    messageInfo.getMap().put("javax.servlet.http.registerSession", Boolean.TRUE.toString());
                    messageInfo.getMap().put("javax.servlet.http.authType", "AzureADServerAuthModule");
                    String fullUrl = currentUri
                            + (httpRequest.getQueryString() != null ? "?"
                                    + httpRequest.getQueryString() : "");
                    AuthenticationResponse authResponse = AuthenticationResponseParser.parse(new URI(fullUrl), params);
                    //params の中に error が含まれている場合、AuthenticationErrorResponse
                    // if there is an error key in params, return AuthenticationErrorResponse
                    //成功の場合 AuthenticationSuccessResponse が返る
                    // if it was successful, return AuthenticationSuccessResponse
                    if (authResponse instanceof AuthenticationSuccessResponse) {
                        //認証に成功した場合
                        // if authentication was successful
                        //リダイレクトされ code, id_token などが含まれる場合
                        // when there is a code, id_token etc after being redirected
                        //この時、認証結果をセッションに保存
                        // at this point, save the authentication result into the session
                        onAuthenticationSuccess(httpRequest, authResponse, clientSubject, currentUri);
                        return AuthStatus.SUCCESS;
                    } else {
                        //認証に失敗した場合
                        // if authentication failed
                        onAuthenticationFailer(authResponse, clientSubject);
                        return AuthStatus.SEND_FAILURE;
                    }
                }
            } else {
                //セッション情報に認証結果が含まれる場合・アクセス・トークンの期限をチェック
                // if there is an authentication result in the session info, verify the validity of the access token
                AzureADUserPrincipal sessionPrincipal = (AzureADUserPrincipal) httpRequest.getUserPrincipal();
                AuthenticationResult authenticationResult = sessionPrincipal.getAuthenticationResult();
                if (authenticationResult.getExpiresOnDate().before(new Date())) {
                    //認証の日付が古い場合・リフレッシュトークンからアクセストークン取得
                    // if the authentication date is old - get an access token from the refresh token
                    AuthenticationResult authResult = getAccessTokenFromRefreshToken(
                            authenticationResult.getRefreshToken(), currentUri);
                    setSessionPrincipal(httpRequest, new AzureADUserPrincipal(authResult));
                }
                // ユーザ・プリンシパル、グループ情報を CallBack Handler で受け渡し
                // pass user principal and group info via callback handlers
                CallerPrincipalCallback callerCallBack = new CallerPrincipalCallback(clientSubject, sessionPrincipal);
                String[] groups = getGroupList(httpRequest, sessionPrincipal);
                GroupPrincipalCallback groupPrincipalCallback = new GroupPrincipalCallback(clientSubject, groups);
                callbacks = new Callback[]{callerCallBack, groupPrincipalCallback};
                handler.handle(callbacks);
                return AuthStatus.SUCCESS;
            }
        } catch (URISyntaxException | IOException | ParseException | UnsupportedCallbackException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return AuthStatus.SEND_FAILURE;
        } catch (Throwable ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return AuthStatus.SEND_FAILURE;
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

    /* 認証されていな最初のリクエストの場合 Azure AD にリダイレクト*/
    /* if it's the initial unauthenticated request, redirect to Azure AD */
    private void redirectOpenIDServer(HttpServletResponse httpResponse, String currentUri) throws UnsupportedEncodingException, IOException {
        //認証しておらず、認証データを持っていない場合
        // if unauthenticated with no authentication data
        // 認証していない場合は Azure AD の認証画面にリダイレクト
        // when it's unauthenticated, redirect to Azure AD authentication screen
        String redirectUrl = getRedirectUrl(currentUri);
        httpResponse.setStatus(302);
        httpResponse.sendRedirect(getRedirectUrl(currentUri));
    }

    /* 認証に成功した場合、ユーザ・プリンシパル、グループ情報を設定し call back で情報の受け渡し */
    /* if authentication was successful, set user principal and group information, and pass the information with callbacks */
    private void onAuthenticationSuccess(HttpServletRequest httpRequest, AuthenticationResponse authResponse, Subject clientSubject, String currentUri) throws Throwable {
        //レスポンスから結果を取得しセッションに保存
        // get the result from the response and save it in the session
        AuthenticationSuccessResponse authSuccessResponse = (AuthenticationSuccessResponse) authResponse;
        AuthenticationResult result = getAccessToken(authSuccessResponse.getAuthorizationCode(), currentUri);
        AzureADUserPrincipal userPrincipal = new AzureADUserPrincipal(result);
        setSessionPrincipal(httpRequest, userPrincipal);

        //ユーザ・プリンシパルの設定
        // setting user principal
        String[] groups = getGroupList(httpRequest, userPrincipal);
        AzureADCallbackHandler azureCallBackHandler = new AzureADCallbackHandler(clientSubject, httpRequest, userPrincipal);
        loginContext = new LoginContext(logContext, azureCallBackHandler);
        loginContext.login();
        Subject subject = loginContext.getSubject();

        CallerPrincipalCallback callerCallBack = new CallerPrincipalCallback(clientSubject, userPrincipal);
        GroupPrincipalCallback groupPrincipalCallback = new GroupPrincipalCallback(clientSubject, groups);
        Callback[] callbacks = new Callback[]{callerCallBack, groupPrincipalCallback};
        handler.handle(callbacks);

    }

    /* 認証に失敗した場合、ユーザ・プリンシパル、グループ情報は null */
    /* if authentication failed, set user principal and group information to null */
    private void onAuthenticationFailer(AuthenticationResponse authResponse, Subject clientSubject) throws IOException, UnsupportedCallbackException {
        // 認証に失敗した場合
        // if authentication failed
        AuthenticationErrorResponse authErrorResponse = (AuthenticationErrorResponse) authResponse;
        CallerPrincipalCallback callerCallBack = new CallerPrincipalCallback(clientSubject, (Principal) null);
        GroupPrincipalCallback groupPrincipalCallback = new GroupPrincipalCallback(clientSubject, null);

        Callback[] callbacks = new Callback[]{callerCallBack, groupPrincipalCallback};
        handler.handle(callbacks);
    }

    private String[] getGroupList(HttpServletRequest request, AzureADUserPrincipal userPrincipal) {
        GraphAPIImpl graph = new GraphAPIImpl();
        graph.init(request);
        return graph.getMemberOfGroup(userPrincipal.getName()).getValue();
//        return graph.getMemberOfGroup(userPrincipal.getName()).getValue();
    }

    /* リフレッシュ・トークンからアクセス・トークンの取得  */
    /* get the access token from the refresh token */
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
    /* get the access token */
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
    /* set authentication information in the session */
    private void setSessionPrincipal(HttpServletRequest httpRequest,
            AzureADUserPrincipal principal) throws Exception {
        httpRequest.getSession().setAttribute(PRINCIPAL_SESSION_NAME, principal);
    }

    /* HTTP セッションから認証結果の取得 */
    /* get the authentication result from the HTTP session */
    public boolean getSessionPrincipal(HttpServletRequest request) {
        return request.getUserPrincipal() != null;
//        return (AzureADUserPrincipal) request.getSession().getAttribute(PRINCIPAL_SESSION_NAME);
    }

    /* リダイレクト URL の取得 */
    /* get the redirect URL */
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
    /* HTTP セッションからにんしょうずみかいなかのチェック */
    /* check whether the HTTP session is authenticated or not */
    // TODO this comment seems misplaced
    public boolean isRedirectedRequestFromAuthServer(HttpServletRequest httpRequest, Map<String, String> params) {
        return httpRequest.getMethod().equalsIgnoreCase("POST")
                && (httpRequest.getParameterMap().containsKey(ERROR)
                || httpRequest.getParameterMap().containsKey(
                        ID_TOKEN) || httpRequest.getParameterMap().containsKey(CODE));
    }


    /* 認証データが含まれるか否かのチェック */
    /* にんしょうデータがふくまれるかいなかのチェック */
    /* check whether authentication data is included or not */
    public boolean containsAuthenticationData(HttpServletRequest httpRequest) {
        Map<String, String[]> map = httpRequest.getParameterMap();

        return httpRequest.getMethod().equalsIgnoreCase("POST")
                && (httpRequest.getParameterMap().containsKey(ERROR)
                || httpRequest.getParameterMap().containsKey(
                        ID_TOKEN) || httpRequest.getParameterMap().containsKey(CODE));
    }


    /* リクエストの URI を取得 */
    /* get the request URI */
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
