package com.example.supervisionapp.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private final long userId;
    private final String displayName;
    private final UserTypeModel userTypeModel;

    public LoggedInUser(long userId, String displayName, UserTypeModel userTypeModel) {
        this.userId = userId;
        this.displayName = displayName;
        this.userTypeModel = userTypeModel;
    }

    public long getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public UserTypeModel getUserType() {
        return userTypeModel;
    }
}