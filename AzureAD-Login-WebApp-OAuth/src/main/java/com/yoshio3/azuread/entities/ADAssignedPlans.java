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

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Yoshio Terada
 */
@XmlRootElement
public class ADAssignedPlans {
    private String assignedTimestamp;
    private String capabilityStatus;
    private String service;
    private String servicePlanId;

    /**
     * @return the assignedTimestamp
     */
    public String getAssignedTimestamp() {
        return assignedTimestamp;
    }

    /**
     * @param assignedTimestamp the assignedTimestamp to set
     */
    public void setAssignedTimestamp(String assignedTimestamp) {
        this.assignedTimestamp = assignedTimestamp;
    }

    /**
     * @return the capabilityStatus
     */
    public String getCapabilityStatus() {
        return capabilityStatus;
    }

    /**
     * @param capabilityStatus the capabilityStatus to set
     */
    public void setCapabilityStatus(String capabilityStatus) {
        this.capabilityStatus = capabilityStatus;
    }

    /**
     * @return the service
     */
    public String getService() {
        return service;
    }

    /**
     * @param service the service to set
     */
    public void setService(String service) {
        this.service = service;
    }

    /**
     * @return the servicePlanId
     */
    public String getServicePlanId() {
        return servicePlanId;
    }

    /**
     * @param servicePlanId the servicePlanId to set
     */
    public void setServicePlanId(String servicePlanId) {
        this.servicePlanId = servicePlanId;
    }

    @Override
    public String toString() {
        return "ADAssignedPlans{" + "assignedTimestamp=" + assignedTimestamp + ", capabilityStatus=" + capabilityStatus + ", service=" + service + ", servicePlanId=" + servicePlanId + '}';
    }

}
