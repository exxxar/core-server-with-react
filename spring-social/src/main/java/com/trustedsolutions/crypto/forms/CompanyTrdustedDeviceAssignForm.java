/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.trustedsolutions.crypto.forms;

import javax.validation.constraints.NotNull;

/**
 *
 * @author SAMS
 */
public class CompanyTrdustedDeviceAssignForm {

    @NotNull
    private Long trustedDeviceId;

    @NotNull
    private Long companyId;

    public Long getTrustedDeviceId() {
        return trustedDeviceId;
    }

    public void setTrustedDeviceId(Long trustedDeviceId) {
        this.trustedDeviceId = trustedDeviceId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

}
