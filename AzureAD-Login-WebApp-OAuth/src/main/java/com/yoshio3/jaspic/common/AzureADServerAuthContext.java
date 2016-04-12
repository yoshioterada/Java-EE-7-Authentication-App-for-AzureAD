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
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.config.ServerAuthContext;
import javax.security.auth.message.module.ServerAuthModule;

/**
 *
 * @author Yoshio Terada
 */
public class AzureADServerAuthContext implements ServerAuthContext {

    private final ServerAuthModule serverAuthModule;

    public AzureADServerAuthContext(MessagePolicy requestPolicy, MessagePolicy responsePolicy, CallbackHandler handler, Map<String, String> options, ServerAuthModule serverAuthModule) throws AuthException {
        this.serverAuthModule = serverAuthModule;
//        serverAuthModule.initialize(null, null, handler, Collections.<String, String> emptyMap());
        serverAuthModule.initialize(requestPolicy, responsePolicy, handler, options);
    }

    private static final String IS_MANDATORY = "javax.security.auth.message.MessagePolicy.isMandatory";

    @Override
    public AuthStatus validateRequest(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject)
            throws AuthException {
        // web.xml の<web-resource-collection><url-pattern>のパターンの内容で認証
        // の有無を決定(パターンにマッチしない場合認証は不要)
        if (!Boolean.valueOf((String) messageInfo.getMap().get(IS_MANDATORY))) {
            return AuthStatus.SUCCESS;
        } else {
            return serverAuthModule.validateRequest(messageInfo, clientSubject, serviceSubject);
        }
    }

    @Override
    public AuthStatus secureResponse(MessageInfo messageInfo, Subject serviceSubject) throws AuthException {
        return serverAuthModule.secureResponse(messageInfo, serviceSubject);
    }

    @Override
    public void cleanSubject(MessageInfo messageInfo, Subject subject) throws AuthException {
        serverAuthModule.cleanSubject(messageInfo, subject);
    }
}
