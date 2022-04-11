package com.example.supervisionapp.data;

import com.example.supervisionapp.data.model.LoggedInUser;
import com.example.supervisionapp.data.model.User;
import com.example.supervisionapp.persistence.AppDatabase;
import com.example.supervisionapp.persistence.UserRepository;
import com.example.supervisionapp.ui.main.SupervisorApplication;
import com.example.supervisionapp.utils.PasswordUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {
        try {
            AppDatabase appDatabase = AppDatabase.getDatabase(SupervisorApplication.getAppContext());
            UserRepository userRepository = new UserRepository(appDatabase);
            // this is a hacky way to ensure that the initial database data ingestion has
            // actually finished.
            // This ingestion has to be async because it otherwise triggers an infinite loop.
            // As we cannot await the ingestion elsewhere, we do it here to ensure the login succeeds
            // and does not always fail the first time.
            List<User> users = new ArrayList<>();
            while (users.isEmpty()) {
                users = userRepository.getAll().blockingGet();
            }
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
        // noop
    }
}