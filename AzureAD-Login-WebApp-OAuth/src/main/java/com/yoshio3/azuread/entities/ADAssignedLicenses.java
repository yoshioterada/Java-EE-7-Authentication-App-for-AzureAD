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
package com.yoshio3.azuread.entities;

import java.util.Arrays;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Yoshio Terada
 */
@XmlRootElement
public class ADAssignedLicenses {
    private String[] disabledPlans;
    private String skuId;

    /**
     * @return the disabledPlans
     */
    public String[] getDisabledPlans() {
        return disabledPlans;
    }

    /**
     * @param disabledPlans the disabledPlans to set
     */
    public void setDisabledPlans(String[] disabledPlans) {
        this.disabledPlans = disabledPlans;
    }

    /**
     * @return the skuId
     */
    public String getSkuId() {
        return skuId;
    }

    /**
     * @param skuId the skuId to set
     */
    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    @Override
    public String toString() {
        return "ADAssignedLicenses{" + "disabledPlans=" + Arrays.toString(disabledPlans) + ", skuId=" + skuId + '}';
    }
}
