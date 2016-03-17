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
package com.yoshio3.jaspic.common;

import com.yoshio3.jaspic.AzureADServerAuthModule;
import java.util.HashMap;
import java.util.Map;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.config.AuthConfigFactory;
import javax.security.auth.message.module.ServerAuthModule;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 *
 * @author Yoshio Terada
 */
@WebListener
public class AzureADSAMRegistrationListener implements ServletContextListener {
    
    private final static String CLIENT_ID = "client_id";
    private final static String AUTHORITY = "authority";
    private final static String TENANT = "tenant";
    private final static String SECRET_KEY = "secret_key";
    private final static String LOGIN_CONTEXT_NAME = "javax.security.auth.login.LoginContext";
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        Map<String,String> options = getInitParameter(context);
        AzureADServerAuthModule azureADServerAuthModule = new AzureADServerAuthModule(options);
        registerSAM(context, azureADServerAuthModule,azureADServerAuthModule.getRequestPolicy(),azureADServerAuthModule.getResponsePolicy(),azureADServerAuthModule.getOptions());
    }    

    /* 通常コンテナで設定する内容をプログラム内で記載 ServerAuthModule のoptionsに設定する内容*/
    private Map<String,String> getInitParameter(ServletContext context){
        String clientId = context.getInitParameter(CLIENT_ID);
        String authority =context.getInitParameter(AUTHORITY);
        String tenant = context.getInitParameter(TENANT);
        String secretKey = context.getInitParameter(SECRET_KEY);
        String loginContextName = context.getInitParameter(LOGIN_CONTEXT_NAME);
        
        Map<String, String> options = new HashMap<>();
        options.put(CLIENT_ID, clientId);
        options.put(AUTHORITY, authority);        
        options.put(TENANT, tenant);        
        options.put(SECRET_KEY, secretKey);        
        options.put(LOGIN_CONTEXT_NAME, loginContextName);
        return options;
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

    public static void registerSAM(ServletContext context, ServerAuthModule serverAuthModule, MessagePolicy requestPolicy, MessagePolicy responsePolicy,Map<String, String> options) {
        AuthConfigFactory.getFactory().
                registerConfigProvider(
                        new AzureADAuthConfigProvider(serverAuthModule,requestPolicy,responsePolicy,options), 
                        "HttpServlet",
                        getAppContextID(context), 
                        "AzureAD authentication config provider");
    }

    public static String getAppContextID(ServletContext context) {
        return context.getVirtualServerName() + " " + context.getContextPath();
    }
}
