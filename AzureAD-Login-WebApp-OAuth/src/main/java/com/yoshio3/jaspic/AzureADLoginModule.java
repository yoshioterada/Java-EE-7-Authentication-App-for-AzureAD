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

import com.microsoft.aad.adal4j.AuthenticationResult;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.message.callback.CallerPrincipalCallback;
import javax.security.auth.message.callback.GroupPrincipalCallback;
import javax.security.auth.spi.LoginModule;

/**
 *
 * @author yoterada
 */
public class AzureADLoginModule implements LoginModule {

    private Subject subject;
    private CallbackHandler callbackHandler;
    private Map<String, ?> sharedState;
    private Map<String, ?> options;
    private AuthenticationResult azureadResult;

    private Set<Principal> principals;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = sharedState;
        this.options = options;
    }

    @Override
    public boolean login() throws LoginException {
        if (callbackHandler == null) {
            throw new LoginException("No CallbackHandler specified");
        }
        principals = subject.getPrincipals();

        List<Callback> data = new ArrayList<>();
        principals.stream().forEach((princ) -> {
            if (princ instanceof AzureADUserPrincipal) {
                CallerPrincipalCallback callerPrincipalCallback = new CallerPrincipalCallback(subject, princ);
                data.add(callerPrincipalCallback);
            }else if(princ instanceof GroupPrincipalCallback){
                GroupPrincipalCallback groupPrincipalCallback = new GroupPrincipalCallback(subject, ((GroupPrincipalCallback) princ).getGroups());                
                data.add(groupPrincipalCallback);
            }
        });
        Callback[] callbacks = data.toArray(new Callback[0]);
        try {
            callbackHandler.handle(callbacks);
        } catch (IOException | UnsupportedCallbackException ex) {
            Logger.getLogger(AzureADLoginModule.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    @Override
    public boolean commit() throws LoginException {
        subject.getPrincipals().addAll(principals);
        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        return true;
    }

    @Override
    public boolean logout() throws LoginException {
        subject.getPrincipals().removeAll(principals);
        return true;
    }
}
