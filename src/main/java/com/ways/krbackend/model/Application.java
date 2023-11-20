package com.ways.krbackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Application {
    @Id
    int id;
    String text;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
