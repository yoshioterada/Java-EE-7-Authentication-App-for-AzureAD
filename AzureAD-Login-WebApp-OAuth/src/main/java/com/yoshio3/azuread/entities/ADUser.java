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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Yoshio Terada
 */
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class ADUser {

    @JsonProperty("odata.metadata")
    private String metadata;
    @JsonProperty("odata.type")
    private String odata_type;
    private String objectType;
    private String objectId;
    private String deletionTimestamp;
    private String accountEnabled;
    private ADAssignedLicenses[] assignedLicenses;
    private ADAssignedPlans[] assignedPlans;
    private String city;
    private String companyName;
    private String country;
    private String creationType;
    private String department;
    private String dirSyncEnabled;
    private String displayName;
    private String facsimileTelephoneNumber;
    private String givenName;
    private String immutableId;
    private String jobTitle;
    private String lastDirSyncTime;
    private String mail;
    private String mailNickname;
    private String mobile;
    private String onPremisesSecurityIdentifier;
    private String[] otherMails;    // どのような内容が入るかわからないので必要に応じ子クラスを作成する必要あり(単なる文字配列でない場合の可能性)
                                    // どのようなないようがはいるかわからないのでひつようにおうじこクラスをさくせいするひつようあり（たんなるもじはいれつでないばあいのかのうせい）
                                    // Because it's not clear what the content will be, it is necessary to create a subclass accordingly (it is possible that a simple string won't do it)
    private String passwordPolicies;
    //private String passwordProfile;
    private String physicalDeliveryOfficeName;
    private String postalCode;
    private String preferredLanguage;
    private String[] provisionedPlans;  // どのような内容が入るかわからないので必要に応じ子クラスを作成する必要あり(単なる文字配列でない場合の可能性)
                                        // どのようなないようがはいるかわからないのでひつようにおうじこクラスをさくせいするひつようあり（たんなるもじはいれつでないばあいのかのうせい）
                                        // Because it's not clear what the content will be, it is necessary to create a subclass accordingly (it is possible that a simple string won't do it)
    private String[] provisioningErrors;    // どのような内容が入るかわからないので必要に応じ子クラスを作成する必要あり(単なる文字配列でない場合の可能性)
                                            // どのようなないようがはいるかわからないのでひつようにおうじこクラスをさくせいするひつようあり（たんなるもじはいれつでないばあいのかのうせい）
                                            // Because it's not clear what the content will be, it is necessary to create a subclass accordingly (it is possible that a simple string won't do it)
    private String[] proxyAddresses;    // どのような内容が入るかわからないので必要に応じ子クラスを作成する必要あり(単なる文字配列でない場合の可能性)
                                        // どのようなないようがはいるかわからないのでひつようにおうじこクラスをさくせいするひつようあり（たんなるもじはいれつでないばあいのかのうせい）
                                        // Because it's not clear what the content will be, it is necessary to create a subclass accordingly (it is possible that a simple string won't do it)
    private String sipProxyAddress;
    private String state;
    private String streetAddress;
    private String surname;
    private String telephoneNumber;
    private String usageLocation;
    private String userPrincipalName;
    private String userType;
    private String[] signInNames;   // どのような内容が入るかわからないので必要に応じ子クラスを作成する必要あり
                                    // どのようなないようがはいるかわからないのでひつようにおうじこクラスをさくせいするひつようあり
                                    // Because it's not clear what the content will be, it is necessary to create a subclass accordingly
    private String isCompromised;


    public ADUser(){
        
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
     * @return the accountEnabled
     */
    public String getAccountEnabled() {
        return accountEnabled;
    }

    /**
     * @param accountEnabled the accountEnabled to set
     */
    public void setAccountEnabled(String accountEnabled) {
        this.accountEnabled = accountEnabled;
    }


    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return the companyName
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * @param companyName the companyName to set
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    /**
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country the country to set
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * @return the creationType
     */
    public String getCreationType() {
        return creationType;
    }

    /**
     * @param creationType the creationType to set
     */
    public void setCreationType(String creationType) {
        this.creationType = creationType;
    }

    /**
     * @return the department
     */
    public String getDepartment() {
        return department;
    }

    /**
     * @param department the department to set
     */
    public void setDepartment(String department) {
        this.department = department;
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
     * @return the facsimileTelephoneNumber
     */
    public String getFacsimileTelephoneNumber() {
        return facsimileTelephoneNumber;
    }

    /**
     * @param facsimileTelephoneNumber the facsimileTelephoneNumber to set
     */
    public void setFacsimileTelephoneNumber(String facsimileTelephoneNumber) {
        this.facsimileTelephoneNumber = facsimileTelephoneNumber;
    }

    /**
     * @return the givenName
     */
    public String getGivenName() {
        return givenName;
    }

    /**
     * @param givenName the givenName to set
     */
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    /**
     * @return the immutableId
     */
    public String getImmutableId() {
        return immutableId;
    }

    /**
     * @param immutableId the immutableId to set
     */
    public void setImmutableId(String immutableId) {
        this.immutableId = immutableId;
    }

    /**
     * @return the jobTitle
     */
    public String getJobTitle() {
        return jobTitle;
    }

    /**
     * @param jobTitle the jobTitle to set
     */
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
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
     * @return the mobile
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * @param mobile the mobile to set
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
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
     * @return the otherMails
     */
    public String[] getOtherMails() {
        return otherMails;
    }

    /**
     * @param otherMails the otherMails to set
     */
    public void setOtherMails(String[] otherMails) {
        this.otherMails = otherMails;
    }

    /**
     * @return the passwordPolicies
     */
    public String getPasswordPolicies() {
        return passwordPolicies;
    }

    /**
     * @param passwordPolicies the passwordPolicies to set
     */
    public void setPasswordPolicies(String passwordPolicies) {
        this.passwordPolicies = passwordPolicies;
    }

    /**
     * @return the passwordProfile
     */
    //public String getPasswordProfile() {
    //    return passwordProfile;
    //}

    /**
     * @param passwordProfile the passwordProfile to set
     */
    //public void setPasswordProfile(String passwordProfile) {
    //    this.passwordProfile = passwordProfile;
    //}

    /**
     * @return the physicalDeliveryOfficeName
     */
    public String getPhysicalDeliveryOfficeName() {
        return physicalDeliveryOfficeName;
    }

    /**
     * @param physicalDeliveryOfficeName the physicalDeliveryOfficeName to set
     */
    public void setPhysicalDeliveryOfficeName(String physicalDeliveryOfficeName) {
        this.physicalDeliveryOfficeName = physicalDeliveryOfficeName;
    }

    /**
     * @return the postalCode
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * @param postalCode the postalCode to set
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * @return the preferredLanguage
     */
    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    /**
     * @param preferredLanguage the preferredLanguage to set
     */
    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    /**
     * @return the provisionedPlans
     */
    public String[] getProvisionedPlans() {
        return provisionedPlans;
    }

    /**
     * @param provisionedPlans the provisionedPlans to set
     */
    public void setProvisionedPlans(String[] provisionedPlans) {
        this.provisionedPlans = provisionedPlans;
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
     * @return the sipProxyAddress
     */
    public String getSipProxyAddress() {
        return sipProxyAddress;
    }

    /**
     * @param sipProxyAddress the sipProxyAddress to set
     */
    public void setSipProxyAddress(String sipProxyAddress) {
        this.sipProxyAddress = sipProxyAddress;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return the streetAddress
     */
    public String getStreetAddress() {
        return streetAddress;
    }

    /**
     * @param streetAddress the streetAddress to set
     */
    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    /**
     * @return the surname
     */
    public String getSurname() {
        return surname;
    }

    /**
     * @param surname the surname to set
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * @return the telephoneNumber
     */
    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    /**
     * @param telephoneNumber the telephoneNumber to set
     */
    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    /**
     * @return the usageLocation
     */
    public String getUsageLocation() {
        return usageLocation;
    }

    /**
     * @param usageLocation the usageLocation to set
     */
    public void setUsageLocation(String usageLocation) {
        this.usageLocation = usageLocation;
    }

    /**
     * @return the userPrincipalName
     */
    public String getUserPrincipalName() {
        return userPrincipalName;
    }

    /**
     * @param userPrincipalName the userPrincipalName to set
     */
    public void setUserPrincipalName(String userPrincipalName) {
        this.userPrincipalName = userPrincipalName;
    }

    /**
     * @return the userType
     */
    public String getUserType() {
        return userType;
    }

    /**
     * @param userType the userType to set
     */
    public void setUserType(String userType) {
        this.userType = userType;
    }

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
     * @return the signInNames
     */
    public String[] getSignInNames() {
        return signInNames;
    }

    /**
     * @param signInNames the signInNames to set
     */
    public void setSignInNames(String[] signInNames) {
        this.signInNames = signInNames;
    }

    /**
     * @return the isCompromised
     */
    public String getIsCompromised() {
        return isCompromised;
    }

    /**
     * @param isCompromised the isCompromised to set
     */
    public void setIsCompromised(String isCompromised) {
        this.isCompromised = isCompromised;
    }

    /**
     * @return the assignedLicenses
     */
    public ADAssignedLicenses[] getAssignedLicenses() {
        return assignedLicenses;
    }

    /**
     * @param assignedLicenses the assignedLicenses to set
     */
    public void setAssignedLicenses(ADAssignedLicenses[] assignedLicenses) {
        this.assignedLicenses = assignedLicenses;
    }

    /**
     * @return the assignedPlans
     */
    public ADAssignedPlans[] getAssignedPlans() {
        return assignedPlans;
    }

    /**
     * @param assignedPlans the assignedPlans to set
     */
    public void setAssignedPlans(ADAssignedPlans[] assignedPlans) {
        this.assignedPlans = assignedPlans;
    }

    @Override
    public String toString() {
        return "ADUser{" + "metadata=" + metadata + ", odata_type=" + odata_type + ", objectType=" + objectType + ", objectId=" + objectId + ", deletionTimestamp=" + deletionTimestamp + ", accountEnabled=" + accountEnabled + ", assignedLicenses=" + Arrays.toString(assignedLicenses) + ", assignedPlans=" + Arrays.toString(assignedPlans) + ", city=" + city + ", companyName=" + companyName + ", country=" + country + ", creationType=" + creationType + ", department=" + department + ", dirSyncEnabled=" + dirSyncEnabled + ", displayName=" + displayName + ", facsimileTelephoneNumber=" + facsimileTelephoneNumber + ", givenName=" + givenName + ", immutableId=" + immutableId + ", jobTitle=" + jobTitle + ", lastDirSyncTime=" + lastDirSyncTime + ", mail=" + mail + ", mailNickname=" + mailNickname + ", mobile=" + mobile + ", onPremisesSecurityIdentifier=" + onPremisesSecurityIdentifier + ", otherMails=" + Arrays.toString(otherMails) + ", passwordPolicies=" + /* passwordPolicies + ", passwordProfile=" + passwordProfile + */ ", physicalDeliveryOfficeName=" + physicalDeliveryOfficeName + ", postalCode=" + postalCode + ", preferredLanguage=" + preferredLanguage + ", provisionedPlans=" + Arrays.toString(provisionedPlans) + ", provisioningErrors=" + Arrays.toString(provisioningErrors) + ", proxyAddresses=" + Arrays.toString(proxyAddresses) + ", sipProxyAddress=" + sipProxyAddress + ", state=" + state + ", streetAddress=" + streetAddress + ", surname=" + surname + ", telephoneNumber=" + telephoneNumber + ", usageLocation=" + usageLocation + ", userPrincipalName=" + userPrincipalName + ", userType=" + userType + ", signInNames=" + Arrays.toString(signInNames) + ", isCompromised=" + isCompromised + '}';
    }
}
