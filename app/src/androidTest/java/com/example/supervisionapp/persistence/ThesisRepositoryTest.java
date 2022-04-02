package com.example.supervisionapp.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.supervisionapp.data.model.InvoiceStateModel;
import com.example.supervisionapp.data.model.LoggedInUser;
import com.example.supervisionapp.data.model.SupervisionRequestTypeModel;
import com.example.supervisionapp.data.model.SupervisoryStateModel;
import com.example.supervisionapp.data.model.SupervisoryTypeModel;
import com.example.supervisionapp.data.model.ThesisModel;
import com.example.supervisionapp.data.model.SupervisionRequestModel;
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
    private SupervisionRequestTypeDao supervisionRequestTypeDao;
    private SupervisionRequestDao supervisionRequestDao;

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
        supervisionRequestTypeDao = appDatabase.supervisionRequestTypeDao();
        supervisionRequestDao = appDatabase.supervisionRequestDao();
    }

    @After
    public void closeDb() {
        appDatabase.close();
    }

    private LoggedInUser insertBaseData() {
        ThesisState advertisedThesisState = new ThesisState();
        advertisedThesisState.state = ThesisStateModel.ADVERTISED.name();

        advertisedThesisState.id = thesisStateDao.insert(advertisedThesisState).blockingGet();

        ThesisState inProgressThesisState = new ThesisState();
        inProgressThesisState.state = ThesisStateModel.IN_PROGRESS.name();

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
    public void testThatGetSupervisionRequestsForUserWorksWithSingleRequest() {
        LoggedInUser user = insertBaseData();
        thesisRepository.createThesis("Test1", "TestDescription", user).blockingAwait();

        List<Thesis> theses = thesisDao.getAll().blockingGet();
        Thesis thesis = theses.get(0);

        UserType studentUserType = userTypeDao.getByType(UserTypeModel.STUDENT.name()).blockingGet();

        User studentUser = new User();
        studentUser.foreName = "Karl";
        studentUser.name = "Test-Student";
        studentUser.type = studentUserType.id;
        studentUser.id = userDao.insert(studentUser).blockingGet();

        SupervisionRequestType supervisionRequestType = new SupervisionRequestType();
        supervisionRequestType.type = SupervisionRequestTypeModel.SUPERVISION.name();
        supervisionRequestType.id = supervisionRequestTypeDao.insert(supervisionRequestType).blockingGet();

        SupervisionRequest supervisionRequest = new SupervisionRequest();
        supervisionRequest.thesis = thesis.id;
        supervisionRequest.user = studentUser.id;
        supervisionRequest.type = supervisionRequestType.id;
        supervisionRequestDao.insert(supervisionRequest).blockingAwait();

        List<SupervisionRequestModel> requests = thesisRepository.getSupervisionRequestsForUser(user).blockingGet();
        assertNotNull(requests);
        assertFalse(requests.isEmpty());
        assertEquals(1, requests.size());
    }

    @Test
    public void testThatGetSupervisionRequestsForUserWorksWithMultipleRequests() {
        LoggedInUser user = insertBaseData();
        thesisRepository.createThesis("Test1", "TestDescription", user).blockingAwait();

        List<Thesis> theses = thesisDao.getAll().blockingGet();
        Thesis thesis = theses.get(0);

        UserType studentUserType = userTypeDao.getByType(UserTypeModel.STUDENT.name()).blockingGet();

        User studentUserOne = new User();
        studentUserOne.foreName = "Karl";
        studentUserOne.name = "Test-Student";
        studentUserOne.type = studentUserType.id;
        studentUserOne.id = userDao.insert(studentUserOne).blockingGet();

        SupervisionRequestType supervisionRequestType = new SupervisionRequestType();
        supervisionRequestType.type = SupervisionRequestTypeModel.SUPERVISION.name();
        supervisionRequestType.id = supervisionRequestTypeDao.insert(supervisionRequestType).blockingGet();

        SupervisionRequest supervisionRequestOne = new SupervisionRequest();
        supervisionRequestOne.thesis = thesis.id;
        supervisionRequestOne.user = studentUserOne.id;
        supervisionRequestOne.type = supervisionRequestType.id;
        supervisionRequestDao.insert(supervisionRequestOne).blockingAwait();

        User studentUserTwo = new User();
        studentUserTwo.foreName = "Peter";
        studentUserTwo.name = "Test-Student";
        studentUserTwo.type = studentUserType.id;
        studentUserTwo.id = userDao.insert(studentUserTwo).blockingGet();

        SupervisionRequest supervisionRequestTwo = new SupervisionRequest();
        supervisionRequestTwo.thesis = thesis.id;
        supervisionRequestTwo.user = studentUserTwo.id;
        supervisionRequestTwo.type = supervisionRequestType.id;
        supervisionRequestDao.insert(supervisionRequestTwo).blockingAwait();

        List<SupervisionRequestModel> requests = thesisRepository.getSupervisionRequestsForUser(user).blockingGet();
        assertNotNull(requests);
        assertFalse(requests.isEmpty());
        assertEquals(2, requests.size());
    }

    @Test
    public void testThatGetSupervisionRequestsForUserWorksOnEmptyResult() {
        LoggedInUser user = insertBaseData();
        thesisRepository.createThesis("Test1", "TestDescription", user).blockingAwait();

        List<SupervisionRequestModel> requests = thesisRepository.getSupervisionRequestsForUser(user).blockingGet();
        assertNotNull(requests);
        assertTrue(requests.isEmpty());
    }

    @Test
    public void testThatRequestSecondSupervisorWorks() {
        LoggedInUser user = insertBaseData();
        thesisRepository.createThesis("Test1", "TestDescription", user).blockingAwait();
        UserType userType = userTypeDao.getByType(UserTypeModel.SUPERVISOR.name()).blockingGet();

        User dbUser = new User();
        dbUser.username = "b";
        dbUser.name = "Lampe";
        dbUser.foreName = "Kai";
        dbUser.type = userType.id;
        dbUser.id = userDao.insert(dbUser).blockingGet();
        LoggedInUser loggedInUser = new LoggedInUser(
                dbUser.id,
                dbUser.username,
                UserTypeModel.STUDENT
        );

        List<Thesis> theses = thesisDao.getAll().blockingGet();
        Thesis thesis = theses.get(0);

        SupervisionRequestType supervisionRequestType = new SupervisionRequestType();
        supervisionRequestType.type = SupervisionRequestTypeModel.SUPERVISION.name();
        supervisionRequestType.id = supervisionRequestTypeDao.insert(supervisionRequestType).blockingGet();

        thesisRepository.requestSupervision(
                thesis.id,
                loggedInUser,
                "Test subtitle",
                "Test description",
                "file:///foo"
        ).blockingAwait();

        List<SupervisionRequest> supervisionRequests = supervisionRequestDao.getAll().blockingGet();
        assertNotNull(supervisionRequests);
        assertFalse(supervisionRequests.isEmpty());
        assertEquals(1, supervisionRequests.size());
    }

    @Test
    public void testThatRequestSupervisonWorks() {
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

        SupervisionRequestType supervisionRequestType = new SupervisionRequestType();
        supervisionRequestType.type = SupervisionRequestTypeModel.SECOND_SUPERVISOR.name();
        supervisionRequestType.id = supervisionRequestTypeDao.insert(supervisionRequestType).blockingGet();

        thesisRepository.requestSecondSupervisor(thesis.id, dbUser.id).blockingAwait();

        List<SupervisionRequest> supervisionRequests = supervisionRequestDao.getAll().blockingGet();
        assertNotNull(supervisionRequests);
        assertFalse(supervisionRequests.isEmpty());
        assertEquals(1, supervisionRequests.size());
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

    @Test
    public void testThatGetAdvertisedThesesWorksWhenResultSetIsEmpty() {
        insertBaseData();

        List<Thesis> advertisedTheses = thesisRepository.getAdvertisedTheses().blockingGet();
        assertNotNull(advertisedTheses);
        assertTrue(advertisedTheses.isEmpty());
    }

    @Test
    public void testThatGetAdvertisedThesesWorksWithOneDraft() {
        LoggedInUser user = insertBaseData();
        thesisRepository.createThesis("Test1", "TestDescription", user).blockingAwait();

        List<Thesis> advertisedTheses = thesisRepository.getAdvertisedTheses().blockingGet();
        assertNotNull(advertisedTheses);
        assertFalse(advertisedTheses.isEmpty());
        assertEquals(1, advertisedTheses.size());
    }

    @Test
    public void testThatGetAdvertisedThesesWorksWithMultipleDrafts() {
        LoggedInUser user = insertBaseData();
        thesisRepository.createThesis("Test1", "TestDescription", user).blockingAwait();
        thesisRepository.createThesis("Test2", "TestDescription", user).blockingAwait();
        thesisRepository.createThesis("Test3", "TestDescription", user).blockingAwait();

        List<Thesis> advertisedTheses = thesisRepository.getAdvertisedTheses().blockingGet();
        assertNotNull(advertisedTheses);
        assertFalse(advertisedTheses.isEmpty());
        assertEquals(3, advertisedTheses.size());
    }

    @Test
    public void testThatGetAdvertisedThesesWorksWithMultipleMixedTheses() {
        LoggedInUser user = insertBaseData();
        thesisRepository.createThesis("Test1", "TestDescription", user).blockingAwait();
        thesisRepository.createThesis("Test2", "TestDescription", user).blockingAwait();
        thesisRepository.createThesis("Test3", "TestDescription", user).blockingAwait();

        List<Thesis> theses = thesisDao.getAll().blockingGet();
        Thesis thesisToModify = theses.get(1);
        ThesisState inProgressThesisState = thesisStateDao.getByState(ThesisStateModel.IN_PROGRESS.name()).blockingGet();
        thesisToModify.state = inProgressThesisState.id;
        thesisDao.update(thesisToModify).blockingAwait();

        List<Thesis> advertisedTheses = thesisRepository.getAdvertisedTheses().blockingGet();
        assertNotNull(advertisedTheses);
        assertFalse(advertisedTheses.isEmpty());
        assertEquals(2, advertisedTheses.size());
    }

    @Test
    public void testThatGetThesisByIdWorksWhenThesisExists() {
        LoggedInUser user = insertBaseData();
        thesisRepository.createThesis("Test1", "TestDescription", user).blockingAwait();

        Thesis thesis = thesisRepository.getThesisById(1).blockingGet();
        assertNotNull(thesis);
    }

    @Test
    public void testThatGetThesisByIdWorksWhenThesisDoesNotExist() {
        Thesis thesis = thesisRepository.getThesisById(1).blockingGet();
        assertNull(thesis);
    }

    @Test
    public void testThatGetSupervisionRequestByThesisAndUserWorksWhenThesisExists() {
        LoggedInUser user = insertBaseData();
        thesisRepository.createThesis("Test1", "TestDescription", user).blockingAwait();

        List<Thesis> theses = thesisDao.getAll().blockingGet();
        Thesis thesis = theses.get(0);

        UserType studentUserType = userTypeDao.getByType(UserTypeModel.STUDENT.name()).blockingGet();

        User studentUser = new User();
        studentUser.foreName = "Karl";
        studentUser.name = "Test-Student";
        studentUser.type = studentUserType.id;
        studentUser.id = userDao.insert(studentUser).blockingGet();

        SupervisionRequestType supervisionRequestType = new SupervisionRequestType();
        supervisionRequestType.type = SupervisionRequestTypeModel.SUPERVISION.name();
        supervisionRequestType.id = supervisionRequestTypeDao.insert(supervisionRequestType).blockingGet();

        SupervisionRequest supervisionRequest = new SupervisionRequest();
        supervisionRequest.thesis = thesis.id;
        supervisionRequest.user = studentUser.id;
        supervisionRequest.type = supervisionRequestType.id;
        supervisionRequestDao.insert(supervisionRequest).blockingAwait();

        SupervisionRequestModel result = thesisRepository.getSupervisionRequestByThesisAndUser(thesis.id, studentUser.id).blockingGet();
        assertNotNull(result);
        assertEquals(thesis.id, result.getThesisId());
        assertEquals(studentUser.id, result.getRequestingUserId());
    }

    @Test
    public void testThatGetSupervisionRequestByThesisAndUserWorksWhenThesisDoesNotExist() {
        LoggedInUser user = insertBaseData();
        SupervisionRequestModel supervisionRequest = thesisRepository.getSupervisionRequestByThesisAndUser(1, user.getUserId()).blockingGet();
        assertNull(supervisionRequest);
    }

    @Test
    public void testThatRejectSupervisionRequestWorksWithNullInput() {
        thesisRepository.rejectSupervisionRequest(null).blockingAwait();
    }

    @Test
    public void testThatRejectSupervisionRequestWorks() {
        LoggedInUser user = insertBaseData();
        thesisRepository.createThesis("Test1", "TestDescription", user).blockingAwait();

        List<Thesis> theses = thesisDao.getAll().blockingGet();
        Thesis thesis = theses.get(0);

        UserType studentUserType = userTypeDao.getByType(UserTypeModel.STUDENT.name()).blockingGet();

        User studentUser = new User();
        studentUser.foreName = "Karl";
        studentUser.name = "Test-Student";
        studentUser.type = studentUserType.id;
        studentUser.id = userDao.insert(studentUser).blockingGet();

        SupervisionRequestType supervisionRequestType = new SupervisionRequestType();
        supervisionRequestType.type = SupervisionRequestTypeModel.SUPERVISION.name();
        supervisionRequestType.id = supervisionRequestTypeDao.insert(supervisionRequestType).blockingGet();

        SupervisionRequest supervisionRequest = new SupervisionRequest();
        supervisionRequest.thesis = thesis.id;
        supervisionRequest.user = studentUser.id;
        supervisionRequest.type = supervisionRequestType.id;
        supervisionRequestDao.insert(supervisionRequest).blockingAwait();

        SupervisionRequestModel request = thesisRepository
                .getSupervisionRequestByThesisAndUser(thesis.id, studentUser.id)
                .blockingGet();

        thesisRepository
                .rejectSupervisionRequest(request)
                .blockingAwait();

        List<SupervisionRequest> supervisionRequests = supervisionRequestDao
                .getAll()
                .blockingGet();
        assertTrue(supervisionRequests.isEmpty());
    }

    @Test
    public void testThatAcceptSupervisionRequestWorksForUsers() {
        LoggedInUser user = insertBaseData();
        thesisRepository.createThesis("Test1", "TestDescription", user).blockingAwait();

        List<Thesis> theses = thesisDao.getAll().blockingGet();
        Thesis thesis = theses.get(0);

        UserType studentUserType = userTypeDao.getByType(UserTypeModel.STUDENT.name()).blockingGet();

        User studentUser = new User();
        studentUser.foreName = "Karl";
        studentUser.name = "Test-Student";
        studentUser.type = studentUserType.id;
        studentUser.id = userDao.insert(studentUser).blockingGet();

        SupervisionRequestType supervisionRequestType = new SupervisionRequestType();
        supervisionRequestType.type = SupervisionRequestTypeModel.SUPERVISION.name();
        supervisionRequestType.id = supervisionRequestTypeDao.insert(supervisionRequestType).blockingGet();

        SupervisionRequest supervisionRequest = new SupervisionRequest();
        supervisionRequest.thesis = thesis.id;
        supervisionRequest.user = studentUser.id;
        supervisionRequest.type = supervisionRequestType.id;
        supervisionRequest.subtitle = "Test subtitle";
        supervisionRequest.description = "A test description";
        supervisionRequest.expose = "file:///foo";
        supervisionRequestDao.insert(supervisionRequest).blockingAwait();

        SupervisionRequestModel request = thesisRepository
                .getSupervisionRequestByThesisAndUser(thesis.id, studentUser.id)
                .blockingGet();

        thesisRepository
                .acceptSupervisionRequest(request)
                .blockingAwait();

        SupervisoryState supervisedState = supervisoryStateDao
                .getByState(SupervisoryStateModel.SUPERVISED.name())
                .blockingGet();

        SupervisoryType supervisoryType = supervisoryTypeDao
                .getByType(SupervisoryTypeModel.FIRST_SUPERVISOR.name())
                .blockingGet();

        Supervisor supervisor = supervisorDao
                .getByThesisAndType(thesis.id, supervisoryType.id)
                .blockingGet();
        assertNotNull(supervisor);
        assertEquals(supervisedState.id, supervisor.state);

        List<SupervisionRequest> supervisionRequests = supervisionRequestDao
                .getAll()
                .blockingGet();
        assertTrue(supervisionRequests.isEmpty());

        Student student = studentDao.getByThesis(thesis.id).blockingGet();
        assertNotNull(student);
        assertEquals(studentUser.id, student.user);
        assertEquals(thesis.id, student.thesis);

        Thesis updatedThesis = thesisDao.getById(supervisionRequest.thesis).blockingGet();
        assertNotNull(updatedThesis);
        assertEquals(supervisionRequest.subtitle, updatedThesis.subtitle);
        assertEquals(supervisionRequest.description, updatedThesis.description);
        assertEquals(supervisionRequest.expose, updatedThesis.expose);
    }

    @Test
    public void testThatAcceptSupervisionRequestWorksForSupervisors() {
        LoggedInUser user = insertBaseData();
        thesisRepository.createThesis("Test1", "TestDescription", user).blockingAwait();

        List<Thesis> theses = thesisDao.getAll().blockingGet();
        Thesis thesis = theses.get(0);

        UserType studentUserType = userTypeDao.getByType(UserTypeModel.STUDENT.name()).blockingGet();
        UserType supervisorUserType = userTypeDao.getByType(UserTypeModel.SUPERVISOR.name()).blockingGet();

        User studentUser = new User();
        studentUser.foreName = "Karl";
        studentUser.name = "Test-Student";
        studentUser.type = studentUserType.id;
        studentUser.id = userDao.insert(studentUser).blockingGet();

        User secondSupervisorUser = new User();
        secondSupervisorUser.foreName = "Peter";
        secondSupervisorUser.name = "Zweitbetreuer";
        secondSupervisorUser.type = supervisorUserType.id;
        secondSupervisorUser.id = userDao.insert(secondSupervisorUser).blockingGet();

        SupervisionRequestType secondSupervisorRequestType = new SupervisionRequestType();
        secondSupervisorRequestType.type = SupervisionRequestTypeModel.SECOND_SUPERVISOR.name();
        secondSupervisorRequestType.id = supervisionRequestTypeDao.insert(secondSupervisorRequestType).blockingGet();

        SupervisionRequest supervisionRequest = new SupervisionRequest();
        supervisionRequest.thesis = thesis.id;
        supervisionRequest.user = secondSupervisorUser.id;
        supervisionRequest.type = secondSupervisorRequestType.id;
        supervisionRequestDao.insert(supervisionRequest).blockingAwait();

        SupervisionRequestModel request = thesisRepository
                .getSupervisionRequestByThesisAndUser(thesis.id, secondSupervisorUser.id)
                .blockingGet();

        thesisRepository
                .acceptSupervisionRequest(request)
                .blockingAwait();

        SupervisoryState supervisedState = supervisoryStateDao
                .getByState(SupervisoryStateModel.SUPERVISED.name())
                .blockingGet();

        SupervisoryType secondSupervisorSupervisoryType = supervisoryTypeDao
                .getByType(SupervisoryTypeModel.SECOND_SUPERVISOR.name())
                .blockingGet();

        Supervisor secondSupervisor = supervisorDao
                .getByThesisAndType(thesis.id, secondSupervisorSupervisoryType.id)
                .blockingGet();
        assertNotNull(secondSupervisor);
        assertEquals(supervisedState.id, secondSupervisor.state);

        List<SupervisionRequest> supervisionRequests = supervisionRequestDao
                .getAll()
                .blockingGet();
        assertTrue(supervisionRequests.isEmpty());
    }
}
