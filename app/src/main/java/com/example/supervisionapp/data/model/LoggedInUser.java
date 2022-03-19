package com.example.supervisionapp.data.model;

import java.io.Serializable;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private final long userId;
    private final String displayName;
    private final UserType userType;

    public LoggedInUser(long userId, String displayName, UserType userType) {
        this.userId = userId;
        this.displayName = displayName;
        this.userType = userType;
    }

    public long getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public UserType getUserType() {
        return userType;
    }
}