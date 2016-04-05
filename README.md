# Java-EE-7-Authentication-App-for-AzureAD

This project is an Azure AD Authentication sample application of Java EE 7 which used the ADAL4J and GraphAPI.

## Overview and Project Structure

### Web App Module (AzureAD-Login-WebApp-OAuth)

The project uses [JASPIC](http://blog.c2b2.co.uk/2015/01/using-jaspic-to-secure-web-application.html)
   * Configured by a WebListener (com.yoshio3.jaspic.common.AzureADSAMRegistrationListener), which registers
      * an authentication provider (com.yoshio3.jaspic.common.AzureADAuthConfigProvider), which in turn provides on request
         * a new instance of the authentication config (com.yoshio3.jaspic.common.AzureADServerAuthConfig), including
            * the singleton-like instance of the authentication module (com.yoshio3.jaspic.AzureADServerAuthModule)
            * a new instance of the callback handler (com.yoshio3.jaspic.AzureADCallbackHandler)

At each secured HTTP request, the validateRequest method on com.yoshio3.jaspic.AzureADServerAuthModule is executed. This implements
the [OAuth2 logic](http://oauth.net/2/), with a set of domain objects that are specific to the Azure AD domain.

Although there is a security filter in the web app, this is not needed with JASPIC.

### SAM Module (AzureADSAMModule)

This project only contains the code necessary to configure a SAM module for authenticating users via Azure AD.

The web project above is an example of application of the SAM module contained here.

## Setting up the Azure AD client application

![](https://c2.staticflickr.com/2/1609/25782167096_5ef75fb1ea_z.jpg)
![](https://c2.staticflickr.com/2/1717/25177762974_63755c37a6_z.jpg)
![](https://c2.staticflickr.com/2/1718/25713163621_21fe096bb3_z.jpg)
![](https://c2.staticflickr.com/2/1635/25713163521_4aac8d88da_z.jpg)
![](https://c2.staticflickr.com/2/1694/25177762764_da14fede68_z.jpg)
![](https://c2.staticflickr.com/2/1645/25782167346_c2d803f3a5_z.jpg)
![](https://c2.staticflickr.com/2/1654/25507573620_f3a5ba8a68_z.jpg)
![](https://c2.staticflickr.com/2/1683/25713163641_24e3db9449_z.jpg)
![](https://c2.staticflickr.com/2/1634/25808251005_2c86ae08e3_z.jpg)
![](https://c2.staticflickr.com/2/1625/25713163891_c86a1f82a0_z.jpg)


![](https://c2.staticflickr.com/2/1642/25177763364_c740e9d515_z.jpg)
![](https://c2.staticflickr.com/2/1702/25181624943_59bd38bd04_z.jpg)
![](https://c2.staticflickr.com/2/1634/25177763534_8e1c47d629_z.jpg)
![](https://c2.staticflickr.com/2/1583/25808251125_81af6e8476_z.jpg)
![](https://c2.staticflickr.com/2/1603/25507573880_e292b9d590_z.jpg)
![](https://c2.staticflickr.com/2/1676/25782167886_85984247e4_z.jpg)
![](https://c2.staticflickr.com/2/1704/25177763654_0c153043b3_z.jpg)
![](https://c2.staticflickr.com/2/1583/25713164061_605714c4f8_z.jpg)
![](https://c2.staticflickr.com/2/1524/25226536833_4897bfe2d3_z.jpg)
![](https://c2.staticflickr.com/2/1653/25687287592_ea2718ec5c_z.jpg)


![](https://c2.staticflickr.com/2/1532/25552393910_dd2ee850e9_z.jpg)
![](https://c2.staticflickr.com/2/1545/25853056805_f453fb5f3a_z.jpg)
![](https://c2.staticflickr.com/2/1550/25757951871_cc187e8638_z.jpg)
![](https://c2.staticflickr.com/2/1517/25552313050_a9f92d7a0c_z.jpg)
![](https://c2.staticflickr.com/2/1486/25552248730_7204e9b8d3_z.jpg)
![](https://c2.staticflickr.com/2/1605/25757883791_219b830a82_z.jpg)
![](https://c2.staticflickr.com/2/1669/25757809671_c585595c83_z.jpg)
![](https://c2.staticflickr.com/2/1705/25808251675_8967a21589_z.jpg)
![](https://c2.staticflickr.com/2/1587/25507574210_227c4d5773_z.jpg)
![](https://c2.staticflickr.com/2/1638/25177764064_7e212cf0ea_z.jpg)


![](https://c2.staticflickr.com/2/1588/25177764134_2f4aeaf5af_z.jpg)
![](https://c2.staticflickr.com/2/1713/25782168436_a65ce79621_z.jpg)
![](https://c2.staticflickr.com/2/1612/25177764144_ee8a0fcf03_z.jpg)
![](https://c2.staticflickr.com/2/1586/25782168466_537ae48f9f_z.jpg)
![](https://c2.staticflickr.com/2/1698/25782168566_b1ed0af3b3_z.jpg)
![](https://c2.staticflickr.com/2/1719/25181625813_6ffc5d7827_z.jpg)
![](https://c2.staticflickr.com/2/1640/25507574520_8538f45e22_z.jpg)
![](https://c2.staticflickr.com/2/1668/25507574550_e066e4e85b_z.jpg)
![](https://c2.staticflickr.com/2/1697/25507574630_846af36330_z.jpg)
![](https://c2.staticflickr.com/2/1625/25687288272_a40af13fa0_z.jpg)


![](https://c2.staticflickr.com/2/1607/25177764454_3820211747_z.jpg)
![](https://c2.staticflickr.com/2/1615/25687288282_3965c7442d_z.jpg)
![](https://c2.staticflickr.com/2/1603/25177764514_a66f999c92_z.jpg)
![](https://c2.staticflickr.com/2/1644/25713164751_cc1023fbd5_z.jpg)
![](https://c2.staticflickr.com/2/1709/25782168766_a0005b7358_z.jpg)
![](https://c2.staticflickr.com/2/1599/25177764574_946081ffdb_z.jpg)
![](https://c2.staticflickr.com/2/1656/25507574850_3b742b332c_z.jpg)
![](https://c2.staticflickr.com/2/1684/25782168846_ed3d30faa1_z.jpg)
![](https://c2.staticflickr.com/2/1591/25782168926_495957acb6_z.jpg)
![](https://c2.staticflickr.com/2/1673/25687288582_b49e76f37c_z.jpg)


![](https://c2.staticflickr.com/2/1668/25687288642_a52bebf626_z.jpg)
![](https://c2.staticflickr.com/2/1485/25222251784_56c5dacf8b_z.jpg)
![](https://c2.staticflickr.com/2/1703/25177764734_baef900911_z.jpg)
![](https://c2.staticflickr.com/2/1619/25507575030_6e2dc2a694_z.jpg)
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
