package com.example.supervisionapp.persistence;

import androidx.room.Database;
import androidx.room.RoomDatabase;

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
}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
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
