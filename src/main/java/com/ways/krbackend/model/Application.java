package com.ways.krbackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

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
}
