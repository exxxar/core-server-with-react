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
public class UserCheckForm {

    @NotNull
    @Size(min = 1, max = 255)
    private String userId;

    @NotNull
    @Size(min = 1, max = 255)
    private Long companyId;

  

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

}
