package com.example.supervisionapp.data;

import com.example.supervisionapp.data.model.LoggedInUser;
import com.example.supervisionapp.persistence.AppDatabase;
import com.example.supervisionapp.persistence.User;
import com.example.supervisionapp.persistence.UserDao;
import com.example.supervisionapp.persistence.UserType;
import com.example.supervisionapp.persistence.UserTypeDao;
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
            UserDao userDao = appDatabase.userDao();
            User user = userDao.getByUsername(username).blockingGet();
            if (user == null) {
                return new Result.Error(new IllegalArgumentException("User or password wrong"));
            }
            String passwordSha = PasswordUtils.createSha256(password);
            if (!passwordSha.equals(user.password)) {
                return new Result.Error(new IllegalArgumentException("User or password wrong"));
            }
            UserTypeDao userTypeDao = appDatabase.userTypeDao();
            UserType userType = userTypeDao.getById(user.type).blockingGet();
            LoggedInUser loggedInUser = new LoggedInUser(user.id, user.username, com.example.supervisionapp.data.model.UserType.valueOf(userType.type));
            return new Result.Success(loggedInUser);
        } catch (Exception e) {
            return new Result.Error(new IllegalStateException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}