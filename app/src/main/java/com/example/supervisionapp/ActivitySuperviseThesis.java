package com.example.supervisionapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.supervisionapp.data.LoginRepository;
import com.example.supervisionapp.databinding.ActivitySubmitThesisBinding;
import com.example.supervisionapp.databinding.ActivitySuperviseThesisBinding;
import com.example.supervisionapp.ui.login.LoginActivity;

public class ActivitySuperviseThesis extends AppCompatActivity {

    private ActivitySuperviseThesisBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperviseThesisBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // TODO: retrieve bundle and use to fill ViewModel, etc.

        View arrowBack = findViewById(R.id.activity_supervise_thesis_backArrow);
        arrowBack.setClickable(true);
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        View buttonSecondSupervisorRequest = findViewById(R.id.activity_supervise_thesis_buttonSecondSupervisor);
        buttonSecondSupervisorRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivitySuperviseThesis.this, ActivitySecondSupervisorRequest.class);
                // TODO put data bundle
                startActivity(intent);
            }
        });

        View logout = findViewById(R.id.activity_supervise_thesis_appBar_logout);
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
