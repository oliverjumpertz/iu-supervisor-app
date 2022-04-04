package com.example.supervisionapp.data.model;

public class UserThesisModel {
    private long id;
    private String title;
    private String subtitle;
    private String description;
    private String expose;
    private long state;
    private boolean alreadyRequested;

    public UserThesisModel(long id, String title, String subtitle, String description, String expose, long state, boolean alreadyRequested) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.description = description;
        this.expose = expose;
        this.state = state;
        this.alreadyRequested = alreadyRequested;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getDescription() {
        return description;
    }

    public String getExpose() {
        return expose;
    }

    public long getState() {
        return state;
    }

    public boolean isAlreadyRequested() {
        return alreadyRequested;
    }
}
