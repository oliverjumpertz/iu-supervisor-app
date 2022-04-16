package com.example.supervisionapp.persistence;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.supervisionapp.data.model.InvoiceStateModel;
import com.example.supervisionapp.data.model.LoggedInUser;
import com.example.supervisionapp.data.model.SupervisionRequestTypeModel;
import com.example.supervisionapp.data.model.SupervisoryStateModel;
import com.example.supervisionapp.data.model.SupervisoryTypeModel;
import com.example.supervisionapp.data.model.ThesisStateModel;
import com.example.supervisionapp.data.model.User;
import com.example.supervisionapp.data.model.UserTypeModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;

@RunWith(AndroidJUnit4.class)
public class UserRepositoryTest {
    private AppDatabase appDatabase;
    private UserRepository userRepository;
    private UserDao userDao;
    private UserTypeDao userTypeDao;
    private ThesisRepository thesisRepository;
    private ThesisDao thesisDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        userRepository = new UserRepository(appDatabase);
        userDao = appDatabase.userDao();
        userTypeDao = appDatabase.userTypeDao();
        thesisRepository = new ThesisRepository(appDatabase);
        thesisDao = appDatabase.thesisDao();
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
        userType.type = UserTypeModel.STUDENT.name();
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

    @Test
    public void testThatGetEligibleSecondSupervisorsWorks() {
        UserType userType = new UserType();
        userType.type = UserTypeModel.SUPERVISOR.name();
        userType.id = userTypeDao.insert(userType).blockingGet();

        com.example.supervisionapp.persistence.User firstSupervisor = new com.example.supervisionapp.persistence.User();
        firstSupervisor.username = "a";
        firstSupervisor.type = userType.id;
        firstSupervisor.id = userDao.insert(firstSupervisor).blockingGet();

        LoggedInUser firstLoggedInUser = new LoggedInUser(
                firstSupervisor.id,
                firstSupervisor.username,
                UserTypeModel.SUPERVISOR
        );

        com.example.supervisionapp.persistence.User secondSupervisor = new com.example.supervisionapp.persistence.User();
        secondSupervisor.username = "b";
        secondSupervisor.type = userType.id;
        secondSupervisor.id = userDao.insert(secondSupervisor).blockingGet();

        com.example.supervisionapp.persistence.User thirdSupervisor = new com.example.supervisionapp.persistence.User();
        thirdSupervisor.username = "c";
        thirdSupervisor.type = userType.id;
        thirdSupervisor.id = userDao.insert(thirdSupervisor).blockingGet();

        List<User> eligibleSecondSupervisors = userRepository
                .getEligibleSecondSupervisors(firstLoggedInUser)
                .blockingGet();
        assertNotNull(eligibleSecondSupervisors);
        assertFalse(eligibleSecondSupervisors.isEmpty());
        assertEquals(2, eligibleSecondSupervisors.size());
    }

    @Test
    public void testThatGetEligibleSecondSupervisorsWorksWhenSupervisionRequestExists() {
        basicEnumSetup();
        UserType userType = new UserType();
        userType.type = UserTypeModel.SUPERVISOR.name();
        userType.id = userTypeDao.insert(userType).blockingGet();

        com.example.supervisionapp.persistence.User firstSupervisor = new com.example.supervisionapp.persistence.User();
        firstSupervisor.username = "a";
        firstSupervisor.type = userType.id;
        firstSupervisor.id = userDao.insert(firstSupervisor).blockingGet();

        LoggedInUser firstLoggedInUser = new LoggedInUser(
                firstSupervisor.id,
                firstSupervisor.username,
                UserTypeModel.SUPERVISOR
        );

        com.example.supervisionapp.persistence.User secondSupervisor = new com.example.supervisionapp.persistence.User();
        secondSupervisor.username = "b";
        secondSupervisor.type = userType.id;
        secondSupervisor.id = userDao.insert(secondSupervisor).blockingGet();

        com.example.supervisionapp.persistence.User thirdSupervisor = new com.example.supervisionapp.persistence.User();
        thirdSupervisor.username = "c";
        thirdSupervisor.type = userType.id;
        thirdSupervisor.id = userDao.insert(thirdSupervisor).blockingGet();

        thesisRepository
                .createThesis("", "", firstLoggedInUser)
                .blockingAwait();
        Thesis thesis = thesisDao.getAll().blockingGet().get(0);
        thesisRepository
                .requestSecondSupervisor(thesis.id, secondSupervisor.id)
                .blockingAwait();
        List<User> eligibleSecondSupervisors = userRepository
                .getEligibleSecondSupervisors(firstLoggedInUser)
                .blockingGet();
        assertNotNull(eligibleSecondSupervisors);
        assertFalse(eligibleSecondSupervisors.isEmpty());
        assertEquals(1, eligibleSecondSupervisors.size());
    }

    private void basicEnumSetup() {
        Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                appDatabase.runInTransaction(new Runnable() {
                    @Override
                    public void run() {
                        ThesisStateDao thesisStateDao = appDatabase.thesisStateDao();
                        for (ThesisStateModel thesisState : ThesisStateModel.values()) {
                            ThesisState newThesisState = new ThesisState();
                            newThesisState.state = thesisState.name();
                            newThesisState.id = thesisStateDao.insert(newThesisState).blockingGet();
                        }

                        SupervisoryTypeDao supervisoryTypeDao = appDatabase.supervisoryTypeDao();
                        for (SupervisoryTypeModel supervisoryType : SupervisoryTypeModel.values()) {
                            SupervisoryType newSupervisoryType = new SupervisoryType();
                            newSupervisoryType.type = supervisoryType.name();
                            newSupervisoryType.id = supervisoryTypeDao.insert(newSupervisoryType).blockingGet();
                        }

                        InvoiceStateDao invoiceStateDao = appDatabase.invoiceStateDao();
                        for (InvoiceStateModel invoiceState : InvoiceStateModel.values()) {
                            InvoiceState newInvoiceState = new InvoiceState();
                            newInvoiceState.state = invoiceState.name();
                            newInvoiceState.id = invoiceStateDao.insert(newInvoiceState).blockingGet();
                        }

                        SupervisoryStateDao supervisoryStateDao = appDatabase.supervisoryStateDao();
                        for (SupervisoryStateModel supervisoryState : SupervisoryStateModel.values()) {
                            SupervisoryState newSupervisoryState = new SupervisoryState();
                            newSupervisoryState.state = supervisoryState.name();
                            newSupervisoryState.id = supervisoryStateDao.insert(newSupervisoryState).blockingGet();
                        }

                        SupervisionRequestTypeDao supervisionRequestTypeDao = appDatabase.supervisionRequestTypeDao();
                        for (SupervisionRequestTypeModel supervisionRequestType : SupervisionRequestTypeModel.values()) {
                            SupervisionRequestType newSupervisionRequestType = new SupervisionRequestType();
                            newSupervisionRequestType.type = supervisionRequestType.name();
                            newSupervisionRequestType.id = supervisionRequestTypeDao.insert(newSupervisionRequestType).blockingGet();
                        }
                    }
                });
            }
        }).blockingAwait();
    }
}
