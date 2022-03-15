package com.example.supervisionapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.supervisionapp.data.LoginRepository;
import com.example.supervisionapp.data.list.model.MyResearchListItem;
import com.example.supervisionapp.data.list.model.SecondSupervisorRequestListItem;
import com.example.supervisionapp.databinding.ActivityAdvertiseThesisBinding;
import com.example.supervisionapp.databinding.ActivitySecondSupervisorRequestBinding;
import com.example.supervisionapp.ui.list.MyResearchListAdapter;
import com.example.supervisionapp.ui.list.SecondSupervisorRequestListAdapter;
import com.example.supervisionapp.ui.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;

public class ActivitySecondSupervisorRequest extends AppCompatActivity {

    private ActivitySecondSupervisorRequestBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySecondSupervisorRequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // TODO: retrieve bundle and use to fill ViewModel, etc.

        View arrowBack = findViewById(R.id.activity_second_supervisor_request_backArrow);
        arrowBack.setClickable(true);
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        View logout = findViewById(R.id.activity_second_supervisor_request_appBar_logout);
        logout.setClickable(true);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickLogout(view);
            }
        });

        final List<SecondSupervisorRequestListItem> items = new ArrayList<>();
        items.add(new SecondSupervisorRequestListItem("B. Scheuert"));
        items.add(new SecondSupervisorRequestListItem("B. Scheuert"));
        SecondSupervisorRequestListAdapter secondSupervisorRequestListAdapter = new SecondSupervisorRequestListAdapter(this, items);
        ListView listView = (ListView) findViewById(R.id.activity_second_supervisor_request_supervisors);
        listView.setAdapter(secondSupervisorRequestListAdapter);
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
