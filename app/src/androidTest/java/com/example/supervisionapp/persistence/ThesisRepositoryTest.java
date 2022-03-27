package com.example.supervisionapp.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.supervisionapp.data.model.InvoiceStateModel;
import com.example.supervisionapp.data.model.LoggedInUser;
import com.example.supervisionapp.data.model.SupervisoryStateModel;
import com.example.supervisionapp.data.model.SupervisoryTypeModel;
import com.example.supervisionapp.data.model.ThesisModel;
import com.example.supervisionapp.data.model.ThesisStateModel;
import com.example.supervisionapp.data.model.UserTypeModel;

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
        advertisedThesisState.state = ThesisStateModel.ADVERTISED.name();;
        advertisedThesisState.id = thesisStateDao.insert(advertisedThesisState).blockingGet();

        ThesisState inProgressThesisState = new ThesisState();
        inProgressThesisState.state = ThesisStateModel.IN_PROGRESS.name();;
        inProgressThesisState.id = thesisStateDao.insert(inProgressThesisState).blockingGet();

        SupervisoryType firstSupervisorType = new SupervisoryType();
        firstSupervisorType.type = SupervisoryTypeModel.FIRST_SUPERVISOR.name();
        supervisoryTypeDao.insert(firstSupervisorType).blockingSubscribe();

        SupervisoryType secondSupervisorType = new SupervisoryType();
        secondSupervisorType.type = SupervisoryTypeModel.SECOND_SUPERVISOR.name();
        supervisoryTypeDao.insert(secondSupervisorType).blockingSubscribe();

        InvoiceState invoiceStateUnfinished = new InvoiceState();
        invoiceStateUnfinished.state = InvoiceStateModel.UNFINISHED.name();
        invoiceStateUnfinished.id = invoiceStateDao.insert(invoiceStateUnfinished).blockingGet();

        SupervisoryState draftSupervisoryState = new SupervisoryState();
        draftSupervisoryState.state = SupervisoryStateModel.DRAFT.name();
        draftSupervisoryState.id = supervisoryStateDao.insert(draftSupervisoryState).blockingGet();

        SupervisoryState supervisedSupervisoryState = new SupervisoryState();
        supervisedSupervisoryState.state = SupervisoryStateModel.SUPERVISED.name();
        supervisedSupervisoryState.id = supervisoryStateDao.insert(supervisedSupervisoryState).blockingGet();

        UserType supervisorUserType = new UserType();
        supervisorUserType.type = UserTypeModel.SUPERVISOR.name();
        supervisorUserType.id = userTypeDao.insert(supervisorUserType).blockingGet();

        UserType studentUserType = new UserType();
        studentUserType.type = UserTypeModel.STUDENT.name();
        studentUserType.id = userTypeDao.insert(studentUserType).blockingGet();

        User dbUser = new User();
        dbUser.username = "a";
        dbUser.type = supervisorUserType.id;
        dbUser.id = userDao.insert(dbUser).blockingGet();

        return new LoggedInUser(dbUser.id, dbUser.username, UserTypeModel.valueOf(supervisorUserType.type));
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

        User dbUser = new User();
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

    @Test
    public void testThatGetSupervisorsSupervisedThesesWorksWithSupervisedThesis() {
        LoggedInUser user = insertBaseData();
        thesisRepository.createThesis("Test1", "TestDescription", user).blockingAwait();
        List<Thesis> theses = thesisDao.getAll().blockingGet();

        Thesis initialThesis = theses.get(0);
        UserType userType = new UserType();
        userType.type = "STUDENT";
        userType.id = userTypeDao.insert(userType).blockingGet();

        User dbUser = new User();
        dbUser.username = "b";
        dbUser.name = "Lampe";
        dbUser.foreName = "Kai";
        dbUser.type = userType.id;
        dbUser.id = userDao.insert(dbUser).blockingGet();

        Student student = new Student();
        student.thesis = initialThesis.id;
        student.user = dbUser.id;
        studentDao.insert(student).blockingAwait();

        SupervisoryState supervisedSupervisoryState = new SupervisoryState();
        supervisedSupervisoryState.state = "SUPERVISED";
        supervisedSupervisoryState.id = supervisoryStateDao.insert(supervisedSupervisoryState).blockingGet();

        List<Supervisor> supervisors = supervisorDao.getByUser(user.getUserId()).blockingGet();
        Supervisor supervisor = supervisors.get(0);
        supervisor.state = supervisedSupervisoryState.id;
        supervisorDao.update(supervisor).blockingAwait();

        List<ThesisModel> resultingTheses = thesisRepository.getSupervisorsSupervisedTheses(user).blockingGet();
        assertNotNull(resultingTheses);
        assertFalse(resultingTheses.isEmpty());
        assertEquals(1, resultingTheses.size());
        ThesisModel resultingThesis = resultingTheses.get(0);
        assertEquals("Test1", resultingThesis.getTitle());
    }

    @Test
    public void testThatGetSupervisorsSupervisedThesesWorksWithMultipleSupervisedTheses() {
        LoggedInUser user = insertBaseData();
        thesisRepository.createThesis("Test1", "TestDescription", user).blockingAwait();
        thesisRepository.createThesis("Test2", "TestDescription", user).blockingAwait();
        List<Thesis> theses = thesisDao.getAll().blockingGet();

        Thesis initialThesis = theses.get(0);
        UserType userType = new UserType();
        userType.type = "STUDENT";
        userType.id = userTypeDao.insert(userType).blockingGet();

        User dbUser = new User();
        dbUser.username = "b";
        dbUser.name = "Lampe";
        dbUser.foreName = "Kai";
        dbUser.type = userType.id;
        dbUser.id = userDao.insert(dbUser).blockingGet();

        Student student = new Student();
        student.thesis = initialThesis.id;
        student.user = dbUser.id;
        studentDao.insert(student).blockingAwait();

        SupervisoryState supervisedSupervisoryState = new SupervisoryState();
        supervisedSupervisoryState.state = "SUPERVISED";
        supervisedSupervisoryState.id = supervisoryStateDao.insert(supervisedSupervisoryState).blockingGet();

        List<Supervisor> supervisors = supervisorDao.getByUser(user.getUserId()).blockingGet();
        Supervisor supervisor = supervisors.get(0);
        supervisor.state = supervisedSupervisoryState.id;
        supervisorDao.update(supervisor).blockingAwait();

        Supervisor otherSupervisor = supervisors.get(1);
        otherSupervisor.state = supervisedSupervisoryState.id;
        supervisorDao.update(otherSupervisor).blockingAwait();

        List<ThesisModel> resultingTheses = thesisRepository.getSupervisorsSupervisedTheses(user).blockingGet();
        assertNotNull(resultingTheses);
        assertFalse(resultingTheses.isEmpty());
        assertEquals(2, resultingTheses.size());
        assertEquals("Test1", resultingTheses.get(0).getTitle());
        assertEquals("Test2", resultingTheses.get(1).getTitle());
    }

    @Test
    public void testThatGetSupervisorsSupervisedThesesWorksWithDraftThesis() {
        LoggedInUser user = insertBaseData();
        thesisRepository.createThesis("Test1", "TestDescription", user).blockingAwait();
        List<Thesis> theses = thesisDao.getAll().blockingGet();

        Thesis initialThesis = theses.get(0);
        UserType userType = new UserType();
        userType.type = "STUDENT";
        userType.id = userTypeDao.insert(userType).blockingGet();

        User dbUser = new User();
        dbUser.username = "b";
        dbUser.name = "Lampe";
        dbUser.foreName = "Kai";
        dbUser.type = userType.id;
        dbUser.id = userDao.insert(dbUser).blockingGet();

        Student student = new Student();
        student.thesis = initialThesis.id;
        student.user = dbUser.id;
        studentDao.insert(student).blockingAwait();

        SupervisoryState draftSupervisoryState = supervisoryStateDao.getByState("DRAFT").blockingGet();

        List<Supervisor> supervisors = supervisorDao.getByUser(user.getUserId()).blockingGet();
        Supervisor supervisor = supervisors.get(0);
        supervisor.state = draftSupervisoryState.id;
        supervisorDao.update(supervisor).blockingAwait();

        List<ThesisModel> resultingTheses = thesisRepository.getSupervisorsSupervisedTheses(user).blockingGet();
        assertNotNull(resultingTheses);
        assertTrue(resultingTheses.isEmpty());
    }

    @Test
    public void testThatGetSupervisorsSupervisedThesesWorksWithMixedTheses() {
        LoggedInUser user = insertBaseData();
        thesisRepository.createThesis("Test1", "TestDescription", user).blockingAwait();
        thesisRepository.createThesis("Test2", "TestDescription", user).blockingAwait();

        List<Thesis> theses = thesisDao.getAll().blockingGet();

        Thesis initialThesis = theses.get(0);
        UserType userType = new UserType();
        userType.type = "STUDENT";
        userType.id = userTypeDao.insert(userType).blockingGet();

        User dbUser = new User();
        dbUser.username = "b";
        dbUser.name = "Lampe";
        dbUser.foreName = "Kai";
        dbUser.type = userType.id;
        dbUser.id = userDao.insert(dbUser).blockingGet();

        Student student = new Student();
        student.thesis = initialThesis.id;
        student.user = dbUser.id;
        studentDao.insert(student).blockingAwait();

        SupervisoryState supervisedSupervisoryState = new SupervisoryState();
        supervisedSupervisoryState.state = "SUPERVISED";
        supervisedSupervisoryState.id = supervisoryStateDao.insert(supervisedSupervisoryState).blockingGet();

        List<Supervisor> supervisors = supervisorDao.getByUser(user.getUserId()).blockingGet();
        Supervisor supervisor = supervisors.get(0);
        supervisor.state = supervisedSupervisoryState.id;
        supervisorDao.update(supervisor).blockingAwait();

        SupervisoryState draftSupervisoryState = supervisoryStateDao.getByState("DRAFT").blockingGet();
        Thesis otherThesis = theses.get(1);
        Supervisor otherSupervisor = supervisors.get(1);
        otherSupervisor.state = draftSupervisoryState.id;
        otherSupervisor.thesis = otherThesis.id;

        List<ThesisModel> resultingTheses = thesisRepository.getSupervisorsSupervisedTheses(user).blockingGet();
        assertNotNull(resultingTheses);
        assertFalse(resultingTheses.isEmpty());
        assertEquals(1, resultingTheses.size());
        ThesisModel resultingThesis = resultingTheses.get(0);
        assertEquals("Test1", resultingThesis.getTitle());
    }

    @Test
    public void testThatGetSupervisorsThesesRequestsWorksWithSupervisedThesis() {
        LoggedInUser user = insertBaseData();
        thesisRepository.createThesis("Test1", "TestDescription", user).blockingAwait();
        List<Thesis> theses = thesisDao.getAll().blockingGet();

        Thesis initialThesis = theses.get(0);
        UserType userType = new UserType();
        userType.type = "STUDENT";
        userType.id = userTypeDao.insert(userType).blockingGet();

        User dbUser = new User();
        dbUser.username = "b";
        dbUser.name = "Lampe";
        dbUser.foreName = "Kai";
        dbUser.type = userType.id;
        dbUser.id = userDao.insert(dbUser).blockingGet();

        Student student = new Student();
        student.thesis = initialThesis.id;
        student.user = dbUser.id;
        studentDao.insert(student).blockingAwait();

        SupervisoryState requestedSupervisoryState = new SupervisoryState();
        requestedSupervisoryState.state = "REQUESTED";
        requestedSupervisoryState.id = supervisoryStateDao.insert(requestedSupervisoryState).blockingGet();

        List<Supervisor> supervisors = supervisorDao.getByUser(user.getUserId()).blockingGet();
        Supervisor supervisor = supervisors.get(0);
        supervisor.state = requestedSupervisoryState.id;
        supervisorDao.update(supervisor).blockingAwait();

        List<ThesisModel> resultingTheses = thesisRepository.getSupervisorsThesesRequests(user).blockingGet();
        assertNotNull(resultingTheses);
        assertFalse(resultingTheses.isEmpty());
        assertEquals(1, resultingTheses.size());
        ThesisModel resultingThesis = resultingTheses.get(0);
        assertEquals("Test1", resultingThesis.getTitle());
    }

    @Test
    public void testThatGetSupervisorsThesesRequestsWorksWithMultipleSupervisedTheses() {
        LoggedInUser user = insertBaseData();
        thesisRepository.createThesis("Test1", "TestDescription", user).blockingAwait();
        thesisRepository.createThesis("Test2", "TestDescription", user).blockingAwait();
        List<Thesis> theses = thesisDao.getAll().blockingGet();

        Thesis initialThesis = theses.get(0);
        UserType userType = new UserType();
        userType.type = "STUDENT";
        userType.id = userTypeDao.insert(userType).blockingGet();

        User dbUser = new User();
        dbUser.username = "b";
        dbUser.name = "Lampe";
        dbUser.foreName = "Kai";
        dbUser.type = userType.id;
        dbUser.id = userDao.insert(dbUser).blockingGet();

        Student student = new Student();
        student.thesis = initialThesis.id;
        student.user = dbUser.id;
        studentDao.insert(student).blockingAwait();

        SupervisoryState requestedSupervisoryState = new SupervisoryState();
        requestedSupervisoryState.state = "REQUESTED";
        requestedSupervisoryState.id = supervisoryStateDao.insert(requestedSupervisoryState).blockingGet();

        List<Supervisor> supervisors = supervisorDao.getByUser(user.getUserId()).blockingGet();
        Supervisor supervisor = supervisors.get(0);
        supervisor.state = requestedSupervisoryState.id;
        supervisorDao.update(supervisor).blockingAwait();

        Supervisor otherSupervisor = supervisors.get(1);
        otherSupervisor.state = requestedSupervisoryState.id;
        supervisorDao.update(otherSupervisor).blockingAwait();

        List<ThesisModel> resultingTheses = thesisRepository.getSupervisorsThesesRequests(user).blockingGet();
        assertNotNull(resultingTheses);
        assertFalse(resultingTheses.isEmpty());
        assertEquals(2, resultingTheses.size());
        assertEquals("Test1", resultingTheses.get(0).getTitle());
        assertEquals("Test2", resultingTheses.get(1).getTitle());
    }

    @Test
    public void testThatGetSupervisorsThesesRequestsWorksWithDraftThesis() {
        LoggedInUser user = insertBaseData();
        thesisRepository.createThesis("Test1", "TestDescription", user).blockingAwait();
        List<Thesis> theses = thesisDao.getAll().blockingGet();

        Thesis initialThesis = theses.get(0);
        UserType userType = new UserType();
        userType.type = "STUDENT";
        userType.id = userTypeDao.insert(userType).blockingGet();

        User dbUser = new User();
        dbUser.username = "b";
        dbUser.name = "Lampe";
        dbUser.foreName = "Kai";
        dbUser.type = userType.id;
        dbUser.id = userDao.insert(dbUser).blockingGet();

        Student student = new Student();
        student.thesis = initialThesis.id;
        student.user = dbUser.id;
        studentDao.insert(student).blockingAwait();

        SupervisoryState draftSupervisoryState = supervisoryStateDao.getByState("DRAFT").blockingGet();

        List<Supervisor> supervisors = supervisorDao.getByUser(user.getUserId()).blockingGet();
        Supervisor supervisor = supervisors.get(0);
        supervisor.state = draftSupervisoryState.id;
        supervisorDao.update(supervisor).blockingAwait();

        List<ThesisModel> resultingTheses = thesisRepository.getSupervisorsThesesRequests(user).blockingGet();
        assertNotNull(resultingTheses);
        assertTrue(resultingTheses.isEmpty());
    }

    @Test
    public void testThatGetSupervisorsThesesRequestsWorksWithMixedTheses() {
        LoggedInUser user = insertBaseData();
        thesisRepository.createThesis("Test1", "TestDescription", user).blockingAwait();
        thesisRepository.createThesis("Test2", "TestDescription", user).blockingAwait();

        List<Thesis> theses = thesisDao.getAll().blockingGet();

        Thesis initialThesis = theses.get(0);
        UserType userType = new UserType();
        userType.type = "STUDENT";
        userType.id = userTypeDao.insert(userType).blockingGet();

        User dbUser = new User();
        dbUser.username = "b";
        dbUser.name = "Lampe";
        dbUser.foreName = "Kai";
        dbUser.type = userType.id;
        dbUser.id = userDao.insert(dbUser).blockingGet();

        Student student = new Student();
        student.thesis = initialThesis.id;
        student.user = dbUser.id;
        studentDao.insert(student).blockingAwait();

        SupervisoryState requestedSupervisoryState = new SupervisoryState();
        requestedSupervisoryState.state = "REQUESTED";
        requestedSupervisoryState.id = supervisoryStateDao.insert(requestedSupervisoryState).blockingGet();

        List<Supervisor> supervisors = supervisorDao.getByUser(user.getUserId()).blockingGet();
        Supervisor supervisor = supervisors.get(0);
        supervisor.state = requestedSupervisoryState.id;
        supervisorDao.update(supervisor).blockingAwait();

        SupervisoryState draftSupervisoryState = supervisoryStateDao.getByState("DRAFT").blockingGet();
        Thesis otherThesis = theses.get(1);
        Supervisor otherSupervisor = supervisors.get(1);
        otherSupervisor.state = draftSupervisoryState.id;
        otherSupervisor.thesis = otherThesis.id;

        List<ThesisModel> resultingTheses = thesisRepository.getSupervisorsThesesRequests(user).blockingGet();
        assertNotNull(resultingTheses);
        assertFalse(resultingTheses.isEmpty());
        assertEquals(1, resultingTheses.size());
        ThesisModel resultingThesis = resultingTheses.get(0);
        assertEquals("Test1", resultingThesis.getTitle());
    }

    @Test
    public void testThatAddSecondSupervisorToThesisWorks() {
        LoggedInUser user = insertBaseData();
        thesisRepository.createThesis("Test1", "TestDescription", user).blockingAwait();
        UserType userType = userTypeDao.getByType(UserTypeModel.SUPERVISOR.name()).blockingGet();

        User dbUser = new User();
        dbUser.username = "b";
        dbUser.name = "Lampe";
        dbUser.foreName = "Kai";
        dbUser.type = userType.id;
        dbUser.id = userDao.insert(dbUser).blockingGet();

        List<Thesis> theses = thesisDao.getAll().blockingGet();
        Thesis thesis = theses.get(0);

        SupervisoryState supervisoryStateRequested = new SupervisoryState();
        supervisoryStateRequested.state = SupervisoryStateModel.REQUESTED.name();
        supervisoryStateRequested.id = supervisoryStateDao.insert(supervisoryStateRequested).blockingGet();

        thesisRepository.addSecondSupervisorToThesis(thesis.id, dbUser.id).blockingAwait();

        List<Supervisor> supervisors = supervisorDao.getByUser(dbUser.id).blockingGet();
        assertNotNull(supervisors);
        assertFalse(supervisors.isEmpty());
        assertEquals(1, supervisors.size());

        List<Supervisor> thesisSupervisors = supervisorDao.getByThesis(thesis.id).blockingGet();
        assertNotNull(thesisSupervisors);
        assertFalse(thesisSupervisors.isEmpty());
        assertEquals(2, thesisSupervisors.size());
    }

    @Test
    public void testThatDeleteThesisSupervisorDraftWorks() {
        LoggedInUser user = insertBaseData();
        thesisRepository.createThesis("Test1", "TestDescription", user).blockingAwait();

        List<Thesis> theses = thesisDao.getAll().blockingGet();
        Thesis thesis = theses.get(0);
        thesisRepository.deleteThesisSupervisorDraft(thesis.id).blockingAwait();

        theses = thesisDao.getAll().blockingGet();
        assertTrue(theses.isEmpty());

        List<Supervisor> supervisors = supervisorDao.getAll().blockingGet();
        assertTrue(supervisors.isEmpty());
    }

    @Test
    public void testThatGetStudentThesisWorksBothSupervisors() {
        LoggedInUser user = insertBaseData();
        thesisRepository.createThesis("Test1", "TestDescription", user).blockingAwait();

        SupervisoryType secondSupervisorType = supervisoryTypeDao.getByType(SupervisoryTypeModel.SECOND_SUPERVISOR.name()).blockingGet();
        SupervisoryState supervisedState = supervisoryStateDao.getByState(SupervisoryStateModel.SUPERVISED.name()).blockingGet();
        ThesisState inProgressThesisState = thesisStateDao.getByState(ThesisStateModel.IN_PROGRESS.name()).blockingGet();
        InvoiceState unfinishedInvoiceState = invoiceStateDao.getByState(InvoiceStateModel.UNFINISHED.name()).blockingGet();

        List<Thesis> theses = thesisDao.getAll().blockingGet();
        Thesis thesis = theses.get(0);

        UserType supervisorUserType = userTypeDao.getByType(UserTypeModel.SUPERVISOR.name()).blockingGet();

        User secondSupervisorUser = new User();
        secondSupervisorUser.username = "b";
        secondSupervisorUser.type = supervisorUserType.id;
        secondSupervisorUser.id = userDao.insert(secondSupervisorUser).blockingGet();

        Supervisor supervisor = new Supervisor();
        supervisor.user = secondSupervisorUser.id;
        supervisor.thesis = thesis.id;
        supervisor.state = supervisedState.id;
        supervisor.type = secondSupervisorType.id;
        supervisor.invoiceState = unfinishedInvoiceState.id;
        supervisorDao.insert(supervisor).blockingAwait();

        thesis.state = inProgressThesisState.id;
        thesisDao.update(thesis).blockingAwait();

        UserType studentUserType = userTypeDao.getByType(UserTypeModel.STUDENT.name()).blockingGet();

        User studentUser = new User();
        studentUser.username = "c";
        studentUser.foreName = "This is";
        studentUser.name = "a test student";
        studentUser.type = studentUserType.id;
        studentUser.id = userDao.insert(studentUser).blockingGet();
        LoggedInUser studentLoggedInUser = new LoggedInUser(studentUser.id, studentUser.username, UserTypeModel.STUDENT);

        Student student = new Student();
        student.user = studentUser.id;
        student.thesis = thesis.id;
        studentDao.insert(student).blockingAwait();

        ThesisModel thesisModel = thesisRepository.getStudentThesis(studentLoggedInUser).blockingGet();

        assertNotNull(thesisModel);
        assertEquals("Test1", thesisModel.getTitle());
        assertEquals("This is a test student", thesisModel.getStudentName());
    }

    @Test
    public void testThatGetStudentThesisWorkOnlyFirstSupervisor() {
        LoggedInUser user = insertBaseData();
        thesisRepository.createThesis("Test1", "TestDescription", user).blockingAwait();

        ThesisState inProgressThesisState = thesisStateDao.getByState(ThesisStateModel.IN_PROGRESS.name()).blockingGet();

        List<Thesis> theses = thesisDao.getAll().blockingGet();
        Thesis thesis = theses.get(0);

        thesis.state = inProgressThesisState.id;
        thesisDao.update(thesis).blockingAwait();

        UserType studentUserType = userTypeDao.getByType(UserTypeModel.STUDENT.name()).blockingGet();

        User studentUser = new User();
        studentUser.username = "c";
        studentUser.foreName = "This is";
        studentUser.name = "a test student";
        studentUser.type = studentUserType.id;
        studentUser.id = userDao.insert(studentUser).blockingGet();
        LoggedInUser studentLoggedInUser = new LoggedInUser(studentUser.id, studentUser.username, UserTypeModel.STUDENT);

        Student student = new Student();
        student.user = studentUser.id;
        student.thesis = thesis.id;
        studentDao.insert(student).blockingAwait();

        ThesisModel thesisModel = thesisRepository.getStudentThesis(studentLoggedInUser).blockingGet();

        assertNotNull(thesisModel);
        assertEquals("Test1", thesisModel.getTitle());
        assertEquals("This is a test student", thesisModel.getStudentName());
        assertEquals(" ", thesisModel.getSecondSupervisorName());
    }
}
