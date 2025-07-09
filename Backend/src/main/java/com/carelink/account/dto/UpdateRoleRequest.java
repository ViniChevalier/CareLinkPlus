package com.carelink.account.dto;

public class UpdateRoleRequest {
    private String username;
    private String newRole;

    // getters e setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNewRole() {
        return newRole;
    }

    public void setNewRole(String newRole) {
        this.newRole = newRole;
    }
}