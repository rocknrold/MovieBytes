package com.example.moviebytes.models;

public class Movie {

    private int movie_id;
    private String name;
    private String story;
    private String date;
    private int duration;
    private String info;
    private int genreId;
    private int certId;
    private String poster;

    public Movie(int movie_id,String name, String story, String date, int duration, String info, int genreId, int certId, String poster) {
        this.movie_id = movie_id;
        this.name = name;
        this.story = story;
        this.date = date;
        this.duration = duration;
        this.info = info;
        this.genreId = genreId;
        this.certId = certId;
        this.poster = poster;
    }

    public int getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(int movie_id) {
        this.movie_id = movie_id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getGenreId() {
        return genreId;
    }

    public void setGenreId(int genreId) {
        this.genreId = genreId;
    }

    public int getCertId() {
        return certId;
    }

    public void setCertId(int certId) {
        this.certId = certId;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }
}
