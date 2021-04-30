package com.trustedsolutions.crypto.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Transfer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    private String senderUserId;

    private String recipientUserId;

    private String data;

    public Transfer(String senderUserId, String recipientUserId, String data) {
        this.senderUserId = senderUserId;
        this.recipientUserId = recipientUserId;
        this.data = data;
    }

    public Transfer() {
    }

    /*public Long getId() {
        return id;
    }*/
    public void setId(Long id) {
        this.id = id;
    }

    public String getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(String senderTDID) {
        this.senderUserId = senderTDID;
    }

    public String getRecipientUserId() {
        return recipientUserId;
    }

    public void setRecipientUserId(String recipientTDID) {
        this.recipientUserId = recipientTDID;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
