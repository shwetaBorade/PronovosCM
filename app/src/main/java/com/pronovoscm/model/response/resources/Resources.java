package com.pronovoscm.model.response.resources;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Resources {

    @SerializedName("users")
    private List<Users> users;
    @SerializedName("project_role_name")
    private String projectRoleName;
    @SerializedName("project_roles_id")
    private int projectRolesId;
    @SerializedName("pj_projects_id")
    private int pjProjectsId;
    @SerializedName("project_team_id")
    private int projectTeamId;

    public List<Users> getUsers() {
        return users;
    }

    public void setUsers(List<Users> users) {
        this.users = users;
    }

    public String getProjectRoleName() {
        return projectRoleName;
    }

    public void setProjectRoleName(String projectRoleName) {
        this.projectRoleName = projectRoleName;
    }

    public int getProjectRolesId() {
        return projectRolesId;
    }

    public void setProjectRolesId(int projectRolesId) {
        this.projectRolesId = projectRolesId;
    }

    public int getPjProjectsId() {
        return pjProjectsId;
    }

    public void setPjProjectsId(int pjProjectsId) {
        this.pjProjectsId = pjProjectsId;
    }

    public int getProjectTeamId() {
        return projectTeamId;
    }

    public void setProjectTeamId(int projectTeamId) {
        this.projectTeamId = projectTeamId;
    }
}
