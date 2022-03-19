package com.example.supervisionapp.data;

import com.example.supervisionapp.data.model.LoggedInUser;
import com.example.supervisionapp.data.model.User;
import com.example.supervisionapp.persistence.AppDatabase;
import com.example.supervisionapp.persistence.UserRepository;
import com.example.supervisionapp.ui.main.SupervisorApplication;
import com.example.supervisionapp.utils.PasswordUtils;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {
        try {
            AppDatabase appDatabase = AppDatabase.getDatabase(SupervisorApplication.getAppContext());
            UserRepository userRepository = new UserRepository(appDatabase);
            User user = userRepository.getUserByUsername(username).blockingGet();
            if (user == null) {
                return new Result.Error(new IllegalArgumentException("User or password wrong"));
            }
            String passwordSha = PasswordUtils.createSha256(password);
            if (!passwordSha.equals(user.getPassword())) {
                return new Result.Error(new IllegalArgumentException("User or password wrong"));
            }
            LoggedInUser loggedInUser = new LoggedInUser(user.getId(), user.getUsername(), user.getUserType());
            return new Result.Success(loggedInUser);
        } catch (Exception e) {
            return new Result.Error(new IllegalStateException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}