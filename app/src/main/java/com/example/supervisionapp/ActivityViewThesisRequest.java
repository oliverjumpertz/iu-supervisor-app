package com.example.supervisionapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.supervisionapp.data.LoginRepository;
import com.example.supervisionapp.databinding.ActivitySubmitThesisBinding;
import com.example.supervisionapp.databinding.ActivityViewThesisRequestBinding;
import com.example.supervisionapp.ui.login.LoginActivity;

public class ActivityViewThesisRequest extends AppCompatActivity {

    private ActivityViewThesisRequestBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewThesisRequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // TODO: retrieve bundle and use to fill ViewModel, etc.

        View arrowBack = findViewById(R.id.activity_view_thesis_request_backArrow);
        arrowBack.setClickable(true);
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        View buttonSend = findViewById(R.id.activity_view_thesis_request_thesis_buttonAccept);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO submit...
                finish();
            }
        });
        View buttonReject = findViewById(R.id.activity_view_thesis_request_thesis_buttonReject);
        buttonReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO reject...
                finish();
            }
        });
        View logout = findViewById(R.id.activity_view_thesis_request_appBar_logout);
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
