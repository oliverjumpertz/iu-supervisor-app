package com.example.supervisionapp.ui.login;

import com.example.supervisionapp.data.model.UserTypeModel;

import java.io.Serializable;

/**
 * Class exposing authenticated user details to the UI.
 */
public class LoggedInUserView implements Serializable {
    private final String displayName;
    private final UserTypeModel userTypeModel;
    //... other data fields that may be accessible to the UI

    LoggedInUserView(String displayName, UserTypeModel userTypeModel) {
        this.displayName = displayName;
        this.userTypeModel = userTypeModel;
    }

    String getDisplayName() {
        return displayName;
    }

    public UserTypeModel getUserType() {
        return userTypeModel;
    }
}