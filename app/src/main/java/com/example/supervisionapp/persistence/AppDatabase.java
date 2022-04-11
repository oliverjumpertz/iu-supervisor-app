package com.example.supervisionapp.persistence;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.supervisionapp.data.model.InvoiceStateModel;
import com.example.supervisionapp.data.model.SupervisionRequestTypeModel;
import com.example.supervisionapp.data.model.SupervisoryStateModel;
import com.example.supervisionapp.data.model.SupervisoryTypeModel;
import com.example.supervisionapp.data.model.ThesisStateModel;
import com.example.supervisionapp.data.model.UserTypeModel;
import com.example.supervisionapp.utils.PasswordUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Database(entities = {
        InvoiceState.class,
        Student.class,
        Supervisor.class,
        SupervisoryState.class,
        SupervisoryType.class,
        Thesis.class,
        ThesisState.class,
        User.class,
        UserType.class,
        SupervisionRequest.class,
        SupervisionRequestType.class
}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String LOG_TAG = "AppDatabase";

    private static class DatabaseCallback extends RoomDatabase.Callback {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            ExecutorService databaseExecutor = Executors.newFixedThreadPool(1);
            databaseExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        INSTANCE.runInTransaction(new Runnable() {
                            @Override
                            public void run() {
                                UserTypeDao userTypeDao = INSTANCE.userTypeDao();
                                UserType userTypeStudent = new UserType();
                                userTypeStudent.type = UserTypeModel.STUDENT.name();
                                userTypeStudent.id = userTypeDao.insert(userTypeStudent).blockingGet();

                                UserType userTypeSupervisor = new UserType();
                                userTypeSupervisor.type = UserTypeModel.SUPERVISOR.name();
                                userTypeSupervisor.id = userTypeDao.insert(userTypeSupervisor).blockingGet();

                                UserDao userDao = INSTANCE.userDao();
                                User userOne = new User();
                                userOne.username = "a";
                                userOne.foreName = "Bernd";
                                userOne.name = "Scheuert";
                                userOne.title = "Prof. Dr. rer. nat.";
                                userOne.type = userTypeSupervisor.id;
                                userOne.password = PasswordUtils.createSha256("aaaa");
                                userOne.id = userDao.insert(userOne).blockingGet();

                                User userTwo = new User();
                                userTwo.username = "b";
                                userTwo.foreName = "Kai";
                                userTwo.name = "Lampe";
                                userTwo.type = userTypeStudent.id;
                                userTwo.password = PasswordUtils.createSha256("aaaa");
                                userTwo.id = userDao.insert(userTwo).blockingGet();

                                User userThree = new User();
                                userThree.username = "c";
                                userThree.foreName = "Bernd";
                                userThree.name = "Spaßvogel";
                                userThree.type = userTypeSupervisor.id;
                                userThree.password = PasswordUtils.createSha256("aaaa");
                                userThree.id = userDao.insert(userThree).blockingGet();

                                ThesisStateDao thesisStateDao = INSTANCE.thesisStateDao();

                                for (ThesisStateModel thesisState : ThesisStateModel.values()) {
                                    ThesisState newThesisState = new ThesisState();
                                    newThesisState.state = thesisState.name();
                                    newThesisState.id = thesisStateDao.insert(newThesisState).blockingGet();
                                }

                                SupervisoryTypeDao supervisoryTypeDao = INSTANCE.supervisoryTypeDao();
                                for (SupervisoryTypeModel supervisoryType : SupervisoryTypeModel.values()) {
                                    SupervisoryType newSupervisoryType = new SupervisoryType();
                                    newSupervisoryType.type = supervisoryType.name();
                                    newSupervisoryType.id = supervisoryTypeDao.insert(newSupervisoryType).blockingGet();
                                }

                                InvoiceStateDao invoiceStateDao = INSTANCE.invoiceStateDao();
                                for (InvoiceStateModel invoiceState : InvoiceStateModel.values()) {
                                    InvoiceState newInvoiceState = new InvoiceState();
                                    newInvoiceState.state = invoiceState.name();
                                    newInvoiceState.id = invoiceStateDao.insert(newInvoiceState).blockingGet();
                                }

                                SupervisoryStateDao supervisoryStateDao = INSTANCE.supervisoryStateDao();
                                for (SupervisoryStateModel supervisoryState : SupervisoryStateModel.values()) {
                                    SupervisoryState newSupervisoryState = new SupervisoryState();
                                    newSupervisoryState.state = supervisoryState.name();
                                    newSupervisoryState.id = supervisoryStateDao.insert(newSupervisoryState).blockingGet();
                                }

                                SupervisionRequestTypeDao supervisionRequestTypeDao = INSTANCE.supervisionRequestTypeDao();
                                for (SupervisionRequestTypeModel supervisionRequestType : SupervisionRequestTypeModel.values()) {
                                    SupervisionRequestType newSupervisionRequestType = new SupervisionRequestType();
                                    newSupervisionRequestType.type = supervisionRequestType.name();
                                    newSupervisionRequestType.id = supervisionRequestTypeDao.insert(newSupervisionRequestType).blockingGet();
                                }
                            }
                        });
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "There was an error when trying to set up the database", e);
                        throw e;
                    }
                }
            });
            // don't waste precious resources
            databaseExecutor.shutdown();
        }
    }

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room
                            .databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class,
                                    "app_database")
                            .allowMainThreadQueries()
                            .addCallback(new DatabaseCallback())
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract InvoiceStateDao invoiceStateDao();

    public abstract StudentDao studentDao();

    public abstract SupervisorDao supervisorDao();

    public abstract SupervisoryStateDao supervisoryStateDao();

    public abstract SupervisoryTypeDao supervisoryTypeDao();

    public abstract ThesisDao thesisDao();

    public abstract ThesisStateDao thesisStateDao();

    public abstract UserDao userDao();

    public abstract UserTypeDao userTypeDao();

    public abstract SupervisionRequestDao supervisionRequestDao();

    public abstract SupervisionRequestTypeDao supervisionRequestTypeDao();
}
