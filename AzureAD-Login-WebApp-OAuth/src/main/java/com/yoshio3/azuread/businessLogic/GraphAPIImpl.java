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
package com.yoshio3.azuread.businessLogic;

import com.yoshio3.azuread.entities.ADUsers;
import com.yoshio3.azuread.entities.ADUser;
import com.yoshio3.azuread.entities.ADGroups;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.yoshio3.azuread.entities.ADGroup;
import com.yoshio3.azuread.entities.ADUserMemberOfGroups;
import com.yoshio3.jaspic.AzureADUserPrincipal;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.security.PermitAll;
import javax.enterprise.context.Dependent;
import javax.faces.context.FacesContext;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.jackson.JacksonFeature;

/*
 参考情報
 Graph Explorer ：ブラウザから参照可能な便利ツール
 https://graphexplorer.cloudapp.net/ //現在の方
 https://graphexplorer2.azurewebsites.net/ // 新しい方：まだ未対応

 Graph API のクエリ例
 https://msdn.microsoft.com/Library/Azure/Ad/Graph/howto/azure-ad-graph-api-supported-queries-filters-and-paging-options#CommonQueries
 https://graph.microsoft.io/ja-jp/docs/authorization/app_authorization

  これを見ながら改造
//https://azure.microsoft.com/ja-jp/documentation/articles/active-directory-devquickstarts-webapp-java/
 */
/**
 *
 * @author Yoshio Terada
 */
@Dependent
@PermitAll
public class GraphAPIImpl implements Serializable {

    private static final String PRINCIPAL_SESSION_NAME = "principal";
    private String tenant;
    private String authString;
    private Client jaxrsClient;
    private final static String GRAPH_SEVER = "graph.windows.net";
    private final static Logger LOGGER = Logger.getLogger(GraphAPIImpl.class.getName());

    @PostConstruct
    public void init() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
//        AzureADUserPrincipal userPrincipal = (AzureADUserPrincipal)request.getUserPrincipal();
//        AuthenticationResult authResult = userPrincipal.getAuthenticationResult();
//        System.out.println("GRAPH API Aceess Token : " + authResult.getAccessToken());

        AzureADUserPrincipal userPrincipal = (AzureADUserPrincipal) request.getSession().getAttribute(PRINCIPAL_SESSION_NAME);
        System.out.println("UserPrincipal Access Token:" + userPrincipal.getAuthenticationResult().getAccessToken());

        authString = "Bearer " + userPrincipal.getAuthenticationResult().getAccessToken();
        tenant = request.getServletContext().getInitParameter("tenant");

        jaxrsClient = ClientBuilder.newClient()
                .register((new JacksonJaxbJsonProvider(new ObjectMapper(), JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS)))
                .register(JacksonFeature.class);
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
    }

    public void init(HttpServletRequest request) {
        AzureADUserPrincipal userPrincipal = (AzureADUserPrincipal) request.getSession().getAttribute(PRINCIPAL_SESSION_NAME);
        System.out.println("UserPrincipal Access Token:" + userPrincipal.getAuthenticationResult().getAccessToken());

        authString = "Bearer " + userPrincipal.getAuthenticationResult().getAccessToken();
        tenant = request.getServletContext().getInitParameter("tenant");

        jaxrsClient = ClientBuilder.newClient()
                .register((new JacksonJaxbJsonProvider(new ObjectMapper(), JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS)))
                .register(JacksonFeature.class);
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
    }

    /* 登録済みの全ユーザ取得 */
    public ADUsers getAllADUserFromGraph() {
        String graphURL = String.format("https://%s/%s/users", GRAPH_SEVER, tenant);

        System.out.println("DEBUG:" + graphURL);

        ADUsers users = jaxrsClient.target(graphURL)
                .request()
                .header("Host", GRAPH_SEVER)
                .header("Accept", "application/json, text/plain, */*")
                .header("api-version", "1.6")
                .header("Authorization", authString)
                .get(ADUsers.class);
        LOGGER.log(Level.INFO, users.toString());
        return users;
    }

    /* 指定した ID (メールアドレス・)に対応する ADのユーザ取得 */
    public ADUser getADUserFromGraph(String id) {
        String graphURL = String.format("https://%s/%s/users/%s", GRAPH_SEVER, tenant, id);
        ADUser user = jaxrsClient.target(graphURL)
                .request()
                .header("Host", GRAPH_SEVER)
                .header("Accept", "application/json, text/plain, */*")
                .header("api-version", "1.6")
                .header("Authorization", authString)
                .get(ADUser.class);
        LOGGER.log(Level.INFO, user.toString());
        return user;
    }

    public ADGroups getAllADGroupFromGraph() {
        String graphURL = String.format("https://%s/%s/groups", GRAPH_SEVER, tenant);

        ADGroups groups = jaxrsClient.target(graphURL)
                .request()
                .header("Host", GRAPH_SEVER)
                .header("Accept", "application/json, text/plain, */*")
                .header("api-version", "1.6")
                .header("Authorization", authString)
                .get(ADGroups.class);
        LOGGER.log(Level.INFO, groups.toString());
        return groups;
    }

    /* 指定した グループID に対応するグループ取得 */
    public ADGroup getADGroupFromGraph(String groupid) {
        String graphURL = String.format("https://%s/%s/groups/%s", GRAPH_SEVER, tenant, groupid);

        ADGroup group = jaxrsClient.target(graphURL)
                .request()
                .header("Host", GRAPH_SEVER)
                .header("Accept", "application/json, text/plain, */*")
                .header("api-version", "1.6")
                .header("Authorization", authString)
                .get(ADGroup.class);
        LOGGER.log(Level.INFO, group.toString());
        return group;
    }

    /* 指定した グループID に所属するユーザ一覧取得 */
    public ADUsers getAllUsersInGroup(String groupid) {
        String graphURL = String.format("https://%s/%s/groups/%s/members", GRAPH_SEVER, tenant, groupid);
        ADUsers users = jaxrsClient.target(graphURL)
                .request()
                .header("Host", GRAPH_SEVER)
                .header("Accept", "application/json, text/plain, */*")
                .header("api-version", "1.6")
                .header("Authorization", authString)
                .get(ADUsers.class);
        LOGGER.log(Level.INFO, users.toString());
        return users;
    }

    /* 指定した ユーザID が所属するグループの一覧を取得 */
    public ADUserMemberOfGroups getMemberOfGroup(String userID) {
        String graphURL = String.format("https://%s/%s/users/%s/getMemberGroups", GRAPH_SEVER, tenant, userID);
        JsonObject model = Json.createObjectBuilder()
                .add("securityEnabledOnly", "false")
                .build();
        StringWriter stWriter = new StringWriter();
        try (JsonWriter jsonWriter = Json.createWriter(stWriter)) {
            jsonWriter.writeObject(model);
        }
        String jsonData = stWriter.toString();

        Response response = jaxrsClient.target(graphURL)
                .request()
                .header("Host", GRAPH_SEVER)
                .header("Accept", "application/json, text/plain, */*")
                .header("Content-Type", "application/json")
                .header("api-version", "1.6")
                .header("Authorization", authString)
                .post(Entity.json(jsonData));
        ADUserMemberOfGroups memberOfGrups = response.readEntity(ADUserMemberOfGroups.class);
        LOGGER.log(Level.INFO, memberOfGrups.toString());
        return memberOfGrups;
    }
}
