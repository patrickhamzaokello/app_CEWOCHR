package com.pkasemer.MyFamlinkApp.Models;

public class Name {
    private String name;
    private String description;
    private int status;

    public Name(String name, String description, int status) {
        this.name = name;
        this.status = status;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public int getStatus() {
        return status;
    }

    public String getDescription() { return description;}
}