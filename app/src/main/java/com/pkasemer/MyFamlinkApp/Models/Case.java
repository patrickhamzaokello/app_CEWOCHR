package com.pkasemer.MyFamlinkApp.Models;

public class Case {
    private String name, case_category, location;
    private String description;
    private int status;

    public Case(String name, String case_category, String location, String description, int status) {
        this.name = name;
        this.case_category = case_category;
        this.location = location;
        this.status = status;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getCase_category() {
        return case_category;
    }

    public String getLocation() {
        return location;
    }

    public int getStatus() {
        return status;
    }

    public String getDescription() { return description;}
}