package com.ways.krbackend.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class email {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    private String subject;
    private String fromAddress;
    private Date sentDate;
    @Column(columnDefinition = "LONGTEXT")
    private String content;

    public boolean isForbidden() {
        return forbidden;
    }

    public void setForbidden(boolean forbidden) {
        this.forbidden = forbidden;
    }

    @Column(columnDefinition = "TINYINT(1)")
    private boolean forbidden;



    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String from) {
        this.fromAddress = from;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}