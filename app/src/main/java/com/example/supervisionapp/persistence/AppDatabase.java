package com.example.supervisionapp.persistence;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.supervisionapp.data.model.InvoiceStateModel;
import com.example.supervisionapp.data.model.SupervisoryStateModel;
import com.example.supervisionapp.data.model.SupervisoryTypeModel;
import com.example.supervisionapp.data.model.ThesisStateModel;
import com.example.supervisionapp.data.model.UserTypeModel;
import com.example.supervisionapp.utils.PasswordUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {
        InvoiceState.class,
        Student.class,
        Supervisor.class,
        SupervisoryState.class,
        SupervisoryType.class,
        Thesis.class,
        ThesisState.class,
        User.class,
        UserType.class
}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(1);

    private static class DatabaseCallback extends RoomDatabase.Callback {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
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
                        userDao.insert(userOne).blockingGet();

                        User userTwo = new User();
                        userTwo.username = "b";
                        userTwo.foreName = "Kai";
                        userTwo.name = "Lampe";
                        userTwo.type = userTypeStudent.id;
                        userTwo.password = PasswordUtils.createSha256("aaaa");
                        userDao.insert(userTwo).blockingGet();

                        ThesisStateDao thesisStateDao = INSTANCE.thesisStateDao();
                        ThesisState advertisedThesisState = new ThesisState();
                        advertisedThesisState.state = ThesisStateModel.ADVERTISED.name();
                        advertisedThesisState.id = thesisStateDao.insert(advertisedThesisState).blockingGet();

                        ThesisState inProgressThesisState = new ThesisState();
                        inProgressThesisState.state = ThesisStateModel.IN_PROGRESS.name();
                        inProgressThesisState.id = thesisStateDao.insert(inProgressThesisState).blockingGet();

                        SupervisoryTypeDao supervisoryTypeDao = INSTANCE.supervisoryTypeDao();
                        SupervisoryType firstSupervisorType = new SupervisoryType();
                        firstSupervisorType.type = SupervisoryTypeModel.FIRST_SUPERVISOR.name();
                        supervisoryTypeDao.insert(firstSupervisorType).blockingGet();
                        SupervisoryType secondSupervisorType = new SupervisoryType();
                        secondSupervisorType.type = SupervisoryTypeModel.SECOND_SUPERVISOR.name();
                        supervisoryTypeDao.insert(secondSupervisorType).blockingGet();

                        InvoiceStateDao invoiceStateDao = INSTANCE.invoiceStateDao();
                        InvoiceState invoiceStateUnfinished = new InvoiceState();
                        invoiceStateUnfinished.state = InvoiceStateModel.UNFINISHED.name();
                        invoiceStateUnfinished.id = invoiceStateDao.insert(invoiceStateUnfinished).blockingGet();

                        SupervisoryStateDao supervisoryStateDao = INSTANCE.supervisoryStateDao();

                        SupervisoryState draftSupervisoryState = new SupervisoryState();
                        draftSupervisoryState.state = SupervisoryStateModel.DRAFT.name();
                        draftSupervisoryState.id = supervisoryStateDao.insert(draftSupervisoryState).blockingGet();

                        SupervisoryState supervisedSupervisoryState = new SupervisoryState();
                        supervisedSupervisoryState.state = SupervisoryStateModel.SUPERVISED.name();
                        supervisedSupervisoryState.id = supervisoryStateDao.insert(supervisedSupervisoryState).blockingGet();
                    } catch (Exception e) {
                        throw e;
                    }
                }
            });
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
}
