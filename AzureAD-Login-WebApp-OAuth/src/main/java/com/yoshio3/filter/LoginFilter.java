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
package com.yoshio3.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.naming.ServiceUnavailableException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.openid.connect.sdk.AuthenticationErrorResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponseParser;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;
import java.util.logging.Logger;

/**
 *
 * @author Yoshio Terada
 */
public class LoginFilter implements Filter {

    private final Logger logger = Logger.getLogger(LoginFilter.class.getName());

    public final static String ERROR = "error";
    public final static String ERROR_DESCRIPTION = "error_description";
    public final static String ERROR_URI = "error_uri";
    public final static String ID_TOKEN = "id_token";
    public final static String CODE = "code";

    public static final String PRINCIPAL_SESSION_NAME = "principal";

    private String authority;
    private String tenant = "";
    private String clientId = "";
    private String secretKey = "";

    /* これらの情報は web.xml の設定情報に AD の管理画面から取得した情報を設定
    <context-param>
        <param-name>authority</param-name>
        <param-value>https://login.microsoftonline.com/</param-value>
    </context-param>
    <context-param>
        <param-name>tenant</param-name>
        <param-value>a4451d54-****-****-****-72bbca1317f5</param-value>
    </context-param>
    <context-param>
        <param-name>client_id</param-name>
        <param-value>542ca1af-****-****-****-6725bf3e3ba9</param-value>
    </context-param>
    <context-param>
        <param-name>secret_key</param-name>
        <param-value>********************OrAlDgakeUFyEulQCFWJYpc=</param-value>
    </context-param>    
    
     */
    @Override
    public void init(FilterConfig config) throws ServletException {
        clientId = config.getServletContext().getInitParameter("client_id");
        authority = config.getServletContext().getInitParameter("authority");
        tenant = config.getServletContext().getInitParameter("tenant");
        secretKey = config.getServletContext().getInitParameter("secret_key");
    }

    @Override
    public void destroy() {
        ;
    }

    /* Servlet Filter */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        //リクエストが HttpServletRequest の場合
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            try {
                String currentUri = getCurrentUri(httpRequest);
                String fullUrl = currentUri
                        + (httpRequest.getQueryString() != null ? "?"
                                + httpRequest.getQueryString() : "");

                // ユーザが認証済みのセッションを保持しているかチェック
                // セッションにプリンシパル情報が入っていれば認証済みと判断
                if (!isAuthenticated(httpRequest)) {
                    if (!containsAuthenticationData(httpRequest)) {
                        //認証しておらず、認証データを持っていない場合
                        // 認証していない場合は Azure AD の認証画面にリダイレクト
                        String redirectUrl = getRedirectUrl(currentUri);
                        
                        httpResponse.setStatus(302);
                        httpResponse
                                .sendRedirect(getRedirectUrl(currentUri));
                        return;
                    } else {
                        //Azure AD の認証後、リダイレクトで返ってきた場合
                        //プリンシパル情報はないので、認証に成功している場合、プリンシパルに追加
                        Map<String, String> params = new HashMap<>();
                        request.getParameterMap().keySet().stream().forEach(key -> {
                            params.put(key,
                                    request.getParameterMap().get(key)[0]);
                        });

                        AuthenticationResponse authResponse = AuthenticationResponseParser
                                .parse(new URI(fullUrl), params);
                        //params の中に error が含まれている場合、AuthenticationErrorResponse
                        //成功の場合 AuthenticationSuccessResponse が返る

                        //認証に成功した場合
                        if (authResponse instanceof AuthenticationSuccessResponse) {
                            AuthenticationSuccessResponse authSuccessResponse = (AuthenticationSuccessResponse) authResponse;

                            AuthenticationResult result = getAccessToken(
                                    authSuccessResponse.getAuthorizationCode(),
                                    currentUri);

                            createSessionPrincipal(httpRequest, result);
                        } else {
                            // 認証に失敗した場合
                            AuthenticationErrorResponse authErrorResponse = (AuthenticationErrorResponse) authResponse;
                            throw new Exception(String.format(
                                    "Request for auth code failed: %s - %s",
                                    authErrorResponse.getErrorObject().getCode(),
                                    authErrorResponse.getErrorObject()
                                    .getDescription()));
                        }
                    }
                } else {
                    //既に認証済みの場合、セッションから認証結果情報を取得
                    AuthenticationResult result
                            = getAuthSessionObject(httpRequest);

                    if (result.getExpiresOnDate().before(new Date())) {
                        //認証の日付が古い場合・リフレッシュトークンからアクセストークン取得
                        result = getAccessTokenFromRefreshToken(
                                result.getRefreshToken(), currentUri);
                    }
                    // セッションにプリンシパルの情報を設定
                    createSessionPrincipal(httpRequest, result);
                }
            } catch (Throwable exc) {
                httpResponse.setStatus(500);
                request.setAttribute("error", exc.getMessage());
                httpResponse.sendRedirect(((HttpServletRequest) request)
                        .getContextPath() + "/error.xhtml");
            }
        }
        chain.doFilter(request, response);
    }

    /* クライアントのクレデンシャルからアクセス・トークンを取得*/
    private AuthenticationResult getAccessTokenFromClientCredentials()
            throws Throwable {
        AuthenticationContext context;
        AuthenticationResult result;
        ExecutorService service = null;
        try {
            service = Executors.newFixedThreadPool(1);
            context = new AuthenticationContext(authority + tenant + "/", true,
                    service);
            Future<AuthenticationResult> future = context.acquireToken(
                    "https://graph.windows.net", new ClientCredential(clientId,
                            secretKey), null);
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

    /* 認証済みのセッションを作成 */
    private void createSessionPrincipal(HttpServletRequest httpRequest,
            AuthenticationResult result) throws Exception {
        httpRequest.getSession().setAttribute(PRINCIPAL_SESSION_NAME, result);
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
    public boolean isAuthenticated(HttpServletRequest request) {
        return request.getSession().getAttribute(PRINCIPAL_SESSION_NAME) != null;
    }

    /* HTTP セッションから認証結果の取得 */
    public  AuthenticationResult getAuthSessionObject(HttpServletRequest request) {
        return (AuthenticationResult) request.getSession().getAttribute(
                PRINCIPAL_SESSION_NAME);
    }

    /* 認証データが含まれるか否かのチェック */
    public  boolean containsAuthenticationData(HttpServletRequest httpRequest) {
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
