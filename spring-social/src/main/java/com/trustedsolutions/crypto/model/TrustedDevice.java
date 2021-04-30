package com.trustedsolutions.crypto.model;


import com.core.cryptointerface.forms.TrustedDeviceForm;
import java.io.Serializable;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Base64;

import java.util.Set;

import org.json.simple.JSONObject;

@Entity
public class TrustedDevice implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "device_public_id", unique = true)
    private String devicePublicId;

    @Column(name = "device_factory_key")
    private String deviceFactoryKey;

    @Column(name = "device_private_id", unique = true)
    private String devicePrivateId;

    @Column(name = "device_actual_key")
    private String deviceActualKey;

    @Column(name = "device_old_key")
    private String deviceOldKey;

    @Column(name = "active")
    private boolean active = true;

    @Column(name = "description")
    private String description = "";

    @CreationTimestamp
    @Column(name = "create_date_time")
    private LocalDateTime createDateTime;

    @UpdateTimestamp
    @Column(name = "update_date_time")
    private LocalDateTime updateDateTime;

    @ManyToMany(mappedBy = "trustedDevices")
    private Set<Company> companies;

    public TrustedDevice(TrustedDeviceForm tdForm) {
        setTrustedDevice(tdForm);
    }

    public void setTrustedDevice(TrustedDeviceForm tdForm) {

        this.devicePublicId = tdForm.getDevicePublicId() == null ? this.devicePublicId : tdForm.getDevicePublicId();
        this.devicePrivateId = tdForm.getDevicePrivateId() == null ? this.devicePrivateId : tdForm.getDevicePrivateId();

        this.setDeviceActualKey(tdForm.getDeviceActualKey());
        this.setDeviceOldKey(tdForm.getDeviceOldKey());
        this.setDeviceFactoryKey(tdForm.getDeviceFactoryKey());

        this.active = tdForm.isActive();
        this.description = tdForm.getDescription() == null ? this.description : tdForm.getDescription();

    }

    public TrustedDevice(String devicePublicId,
            String devicePrivateId,
            byte[] deviceActualKey,
            byte[] deviceOldKey,
            byte[] deviceFactoryKey,
            boolean active,
            String description) {
        this.devicePublicId = devicePublicId;
        this.devicePrivateId = devicePrivateId;
        this.setDeviceActualKeyEncode(deviceActualKey);
        this.setDeviceOldKeyEncode(deviceOldKey);
        this.setDeviceFactoryKeyEncode(deviceFactoryKey);
        this.active = active;
        this.description = description;
    }

    public TrustedDevice(String devicePublicId,
            String devicePrivateId,
            String deviceActualKey,
            String deviceOldKey,
            String deviceFactoryKey,
            boolean active,
            String description) {
        this.devicePublicId = devicePublicId;
        this.devicePrivateId = devicePrivateId;
        this.setDeviceActualKey(deviceActualKey);
        this.setDeviceOldKey(deviceOldKey);
        this.setDeviceFactoryKey(deviceFactoryKey);
        this.description = description;

    }

    public TrustedDevice() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDevicePublicId(String devicePublicId) {
        this.devicePublicId = devicePublicId;
    }

    public void setDevicePrivateId(String devicePrivateId) {
        this.devicePrivateId = devicePrivateId;
    }

    public void setDeviceFactoryKey(String deviceFactoryKey) {
        this.deviceFactoryKey = deviceFactoryKey;
    }

    public void setDeviceActualKey(String deviceActualKey) {
        this.deviceActualKey = deviceActualKey;
    }

    public void setDeviceOldKey(String deviceOldKey) {
        this.deviceOldKey = deviceOldKey;
    }

    public void setDeviceFactoryKeyEncode(byte[] deviceFactoryKey) {
        this.deviceFactoryKey = Base64.getEncoder().encodeToString(deviceFactoryKey);
    }

    public void setDeviceActualKeyEncode(byte[] deviceActualKey) {
        this.deviceActualKey = Base64.getEncoder().encodeToString(deviceActualKey);
    }

    public void setDeviceOldKeyEncode(byte[] deviceOldKey) {
        this.deviceOldKey = Base64.getEncoder().encodeToString(deviceOldKey);
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreateDateTime(LocalDateTime createDateTime) {
        this.createDateTime = createDateTime;
    }

    public void setUpdateDateTime(LocalDateTime updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    public String getDevicePublicId() {
        return devicePublicId;
    }

    public String getDevicePrivateId() {
        return devicePrivateId;
    }

    public byte[] getDeviceActualKey() {
        try {
            return Base64.getDecoder().decode(deviceActualKey.getBytes());
        } catch (Exception ex) {
            System.out.print("getDeviceActualKey=>" + ex.getMessage());
            return deviceActualKey.getBytes();
        }

    }

    public byte[] getDeviceFactoryKey() {

        try {
            return Base64.getDecoder().decode(deviceFactoryKey.getBytes());
        } catch (Exception ex) {
            System.out.print("getDeviceFactoryKey=>" + ex.getMessage());
            return deviceFactoryKey.getBytes();
        }

    }

    public byte[] getDeviceOldKey() {
        try {
            return Base64.getDecoder().decode(deviceOldKey.getBytes());
        } catch (Exception ex) {
            System.out.print("getDeviceOldKey=>" + ex.getMessage());
            return deviceOldKey.getBytes();
        }
    }

    public boolean isActive() {
        return active;
    }

    public String getDescription() {
        return description;
    }

    public JSONObject toJSON() {
        JSONObject device = new JSONObject();
        device.put("id", id);
        device.put("description", description);
        device.put("deviceActualKey", deviceActualKey);
        device.put("deviceFactoryKey", deviceFactoryKey);
        device.put("deviceOldKey", deviceOldKey);
        device.put("devicePrivateId", devicePrivateId);
        device.put("devicePublicId", devicePublicId);
        device.put("createDateTime", createDateTime);

        return device;
    }

    public TrustedDeviceForm getTrustedDeviceForm() {
        TrustedDeviceForm tdf = new TrustedDeviceForm();
        tdf.setId(id);
        tdf.setActive(active);
        tdf.setDescription(description);
        tdf.setDeviceActualKey(deviceActualKey);
        tdf.setDeviceFactoryKey(deviceFactoryKey);
        tdf.setDeviceOldKey(deviceOldKey);
        tdf.setDevicePrivateId(devicePrivateId);
        tdf.setDevicePublicId(devicePublicId);

        return tdf;
    }
}
