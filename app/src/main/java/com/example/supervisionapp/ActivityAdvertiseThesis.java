package com.example.supervisionapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.supervisionapp.persistence.ThesisRepository;
import com.example.supervisionapp.persistence.ThesisState;
import com.example.supervisionapp.persistence.ThesisStateDao;
import com.example.supervisionapp.ui.login.LoginActivity;

public class ActivityAdvertiseThesis extends AppCompatActivity {
    private static final String ACTIVITY_LOG_TAG = "ActivityAdvertiseThesis";

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
                AppDatabase appDatabase = AppDatabase.getDatabase(getApplicationContext());
                TextView titleView = findViewById(R.id.activity_advertise_thesis_plainTextTitle);
                TextView descriptionView = findViewById(R.id.activity_advertise_thesis_textViewDescription);

                ThesisRepository thesisRepository = new ThesisRepository(appDatabase);
                try {
                    thesisRepository.createThesis(titleView.getText().toString(), descriptionView.getText().toString(), loggedInUser).blockingAwait();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("title", titleView.getText().toString());
                    resultIntent.putExtra("description", descriptionView.getText().toString());
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                } catch (Exception e) {
                    Log.e(ACTIVITY_LOG_TAG, "Could not save thesis. See attached stack trace.", e);
                    Toast.makeText(ActivityAdvertiseThesis.this, R.string.error_thesis_not_saved, Toast.LENGTH_LONG).show();
                }
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
