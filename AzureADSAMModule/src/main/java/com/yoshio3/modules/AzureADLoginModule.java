/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.yoshio3.modules;

import com.microsoft.aad.adal4j.AuthenticationResult;
import java.security.Principal;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

/**
 *
 * @author yoterada
 */
public class AzureADLoginModule implements LoginModule{

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
        System.out.println("AzureADLoginModule:" + subject + ":" + callbackHandler);
    }

    @Override
    public boolean login() throws LoginException {
        if (callbackHandler == null) {
            throw new LoginException("No CallbackHandler specified");
        }
        
//        CallerPrincipalCallback callBack = new CallerPrincipalCallback(subject, p);
        System.out.println("AzureADLoginModule login() is called."); 
        System.out.println("AzureADLoginModule login() subject size :" + subject.getPrincipals().size()); 
        principals = subject.getPrincipals();
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
        return true;
    }
}
