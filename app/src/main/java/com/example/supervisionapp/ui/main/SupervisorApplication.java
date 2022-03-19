package com.example.supervisionapp.ui.main;

import android.app.Application;
import android.content.Context;

public class SupervisorApplication extends Application {
    private static Context context;

    public static Context getAppContext() {
        return SupervisorApplication.context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SupervisorApplication.context = getApplicationContext();
    }
}
