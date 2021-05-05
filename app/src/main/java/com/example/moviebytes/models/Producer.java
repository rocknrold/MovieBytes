package com.example.moviebytes.models;

public class Producer {
    private int producer_id;
    private String name;
    private String email;
    private String website;

    public Producer(int producer_id, String name, String email, String website) {
        this.producer_id = producer_id;
        this.name = name;
        this.email = email;
        this.website = website;
    }

    public int getProducer_id() {
        return producer_id;
    }

    public void setProducer_id(int producer_id) {
        this.producer_id = producer_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @Override
    public String toString() {
        return name;
    }
}
