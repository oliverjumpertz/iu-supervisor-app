package com.example.supervisionapp.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.supervisionapp.data.model.LoggedInUser;
import com.example.supervisionapp.data.model.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@RunWith(AndroidJUnit4.class)
public class ThesisRepositoryTest {
    private AppDatabase appDatabase;
    private ThesisRepository thesisRepository;
    private UserTypeDao userTypeDao;
    private UserDao userDao;
    private ThesisStateDao thesisStateDao;
    private SupervisoryTypeDao supervisoryTypeDao;
    private InvoiceStateDao invoiceStateDao;
    private SupervisoryStateDao supervisoryStateDao;
    private ThesisDao thesisDao;
    private SupervisorDao supervisorDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        thesisRepository = new ThesisRepository(appDatabase);
        userTypeDao = appDatabase.userTypeDao();
        userDao = appDatabase.userDao();
        thesisStateDao = appDatabase.thesisStateDao();
        supervisoryTypeDao = appDatabase.supervisoryTypeDao();
        invoiceStateDao = appDatabase.invoiceStateDao();
        supervisoryStateDao = appDatabase.supervisoryStateDao();
        thesisDao = appDatabase.thesisDao();
        supervisorDao = appDatabase.supervisorDao();
    }

    @After
    public void closeDb() {
        appDatabase.close();
    }

    private LoggedInUser insertBaseData() {
        ThesisState advertisedThesisState = new ThesisState();
        advertisedThesisState.state = "ADVERTISED";
        advertisedThesisState.id = thesisStateDao.insert(advertisedThesisState).blockingGet();

        SupervisoryType firstSupervisorType = new SupervisoryType();
        firstSupervisorType.type = "FIRST_SUPERVISOR";
        supervisoryTypeDao.insert(firstSupervisorType).blockingGet();
        SupervisoryType secondSupervisorType = new SupervisoryType();
        secondSupervisorType.type = "SECOND_SUPERVISOR";
        supervisoryTypeDao.insert(secondSupervisorType).blockingGet();

        InvoiceState invoiceStateUnfinished = new InvoiceState();
        invoiceStateUnfinished.state = "UNFINISHED";
        invoiceStateUnfinished.id = invoiceStateDao.insert(invoiceStateUnfinished).blockingGet();

        SupervisoryState draftSupervisoryState = new SupervisoryState();
        draftSupervisoryState.state = "DRAFT";
        draftSupervisoryState.id = supervisoryStateDao.insert(draftSupervisoryState).blockingGet();

        UserType userType = new UserType();
        userType.type = "SUPERVISOR";
        userType.id = userTypeDao.insert(userType).blockingGet();

        com.example.supervisionapp.persistence.User dbUser = new com.example.supervisionapp.persistence.User();
        dbUser.username = "a";
        dbUser.type = userType.id;
        dbUser.id = userDao.insert(dbUser).blockingGet();

        return new LoggedInUser(dbUser.id, dbUser.username, com.example.supervisionapp.data.model.UserType.valueOf(userType.type));
    }

    @Test
    public void testThatThesisCanBeCreated() {
        LoggedInUser user = insertBaseData();

        thesisRepository.createThesis("Test", "TestDescription", user).blockingAwait();

        List<Thesis> theses = thesisDao.getAll().blockingGet();
        assertFalse(theses.isEmpty());
        assertEquals(1, theses.size());
        Thesis thesis = theses.get(0);
        assertNotNull(thesis);

        List<Supervisor> supervisors = supervisorDao.getByThesis(thesis.id).blockingGet();
        assertFalse(supervisors.isEmpty());
        assertEquals(1, supervisors.size());
        Supervisor supervisor = supervisors.get(0);
        assertNotNull(supervisor);
        assertEquals(thesis.id, supervisor.thesis);
        assertEquals(user.getUserId(), supervisor.user);
    }

    @Test
    public void testThatDraftThesisCanBeQueriedForSupervisor() {
        LoggedInUser user = insertBaseData();
        thesisRepository.createThesis("Test1", "TestDescription", user).blockingAwait();
        thesisRepository.createThesis("Test2", "TestDescription", user).blockingAwait();
        thesisRepository.createThesis("Test3", "TestDescription", user).blockingAwait();

        List<Thesis> theses = thesisRepository.getSupervisorsAdvertisedTheses(user).blockingGet();
        assertNotNull(theses);
        assertFalse(theses.isEmpty());
        assertEquals(3, theses.size());
    }
}
