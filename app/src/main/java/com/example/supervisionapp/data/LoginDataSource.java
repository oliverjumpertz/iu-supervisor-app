package com.example.supervisionapp.data;

import com.example.supervisionapp.data.model.LoggedInUser;
import com.example.supervisionapp.data.model.UserType;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication
            LoggedInUser fakeUser;
            if ("a".equals(username)) {
                fakeUser = new LoggedInUser(java.util.UUID.randomUUID().toString(), username, UserType.STUDENT);
            } else if ("b".equals(username)) {
                fakeUser = new LoggedInUser(java.util.UUID.randomUUID().toString(), username, UserType.SUPERVISOR);
            } else {
                return new Result.Error(new IllegalArgumentException("User unknown"));
            }
            return new Result.Success(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}