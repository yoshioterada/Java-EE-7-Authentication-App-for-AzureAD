/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.yoshio3.jaspic;

import com.microsoft.aad.adal4j.AuthenticationResult;
import java.io.Serializable;
import java.security.Principal;

/**
 *
 * @author Yoshio Terada
 */
public class AzureADUserPrincipal implements Principal,Serializable{
    
    AuthenticationResult azureadResult;

    public AzureADUserPrincipal(AuthenticationResult azureadResult){
        this.azureadResult = azureadResult;
    }
    
    @Override
    public String getName() {
        return azureadResult.getUserInfo().getUniqueId();
    }
    
    public AuthenticationResult getAuthenticationResult(){
        return this.azureadResult;
    }
}
