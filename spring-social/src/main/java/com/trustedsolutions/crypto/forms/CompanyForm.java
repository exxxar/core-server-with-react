/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.trustedsolutions.crypto.forms;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author SAMS
 */
public class CompanyForm {

    private Long id;

    @NotNull
    @Size(min = 1, max = 255)
    private String companyName;

    @NotNull
    @Size(min = 1, max = 255)
    private String description;

    @NotNull
    @Size(min = 1, max = 255)
    private String callbackUrl;

    @NotNull
    @Size(min = 1, max = 255)
    private String userCheckUrl;

    @NotNull
    private boolean active;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getUserCheckUrl() {
        return userCheckUrl;
    }

    public void setUserCheckUrl(String userCheckUrl) {
        this.userCheckUrl = userCheckUrl;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
