# Java-EE-7-Authentication-App-for-AzureAD

This project is an Azure AD Authentication sample application of Java EE 7 which used the ADAL4J and GraphAPI.

## Overview and Project Structure

### Web App Module (AzureAD-Login-WebApp-OAuth)

The project uses [JASPIC](http://blog.c2b2.co.uk/2015/01/using-jaspic-to-secure-web-application.html)
   * Configured by a WebListener (com.yoshio3.jaspic.common.AzureADSAMRegistrationListener), which registers
      * an authentication provider (com.yoshio3.jaspic.common.AzureADAuthConfigProvider), which in turn provides on
      request
         * a new instance of the authentication config (com.yoshio3.jaspic.common.AzureADServerAuthConfig), including
            * the singleton-like instance of the authentication module (com.yoshio3.jaspic.AzureADServerAuthModule)
            * a new instance of the callback handler (com.yoshio3.jaspic.AzureADCallbackHandler)

At each secured HTTP request, the config provider returns an instance of
com.yoshio3.jaspic.common.AzureADServerAuthContext, which calls the the validateRequest method on
com.yoshio3.jaspic.AzureADServerAuthModule. This implements the [OAuth2 logic](http://oauth.net/2/), with a set of
domain objects that are specific to the Azure AD domain.

#### Configure the login module in your app server

Once authentication via OAuth2 is done, the system will hand over to the login module (com.yoshio3.jaspic.AzureADLoginModule), which must be configured
under Glassfish. **You must add**

    AzureAD-Login {
        com.yoshio3.jaspic.AzureADLoginModule required;
    };

**to the login.conf file inside the config directory of your Glassfish domain**.

Although there is a security filter in the web app, this is not needed with JASPIC.

### SAM Module (AzureADSAMModule)

This project only contains the code necessary to configure a SAM module for authenticating users via Azure AD.

The web project above is an example of application of the SAM module contained here.

## Setting up the Azure AD client application

Select and open the Active Directory service you want to access with your client<br>
![](https://c2.staticflickr.com/2/1645/25782167346_c2d803f3a5_z.jpg)

Enable Azure AD Premium on your directory (30 day trial available)<br>
![](https://c2.staticflickr.com/2/1683/25713163641_24e3db9449_z.jpg)
![](https://c2.staticflickr.com/2/1634/25808251005_2c86ae08e3_z.jpg)
![](https://c2.staticflickr.com/2/1625/25713163891_c86a1f82a0_z.jpg)
![](https://c2.staticflickr.com/2/1702/25181624943_59bd38bd04_z.jpg)

Create a new client application for your Active Directory service<br>
![](https://c2.staticflickr.com/2/1634/25177763534_8e1c47d629_z.jpg)
![](https://c2.staticflickr.com/2/1583/25808251125_81af6e8476_z.jpg)
![](https://c2.staticflickr.com/2/1603/25507573880_e292b9d590_z.jpg)

Enter the URLs associated to your web application<br>
![](https://c2.staticflickr.com/2/1676/25782167886_85984247e4_z.jpg)

The client application configuration will be created and added to the Active Directory<br>
![](https://c2.staticflickr.com/2/1704/25177763654_0c153043b3_z.jpg)

Get the client application parameters for the web.xml configuration<br>
![](https://c2.staticflickr.com/2/1583/25713164061_605714c4f8_z.jpg)
<br>Copy the Client ID and paste in your web.xml file<br>
![](https://c2.staticflickr.com/2/1524/25226536833_4897bfe2d3_z.jpg)
![](https://c2.staticflickr.com/2/1653/25687287592_ea2718ec5c_z.jpg)
<br>Generate a secret key (valid for either one or two years), save it then copy it into your web.xml file<br>
![](https://c2.staticflickr.com/2/1532/25552393910_dd2ee850e9_z.jpg)
![](https://c2.staticflickr.com/2/1545/25853056805_f453fb5f3a_z.jpg)
![](https://c2.staticflickr.com/2/1550/25757951871_cc187e8638_z.jpg)
![](https://c2.staticflickr.com/2/1517/25552313050_a9f92d7a0c_z.jpg)
<br>This is where your parameters should go in your code<br>
![](https://c2.staticflickr.com/2/1485/25222251784_56c5dacf8b_z.jpg)

Open the endpoints view<br>
![](https://c2.staticflickr.com/2/1486/25552248730_7204e9b8d3_z.jpg)
<br>You can get the tenant ID and OAuth 2 URLs for your web.xml from here<br>
![](https://c2.staticflickr.com/2/1605/25757883791_219b830a82_z.jpg)
![](https://c2.staticflickr.com/2/1669/25757809671_c585595c83_z.jpg)

Set the permissions for the application on the Configure page<br>
![](https://c2.staticflickr.com/2/1603/25177764514_a66f999c92_z.jpg)
![](https://c2.staticflickr.com/2/1709/25782168766_a0005b7358_z.jpg)
![](https://c2.staticflickr.com/2/1599/25177764574_946081ffdb_z.jpg)
<br>Remember to save!<br>
![](https://c2.staticflickr.com/2/1656/25507574850_3b742b332c_z.jpg)

Add the Microsoft Graph application<br>
![](https://c2.staticflickr.com/2/1684/25782168846_ed3d30faa1_z.jpg)
<br>Add permissions to it as per the picture<br>
![](https://c2.staticflickr.com/2/1591/25782168926_495957acb6_z.jpg)
![](https://c2.staticflickr.com/2/1673/25687288582_b49e76f37c_z.jpg)
<br>And save!<br>
![](https://c2.staticflickr.com/2/1668/25687288642_a52bebf626_z.jpg)

Here is the application in action<br>

![](https://c2.staticflickr.com/2/1673/25782169066_e47d9927ab_z.jpg)
![](https://c2.staticflickr.com/2/1611/25808252375_cb1a18a0ae_z.jpg)
![](https://c2.staticflickr.com/2/1677/25687288852_9f4259eba8_z.jpg)
![](https://c2.staticflickr.com/2/1651/25808252455_4072d90235_z.jpg)
![](https://c2.staticflickr.com/2/1654/25181626293_a77f8e74a3_z.jpg)
![](https://c2.staticflickr.com/2/1581/25713165151_21e0932211_z.jpg)

![](https://c2.staticflickr.com/2/1642/25177764884_9ab22e3476_z.jpg)
![](https://c2.staticflickr.com/2/1618/25177765004_18aa9e72ae_z.jpg)
![](https://c2.staticflickr.com/2/1629/25177765024_e77364f83e_z.jpg)
![](https://c2.staticflickr.com/2/1616/25713165291_d0304e581d_z.jpg)
![](https://c2.staticflickr.com/2/1679/25507575250_2afbbfb68d_z.jpg)
![](https://c2.staticflickr.com/2/1590/25177765094_0ba999be31_z.jpg)
![](https://c2.staticflickr.com/2/1674/25808252705_e3eb352d39_z.jpg)
![](https://c2.staticflickr.com/2/1618/25782169416_149ebe707e_z.jpg)
![](https://c2.staticflickr.com/2/1592/25852658415_73f98493f4_z.jpg)
![](https://c2.staticflickr.com/2/1701/25713165431_88819de3b3_z.jpg)

![](https://c2.staticflickr.com/2/1698/25713165441_0ea8aa9122_z.jpg)
![](https://c2.staticflickr.com/2/1468/25226054693_586c7423f0_z.jpg)
![](https://c2.staticflickr.com/2/1643/25181626473_5700ba577f_z.jpg)
![](https://c2.staticflickr.com/2/1643/25782169616_28cd9c89fc_z.jpg)
![](https://c2.staticflickr.com/2/1616/25181626503_2d0a46f722_z.jpg)
![](https://c2.staticflickr.com/2/1699/25181626533_10eae5fc2e_z.jpg)
![](https://c2.staticflickr.com/2/1652/25177765374_f76ea7acfa_z.jpg)
![](https://c2.staticflickr.com/2/1601/25507575560_ee00aced10_z.jpg)
