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
    private int phoneNumber;
    private String professionTitle;
    private String diploma;


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

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfessionTitle() {
        return professionTitle;
    }

    public void setProfessionTitle(String professionTitle) {
        this.professionTitle = professionTitle;
    }

    public String getDiploma() {
        return diploma;
    }

    public void setDiploma(String diploma) {
        this.diploma = diploma;
    }
}
