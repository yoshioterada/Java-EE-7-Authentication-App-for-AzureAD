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
package com.yoshio3.azuread.cdis;

import com.yoshio3.azuread.graph.GraphAPIImpl;
import com.yoshio3.azuread.cdis.extensionOfPF.GroupSelectionModel;
import com.yoshio3.azuread.cdis.extensionOfPF.UserSelectionModel;
import com.yoshio3.azuread.entities.ADGroup;
import com.yoshio3.azuread.entities.ADUser;
import java.io.IOException;
import java.io.Serializable;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.security.PermitAll;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Yoshio Terada
 */
@Named(value = "indexPage")
@ViewScoped
@PermitAll
public class IndexPageBackingBean implements Serializable {

    private final static Logger LOGGER = Logger.getLogger(IndexPageBackingBean.class.getName());

    private UserSelectionModel selectionModel;
    private GroupSelectionModel groupSelectionModel;

    private String role;

    private boolean admin;

    @Inject
    private GraphAPIImpl graph;

    private ADUser[] users;
    private ADGroup[] groups;

    private ADUser user;
    private ADUser selectedUser;

    private ADGroup group;
    private ADGroup selectedGroup;

    private String[] memberOfGroup;

    private String nameAndAddress;

    @PostConstruct
    public void init() {
        List<ADUser> data = Arrays.asList(graph.getAllADUserFromGraph().getValue());
        selectionModel = new UserSelectionModel(data);

        Principal userPrincipal = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getUserPrincipal();
    }

    /* ユーザ一覧の取得 */
    public void pushGetUsersInfo() {
        selectionModel = getSelectionModel();
    }

    /* グループ一覧の取得 */
    public void pushGetGroupsInfo() {
        if (groupSelectionModel == null) {
            List<ADGroup> data = Arrays.asList(graph.getAllADGroupFromGraph().getValue());
            groupSelectionModel = new GroupSelectionModel(data);
        } else {
            groupSelectionModel = getGroupSelectionModel();
        }
    }

    /* 特定ユーザの詳細情報の取得 */
    public void onUserRowSelect(SelectEvent event) {
        String id = ((ADUser) event.getObject()).getObjectId();
        user = graph.getADUserFromGraph(id);
        memberOfGroup = graph.getMemberOfGroup(id).getValue();
    }

    /* 特定グループに含まれるユーザ情報の取得 */
    public void onGroupRowSelect(SelectEvent event) {
        String groupid = ((ADGroup) event.getObject()).getObjectId();
        users = graph.getAllUsersInGroup(groupid).getValue();
    }

    /*
     * Admin 権限の有無のチェック
     */
    public boolean isAdmin() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        return externalContext.isUserInRole("admin");
    }

    /*
     * ユーザが持つ権限に応じた処理例
     */
    public String nextPage() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        if (externalContext.isUserInRole("admin")) {
            return "nextAdmin";
        } else if (externalContext.isUserInRole("standard")) {
            return "nextStandard";
        } else {
            return "";
        }
    }

    /**
     * @return ユーザのメールアドレスと所属グループの取得
     */
    public String getNameAndAddress() {
        Principal userPrincipal = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
        String uid = userPrincipal.getName();
        ADUser adUserFromGraph = graph.getADUserFromGraph(uid);
        return adUserFromGraph.getDisplayName() + " : " + adUserFromGraph.getUserPrincipalName() + " でログインしています。";
    }

    public void logout() {
        try {
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

            HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
            request.logout();

            HttpSession session = (HttpSession) externalContext.getSession(false);
            session.invalidate();

            HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
            response.setHeader("Cache-Control", "no-cache"); //Forces caches to obtain a new copy of the page from the origin server
            response.setHeader("Cache-Control", "no-store"); //Directs caches not to store the page under any circumstance
            response.setDateHeader("Expires", 0); //Causes the proxy cache to see the page as "stale"
            response.setHeader("Pragma", "no-cache"); //HTTP 1.0 backward compatibility
            response.sendRedirect("https://login.microsoftonline.com/common/oauth2/logout?post_logout_redirect_uri=http%3A%2F%2Flocalhost:8080");
        } catch (ServletException | IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * ******** 以降は単なるセッタ・ゲッタ Lombok の導入でセッタ・ゲッタは省略可 ******************
     */
    /**
     * @param nameAndAddress the nameAndAddress to set
     */
    public void setNameAndAddress(String nameAndAddress) {
        this.nameAndAddress = nameAndAddress;
    }

    /**
     * @return the selectionModel
     */
    public UserSelectionModel getSelectionModel() {
        return selectionModel;
    }

    /**
     * @param selectionModel the selectionModel to set
     */
    public void setSelectionModel(UserSelectionModel selectionModel) {
        this.selectionModel = selectionModel;
    }

    /**
     * @return the users
     */
    public ADUser[] getUsers() {
        return users;
    }

    /**
     * @param users the users to set
     */
    public void setUsers(ADUser[] users) {
        this.users = users;
    }

    /**
     * @return the groups
     */
    public ADGroup[] getGroups() {
        return groups;
    }

    /**
     * @param groups the groups to set
     */
    public void setGroups(ADGroup[] groups) {
        this.groups = groups;
    }

    /**
     * @return the user
     */
    public ADUser getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(ADUser user) {
        this.user = user;
    }

    /**
     * @return the selectedUser
     */
    public ADUser getSelectedUser() {
        return selectedUser;
    }

    /**
     * @param selectedUser the selectedUser to set
     */
    public void setSelectedUser(ADUser selectedUser) {
        this.selectedUser = selectedUser;
    }

    /**
     * @return the group
     */
    public ADGroup getGroup() {
        return group;
    }

    /**
     * @param group the group to set
     */
    public void setGroup(ADGroup group) {
        this.group = group;
    }

    /**
     * @return the groupSelectionModel
     */
    public GroupSelectionModel getGroupSelectionModel() {
        return groupSelectionModel;
    }

    /**
     * @param groupSelectionModel the groupSelectionModel to set
     */
    public void setGroupSelectionModel(GroupSelectionModel groupSelectionModel) {
        this.groupSelectionModel = groupSelectionModel;
    }

    /**
     * @return the selectedGroup
     */
    public ADGroup getSelectedGroup() {
        return selectedGroup;
    }

    /**
     * @param selectedGroup the selectedGroup to set
     */
    public void setSelectedGroup(ADGroup selectedGroup) {
        this.selectedGroup = selectedGroup;
    }

    /**
     * @return the memberOfGroup
     */
    public String[] getMemberOfGroup() {
        return memberOfGroup;
    }

    /**
     * @param memberOfGroup the memberOfGroup to set
     */
    public void setMemberOfGroup(String[] memberOfGroup) {
        this.memberOfGroup = memberOfGroup;
    }

    /**
     * @return the role
     */
    public String getRole() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        if (externalContext.isUserInRole("admin")) {
            return "私は管理者です";
        } else if (externalContext.isUserInRole("standard")) {
            return "私は一般ユーザです";
        } else {
            return "";
        }
    }

    /**
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }
}
