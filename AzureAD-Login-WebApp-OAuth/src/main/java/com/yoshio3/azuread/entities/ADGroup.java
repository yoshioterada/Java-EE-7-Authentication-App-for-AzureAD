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

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Yoshio Terada
 */
@XmlRootElement
public class ADGroup {

    @JsonProperty("odata.metadata")
    private String metadata;
    @JsonProperty("odata.type")
    private String odata_type;
    private String objectType;
    private String objectId;
    private String deletionTimestamp;
    private String description;
    private String dirSyncEnabled;
    private String displayName;
    private String lastDirSyncTime;
    private String mail;
    private String mailNickname;
    private String mailEnabled;
    private String onPremisesSecurityIdentifier;
    private String[] provisioningErrors;    // どのような内容が入るかわからないので必要に応じ子クラスを作成する必要あり(単なる文字配列でない場合の可能性)
                                            // どのようなないようがはいるかわからないのでひつようにおうじこクラスをさくせいするひつようあり（たんなるもじはいれつでないばあいのかのうせい）
                                            // Because it's not clear what the content will be, it is necessary to create a subclass accordingly (it is possible that a simple string won't do it)
    private String[] proxyAddresses;        // どのような内容が入るかわからないので必要に応じ子クラスを作成する必要あり(単なる文字配列でない場合の可能性)
                                            // どのようなないようがはいるかわからないのでひつようにおうじこクラスをさくせいするひつようあり（たんなるもじはいれつでないばあいのかのうせい）
                                            // Because it's not clear what the content will be, it is necessary to create a subclass accordingly (it is possible that a simple string won't do it)
    private String securityEnabled;

    /**
     * @return the metadata
     */
    public String getMetadata() {
        return metadata;
    }

    /**
     * @param metadata the metadata to set
     */
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    /**
     * @return the odata_type
     */
    public String getOdata_type() {
        return odata_type;
    }

    /**
     * @param odata_type the odata_type to set
     */
    public void setOdata_type(String odata_type) {
        this.odata_type = odata_type;
    }

    /**
     * @return the objectType
     */
    public String getObjectType() {
        return objectType;
    }

    /**
     * @param objectType the objectType to set
     */
    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    /**
     * @return the objectId
     */
    public String getObjectId() {
        return objectId;
    }

    /**
     * @param objectId the objectId to set
     */
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    /**
     * @return the deletionTimestamp
     */
    public String getDeletionTimestamp() {
        return deletionTimestamp;
    }

    /**
     * @param deletionTimestamp the deletionTimestamp to set
     */
    public void setDeletionTimestamp(String deletionTimestamp) {
        this.deletionTimestamp = deletionTimestamp;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the dirSyncEnabled
     */
    public String getDirSyncEnabled() {
        return dirSyncEnabled;
    }

    /**
     * @param dirSyncEnabled the dirSyncEnabled to set
     */
    public void setDirSyncEnabled(String dirSyncEnabled) {
        this.dirSyncEnabled = dirSyncEnabled;
    }

    /**
     * @return the displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @param displayName the displayName to set
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @return the lastDirSyncTime
     */
    public String getLastDirSyncTime() {
        return lastDirSyncTime;
    }

    /**
     * @param lastDirSyncTime the lastDirSyncTime to set
     */
    public void setLastDirSyncTime(String lastDirSyncTime) {
        this.lastDirSyncTime = lastDirSyncTime;
    }

    /**
     * @return the mail
     */
    public String getMail() {
        return mail;
    }

    /**
     * @param mail the mail to set
     */
    public void setMail(String mail) {
        this.mail = mail;
    }

    /**
     * @return the mailNickname
     */
    public String getMailNickname() {
        return mailNickname;
    }

    /**
     * @param mailNickname the mailNickname to set
     */
    public void setMailNickname(String mailNickname) {
        this.mailNickname = mailNickname;
    }

    /**
     * @return the mailEnabled
     */
    public String getMailEnabled() {
        return mailEnabled;
    }

    /**
     * @param mailEnabled the mailEnabled to set
     */
    public void setMailEnabled(String mailEnabled) {
        this.mailEnabled = mailEnabled;
    }

    /**
     * @return the onPremisesSecurityIdentifier
     */
    public String getOnPremisesSecurityIdentifier() {
        return onPremisesSecurityIdentifier;
    }

    /**
     * @param onPremisesSecurityIdentifier the onPremisesSecurityIdentifier to
     * set
     */
    public void setOnPremisesSecurityIdentifier(String onPremisesSecurityIdentifier) {
        this.onPremisesSecurityIdentifier = onPremisesSecurityIdentifier;
    }

    /**
     * @return the provisioningErrors
     */
    public String[] getProvisioningErrors() {
        return provisioningErrors;
    }

    /**
     * @param provisioningErrors the provisioningErrors to set
     */
    public void setProvisioningErrors(String[] provisioningErrors) {
        this.provisioningErrors = provisioningErrors;
    }

    /**
     * @return the proxyAddresses
     */
    public String[] getProxyAddresses() {
        return proxyAddresses;
    }

    /**
     * @param proxyAddresses the proxyAddresses to set
     */
    public void setProxyAddresses(String[] proxyAddresses) {
        this.proxyAddresses = proxyAddresses;
    }

    /**
     * @return the securityEnabled
     */
    public String getSecurityEnabled() {
        return securityEnabled;
    }

    /**
     * @param securityEnabled the securityEnabled to set
     */
    public void setSecurityEnabled(String securityEnabled) {
        this.securityEnabled = securityEnabled;
    }

    @Override
    public String toString() {
        return "ADGroup{" + "metadata=" + metadata + ", odata_type=" + odata_type + ", objectType=" + objectType + ", objectId=" + objectId + ", deletionTimestamp=" + deletionTimestamp + ", description=" + description + ", dirSyncEnabled=" + dirSyncEnabled + ", displayName=" + displayName + ", lastDirSyncTime=" + lastDirSyncTime + ", mail=" + mail + ", mailNickname=" + mailNickname + ", mailEnabled=" + mailEnabled + ", onPremisesSecurityIdentifier=" + onPremisesSecurityIdentifier + ", provisioningErrors=" + Arrays.toString(provisioningErrors) + ", proxyAddresses=" + Arrays.toString(proxyAddresses) + ", securityEnabled=" + securityEnabled + '}';
    }
}
