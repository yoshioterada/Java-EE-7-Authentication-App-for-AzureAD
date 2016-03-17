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

import java.io.IOException;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.message.callback.CallerPrincipalCallback;
import javax.security.auth.message.callback.GroupPrincipalCallback;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Yoshio Terada
 */
public class AzureADCallbackHandler implements CallbackHandler {

    private final ThreadLocal<CallerPrincipalCallback> callerPrincipals = new ThreadLocal<>();
    private final ThreadLocal<GroupPrincipalCallback> groupPrincipals = new ThreadLocal<>();
    private final HttpServletRequest request;
    private final Subject clientSubject;
    private final AzureADUserPrincipal userPrincipal;

    public AzureADCallbackHandler(Subject clientSubject, HttpServletRequest request, AzureADUserPrincipal userPrincipal) {
        this.request = request;
        this.clientSubject = clientSubject;
        this.userPrincipal = userPrincipal;
    }

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            if (callback instanceof CallerPrincipalCallback) {
                callback = new CallerPrincipalCallback(clientSubject, userPrincipal);
            } else {
                throw new UnsupportedCallbackException(callback);
            }
        }
    }
}
