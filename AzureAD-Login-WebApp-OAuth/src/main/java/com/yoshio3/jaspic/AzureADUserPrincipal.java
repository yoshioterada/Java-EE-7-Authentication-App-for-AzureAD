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
