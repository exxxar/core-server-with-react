/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.trustedsolutions.crypto.model;

/**
 *
 * @author SAMS
 */
import com.trustedsolutions.crypto.forms.CompanyForm;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@Entity
public class Company implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private Long id;

    @Column(name = "name")
    private String companyName;

    @Column(name = "description")
    private String description;

    @Column(name = "user_check_url")
    private String userCheckUrl;

    @Column(name = "active")
    private boolean active = true;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable
    private Set<TrustedDevice> trustedDevices;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

//    @OneToMany(mappedBy = "devices")
//    private Set<TrustedDevice> users = new HashSet<>();
    @CreationTimestamp
    @Column(name = "create_date_time")
    private LocalDateTime createDateTime;

    @UpdateTimestamp
    @Column(name = "update_date_time")
    private LocalDateTime updateDateTime;

    public Company(CompanyForm company) {
        setCompany(company);

    }

    public Company() {

    }

    public void setCompany(CompanyForm company) {
        this.companyName = company.getCompanyName() == null ? this.companyName : company.getCompanyName();

        this.description = company.getDescription() == null ? this.description : company.getDescription();
        this.userCheckUrl = company.getUserCheckUrl() == null ? this.userCheckUrl : company.getUserCheckUrl();

    }

    public Company(String companyName, String description, String callbackUrl, String userCheckUrl, LocalDateTime createDateTime) {
        this.companyName = companyName;

        this.description = description;

        this.userCheckUrl = userCheckUrl;
        this.createDateTime = createDateTime;
    }

    public Company(Long id, String companyName, String description, String userCheckUrl, LocalDateTime createDateTime, LocalDateTime updateDateTime) {
        this.id = id;

        this.companyName = companyName;
        this.description = description;

        this.userCheckUrl = userCheckUrl;
        this.createDateTime = createDateTime;
        this.updateDateTime = updateDateTime;
    }

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

    public Set<TrustedDevice> getTrustedDevices() {
        return trustedDevices;
    }

    public void addTrustedDevice(TrustedDevice trustedDevice) {
        this.trustedDevices.add(trustedDevice);
    }

    public void setTrustedDevices(Set<TrustedDevice> trustedDevices) {
        this.trustedDevices = trustedDevices;
    }

    public LocalDateTime getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(LocalDateTime createDateTime) {
        this.createDateTime = createDateTime;
    }

    public LocalDateTime getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(LocalDateTime updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject company = new JSONObject();
        company.put("id", getId());
        company.put("active", isActive());
        company.put("companyName", getCompanyName());
        company.put("createDateTime", getCreateDateTime());
        company.put("userCheckUrl", getUserCheckUrl());

        JSONArray arr = new JSONArray();

        if (getTrustedDevices() != null) {
            for (TrustedDevice td : getTrustedDevices()) {
                arr.add(td.toJSON());
            }
        }

        company.put("trustedDevices", arr);

        return company;
    }

}
