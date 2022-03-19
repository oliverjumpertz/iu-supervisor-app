package com.example.supervisionapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.supervisionapp.data.LoginRepository;
import com.example.supervisionapp.data.model.LoggedInUser;
import com.example.supervisionapp.databinding.ActivityAdvertiseThesisBinding;
import com.example.supervisionapp.databinding.ActivitySubmitThesisBinding;
import com.example.supervisionapp.persistence.AppDatabase;
import com.example.supervisionapp.persistence.InvoiceState;
import com.example.supervisionapp.persistence.InvoiceStateDao;
import com.example.supervisionapp.persistence.Supervisor;
import com.example.supervisionapp.persistence.SupervisorDao;
import com.example.supervisionapp.persistence.SupervisoryState;
import com.example.supervisionapp.persistence.SupervisoryStateDao;
import com.example.supervisionapp.persistence.SupervisoryType;
import com.example.supervisionapp.persistence.SupervisoryTypeDao;
import com.example.supervisionapp.persistence.Thesis;
import com.example.supervisionapp.persistence.ThesisDao;
import com.example.supervisionapp.persistence.ThesisState;
import com.example.supervisionapp.persistence.ThesisStateDao;
import com.example.supervisionapp.ui.login.LoginActivity;

public class ActivityAdvertiseThesis extends AppCompatActivity {

    private ActivityAdvertiseThesisBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdvertiseThesisBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // TODO: retrieve bundle and use to fill ViewModel, etc.

        View arrowBack = findViewById(R.id.activity_advertise_thesis_backArrow);
        arrowBack.setClickable(true);
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        View buttonSend = findViewById(R.id.activity_advertise_thesis_buttonCreate);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginRepository loginRepository = LoginRepository.getInstance(null);
                LoggedInUser loggedInUser = loginRepository.getLoggedInUser();
                // TODO show loading progress bar?
                AppDatabase appDatabase = AppDatabase.getDatabase(getApplicationContext());
                TextView titleView = findViewById(R.id.activity_advertise_thesis_plainTextTitle);
                TextView descriptionView = findViewById(R.id.activity_advertise_thesis_textViewDescription);

                ThesisStateDao thesisStateDao = appDatabase.thesisStateDao();
                ThesisDao thesisDao = appDatabase.thesisDao();
                SupervisoryTypeDao supervisoryTypeDao = appDatabase.supervisoryTypeDao();
                SupervisorDao supervisorDao = appDatabase.supervisorDao();
                InvoiceStateDao invoiceStateDao = appDatabase.invoiceStateDao();
                SupervisoryStateDao supervisoryStateDao = appDatabase.supervisoryStateDao();

                // TODO: refactor into one DAO...more convenience, needs to run in transaction
                appDatabase.runInTransaction(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ThesisState advertisedThesisState = thesisStateDao.getByState("ADVERTISED").blockingGet();

                            Thesis thesis = new Thesis();
                            thesis.title = titleView.getText().toString();
                            thesis.description = descriptionView.getText().toString();
                            thesis.state = advertisedThesisState.id;
                            thesis.id = thesisDao.insert(thesis).blockingGet();

                            SupervisoryType supervisoryType = supervisoryTypeDao.getByType("FIRST_SUPERVISOR").blockingGet();
                            InvoiceState invoiceState = invoiceStateDao.getByType("UNFINISHED").blockingGet();

                            SupervisoryState draftSupervisoryState = supervisoryStateDao.getByState("DRAFT").blockingGet();

                            Supervisor supervisor = new Supervisor();
                            supervisor.user = loggedInUser.getUserId();
                            supervisor.thesis = thesis.id;
                            supervisor.state = draftSupervisoryState.id;
                            supervisor.type = supervisoryType.id;
                            supervisor.invoiceState = invoiceState.id;
                            supervisorDao.insert(supervisor).blockingGet();

                            finish();
                        } catch (Exception e) {
                            throw e;
                        }
                    }
                });
            }
        });

        View logout = findViewById(R.id.activity_advertise_thesis_appBar_logout);
        logout.setClickable(true);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickLogout(view);
            }
        });
    }

    public void onClickLogout(View view) {
        LoginRepository loginRepository = LoginRepository.getInstance(null);
        loginRepository.logout();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        // prevent users from going back in history after they logged out
        finish();
    }
}
