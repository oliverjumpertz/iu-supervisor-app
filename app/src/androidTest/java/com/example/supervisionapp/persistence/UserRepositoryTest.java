package com.example.supervisionapp.persistence;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.supervisionapp.data.model.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UserRepositoryTest {
    private AppDatabase appDatabase;
    private UserRepository userRepository;
    private UserDao userDao;
    private UserTypeDao userTypeDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        userRepository = new UserRepository(appDatabase);
        userDao = appDatabase.userDao();
        userTypeDao = appDatabase.userTypeDao();
    }

    @After
    public void closeDb() {
        appDatabase.close();
    }

    @Test
    public void testThatNoUserFoundResultsInNull() {
        User user = userRepository.getUserByUsername("a").blockingGet();
        assertNull(user);
    }

    @Test
    public void testThatUserIsFound() {
        UserType userType = new UserType();
        userType.type = "STUDENT";
        userType.id = userTypeDao.insert(userType).blockingGet();

        com.example.supervisionapp.persistence.User dbUser = new com.example.supervisionapp.persistence.User();
        dbUser.username = "a";
        dbUser.type = userType.id;
        dbUser.id = userDao.insert(dbUser).blockingGet();

        User user = userRepository.getUserByUsername("a").blockingGet();
        assertNotNull(user);
        assertEquals(user.getUsername(), dbUser.username);
        assertEquals(user.getUserType().toString(), userType.type);
    }
}
