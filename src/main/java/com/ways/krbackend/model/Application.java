package com.ways.krbackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class Application {
    @Id
    private int id;
    private String name;
    private String summery;
    private int age;
    private int phone;
    private String profession;
    private String title;
    @OneToOne
    @JoinColumn(name="application", referencedColumnName = "id")
    email email;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSummery() {
        return summery;
    }

    public void setSummery(String text) {
        this.summery = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phoneNumber) {
        this.phone = phoneNumber;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String professionTitle) {
        this.profession = professionTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public com.ways.krbackend.model.email getEmail() {
        return email;
    }

    public void setEmail(com.ways.krbackend.model.email email) {
        this.email = email;
    }
}
