package com.example.supervisionapp.ui.login;

import com.example.supervisionapp.data.model.UserType;

import java.io.Serializable;

/**
 * Class exposing authenticated user details to the UI.
 */
public class LoggedInUserView implements Serializable {
    private final String displayName;
    private final UserType userType;
    //... other data fields that may be accessible to the UI

    LoggedInUserView(String displayName, UserType userType) {
        this.displayName = displayName;
        this.userType = userType;
    }

    String getDisplayName() {
        return displayName;
    }

    public UserType getUserType() {
        return userType;
    }
}