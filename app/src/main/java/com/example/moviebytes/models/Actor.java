package com.example.moviebytes.models;

public class Actor {
    private int actorId;
    private String name;
    private String note;
    private String poster;

    public Actor(int actorId, String name, String note, String poster) {
        this.actorId = actorId;
        this.name = name;
        this.note = note;
        this.poster = poster;
    }

    public int getActorId() {
        return actorId;
    }

    public void setActorId(int actorId) {
        this.actorId = actorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }
}
