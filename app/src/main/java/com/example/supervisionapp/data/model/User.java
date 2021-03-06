package com.example.supervisionapp.data.model;

public class User {
    private final long id;
    private final String username;
    private final String password;
    private final String title;
    private final String name;
    private final String forename;
    private final UserTypeModel userTypeModel;

    public User(long id, String username, String password, String title, String name, String forename, UserTypeModel userTypeModel) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.title = title;
        this.name = name;
        this.forename = forename;
        this.userTypeModel = userTypeModel;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getTitle() {
        return title;
    }

    public String getName() {
        return name;
    }

    public String getForename() {
        return forename;
    }

    public UserTypeModel getUserType() {
        return userTypeModel;
    }
}
