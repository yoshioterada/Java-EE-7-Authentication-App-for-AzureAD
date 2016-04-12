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

import java.util.Map;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.config.AuthConfigFactory;
import javax.security.auth.message.config.AuthConfigProvider;
import javax.security.auth.message.config.ClientAuthConfig;
import javax.security.auth.message.config.ServerAuthConfig;
import javax.security.auth.message.module.ServerAuthModule;

/**
 *
 * @author Yoshio Terada
 */
public class AzureADAuthConfigProvider implements AuthConfigProvider {

    private final static String CALLBACK_CLASS_NAME = "com.yoshio3.jaspic.AzureADCallbackHandler";    
    private Map<String, String> providerProperties;
    private ServerAuthModule serverAuthModule;
    private MessagePolicy requestPolicy;
            private MessagePolicy responsePolicy;
            private Map<String, String> options;

    public AzureADAuthConfigProvider(ServerAuthModule serverAuthModule,MessagePolicy requestPolicy, MessagePolicy responsePolicy,Map<String, String> options) {
        this.serverAuthModule = serverAuthModule;
        this.requestPolicy =requestPolicy;
        this.responsePolicy = responsePolicy;
        this.options = options;
        
    }

    /**
     * Constructor with signature and implementation that's required by API.
     * 
     * @param properties
     * @param factory
     */

    public AzureADAuthConfigProvider(Map<String, String> properties, AuthConfigFactory factory) {
        this.providerProperties = properties;

        // API requires self registration if factory is provided. Not clear
        // where the "layer" (2nd parameter)
        // and especially "appContext" (3rd parameter) values have to come from
        // at this place.
        if (factory != null) {
            factory.registerConfigProvider(this, null, null, "Auto registration");
        }
    }

    /**
     * The actual factory method that creates the factory used to eventually obtain the delegate for a SAM.
     */
    @Override
    public ServerAuthConfig getServerAuthConfig(String layer, String appContext, CallbackHandler handler) throws AuthException,
        SecurityException {
        return new AzureADServerAuthConfig(
                requestPolicy, responsePolicy, options,
                layer, appContext, handler == null ? createDefaultCallbackHandler() : handler,
            providerProperties, serverAuthModule);
    }

    @Override
    public ClientAuthConfig getClientAuthConfig(String layer, String appContext, CallbackHandler handler) throws AuthException,
        SecurityException {
        return null;
    }

    @Override
    public void refresh() {
    }

    /**
     * Creates a default callback handler via the system property "authconfigprovider.client.callbackhandler", as seemingly
     * required by the API (API uses wording "may" create default handler).
     * TODO: Isn't "authconfigprovider.client.callbackhandler" JBoss specific?
     * 
     * @return
     * @throws AuthException
     */

    private CallbackHandler createDefaultCallbackHandler() throws AuthException {
        //TODO web.xml 経由で取得するのがベター
        //TODO better to obtain this via web.xml
        String callBackClassName = CALLBACK_CLASS_NAME;
        if (callBackClassName == null) {
            throw new AuthException("No default handler set via system property: " + callBackClassName);
        }

        try {
            return (CallbackHandler) Thread.currentThread().getContextClassLoader().loadClass(callBackClassName).newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new AuthException(e.getMessage());
        }
    }
}
