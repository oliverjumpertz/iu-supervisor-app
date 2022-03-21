package com.example.supervisionapp.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.supervisionapp.data.model.LoggedInUser;
import com.example.supervisionapp.data.model.ThesisModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

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
    private StudentDao studentDao;

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
        studentDao = appDatabase.studentDao();
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

    @Test
    public void testThatGetThesisByIdAndUserIdWorksWithMultipleTheses() {
        LoggedInUser user = insertBaseData();
        thesisRepository.createThesis("Test1", "TestDescription", user).blockingAwait();
        thesisRepository.createThesis("Test2", "TestDescription", user).blockingAwait();
        thesisRepository.createThesis("Test3", "TestDescription", user).blockingAwait();
        List<Supervisor> supervisors = supervisorDao.getByUser(user.getUserId()).blockingGet();

        Supervisor supervisorOne = supervisors.get(0);
        ThesisModel thesisOne = thesisRepository.getThesisByIdAndUser(supervisorOne.thesis, user).blockingGet();
        assertNotNull(thesisOne);
        assertEquals("Test1", thesisOne.getTitle());

        Supervisor supervisorTwo = supervisors.get(1);
        ThesisModel thesisTwo = thesisRepository.getThesisByIdAndUser(supervisorTwo.thesis, user).blockingGet();
        assertNotNull(thesisTwo);
        assertEquals("Test2", thesisTwo.getTitle());

        Supervisor supervisorThree = supervisors.get(2);
        ThesisModel thesisThree = thesisRepository.getThesisByIdAndUser(supervisorThree.thesis, user).blockingGet();
        assertNotNull(thesisThree);
        assertEquals("Test3", thesisThree.getTitle());
    }

    @Test
    public void testThatGetThesisByIdAndUserIdWorksWithSingleThesis() {
        LoggedInUser user = insertBaseData();
        thesisRepository.createThesis("Test1", "TestDescription", user).blockingAwait();
        List<Supervisor> supervisors = supervisorDao.getByUser(user.getUserId()).blockingGet();
        Supervisor supervisor = supervisors.get(0);

        ThesisModel thesis = thesisRepository.getThesisByIdAndUser(supervisor.thesis, user).blockingGet();
        assertNotNull(thesis);
        assertEquals("Test1", thesis.getTitle());
    }

    @Test
    public void testThatGetThesisByIdAndUserIdWorksWithSingleThesisAndStudent() {
        LoggedInUser user = insertBaseData();
        thesisRepository.createThesis("Test1", "TestDescription", user).blockingAwait();
        List<Thesis> theses = thesisDao.getAll().blockingGet();
        Thesis initialThesis = theses.get(0);
        UserType userType = new UserType();
        userType.type = "STUDENT";
        userType.id = userTypeDao.insert(userType).blockingGet();
        com.example.supervisionapp.persistence.User dbUser = new com.example.supervisionapp.persistence.User();
        dbUser.username = "b";
        dbUser.name = "Lampe";
        dbUser.foreName = "Kai";
        dbUser.type = userType.id;
        dbUser.id = userDao.insert(dbUser).blockingGet();
        Student student = new Student();
        student.thesis = initialThesis.id;
        student.user = dbUser.id;
        studentDao.insert(student).blockingAwait();

        List<Supervisor> supervisors = supervisorDao.getByUser(user.getUserId()).blockingGet();
        Supervisor supervisor = supervisors.get(0);

        ThesisModel thesis = thesisRepository.getThesisByIdAndUser(supervisor.thesis, user).blockingGet();
        assertNotNull(thesis);
        assertEquals("Test1", thesis.getTitle());
        assertEquals("Kai Lampe", thesis.getStudentName());
    }
}
