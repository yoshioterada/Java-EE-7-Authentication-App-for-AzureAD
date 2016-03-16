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
package com.yoshio3.azuread.cdis.extensionOfPF;

import com.yoshio3.azuread.entities.ADUser;
import java.util.List;
import javax.faces.model.ListDataModel;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author Yoshio Terada
 */
public class UserSelectionModel extends ListDataModel<ADUser> implements SelectableDataModel<ADUser>{
    public UserSelectionModel(){}
    
    public UserSelectionModel(List<ADUser> data){
            super(data);
    }
    
    
    @Override
    public Object getRowKey(ADUser user) {
        return user.getObjectId();
    }

    @Override
    public ADUser getRowData(String rowKey) {
        List<ADUser> users = (List<ADUser>) getWrappedData();
        ADUser usr = null;
        for(ADUser user : users){
            if(user.getObjectId().equals(rowKey)){
                usr = user; 
            }
        }
        return usr;
    }
}
