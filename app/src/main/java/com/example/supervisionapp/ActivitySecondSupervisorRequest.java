package com.example.supervisionapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.supervisionapp.data.LoginRepository;
import com.example.supervisionapp.data.list.model.MyResearchListItem;
import com.example.supervisionapp.data.list.model.SecondSupervisorRequestListItem;
import com.example.supervisionapp.data.model.LoggedInUser;
import com.example.supervisionapp.data.model.ThesisModel;
import com.example.supervisionapp.data.model.User;
import com.example.supervisionapp.databinding.ActivityAdvertiseThesisBinding;
import com.example.supervisionapp.databinding.ActivitySecondSupervisorRequestBinding;
import com.example.supervisionapp.persistence.AppDatabase;
import com.example.supervisionapp.persistence.ThesisRepository;
import com.example.supervisionapp.persistence.UserRepository;
import com.example.supervisionapp.ui.list.MyResearchListAdapter;
import com.example.supervisionapp.ui.list.SecondSupervisorRequestListAdapter;
import com.example.supervisionapp.ui.login.LoginActivity;
import com.example.supervisionapp.ui.main.ViewModelSecondSupervisorRequest;
import com.example.supervisionapp.ui.main.ViewModelSuperviseThesis;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ActivitySecondSupervisorRequest extends AppCompatActivity {

    private ActivitySecondSupervisorRequestBinding binding;
    private ViewModelSecondSupervisorRequest mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySecondSupervisorRequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

        mViewModel = new ViewModelProvider(this).get(ViewModelSecondSupervisorRequest.class);
        mViewModel.getUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                ListView listView = findViewById(R.id.activity_second_supervisor_request_supervisors);
                TextView emptyList = findViewById(R.id.activity_second_supervisor_request_emptySupervisors);
                ThesisModel thesis = mViewModel.getThesis().getValue();
                List<SecondSupervisorRequestListItem> items = new ArrayList<>();
                if (users == null || users.isEmpty()) {
                    listView.setVisibility(View.GONE);
                    listView.invalidate();
                    emptyList.setVisibility(View.VISIBLE);
                    emptyList.invalidate();
                    return;
                }
                for (User user : users) {
                    items.add(new SecondSupervisorRequestListItem(user.getId(), thesis.getThesisId(), user.getForename() + " " + user.getName()));
                }
                SecondSupervisorRequestListAdapter listAdapter = new SecondSupervisorRequestListAdapter(
                        ActivitySecondSupervisorRequest.this,
                        items,
                        new Runnable() {
                            @Override
                            public void run() {
                                loadData(thesis.getThesisId());
                            }
                        }
                );
                listView.setAdapter(listAdapter);
                listView.setVisibility(View.VISIBLE);
                listView.invalidate();
                emptyList.setVisibility(View.GONE);
                emptyList.invalidate();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return;
        }
        Long thesisId = bundle.getLong("thesisId");
        if (thesisId == null) {
            return;
        }
        loadData(thesisId);
    }

    public void onClickLogout(View view) {
        LoginRepository loginRepository = LoginRepository.getInstance(null);
        loginRepository.logout();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        // prevent users from going back in history after they logged out
        finish();
    }

    private void loadData(long thesisId) {
        AppDatabase appDatabase = AppDatabase.getDatabase(getApplicationContext());
        LoginRepository loginRepository = LoginRepository.getInstance(null);
        LoggedInUser loggedInUser = loginRepository.getLoggedInUser();
        ThesisRepository thesisRepository = new ThesisRepository(appDatabase);
        UserRepository userRepository = new UserRepository(appDatabase);
        thesisRepository
                .getThesisByIdAndUser(thesisId, loggedInUser)
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<ThesisModel>() {
                    @Override
                    public void accept(ThesisModel thesisModel) throws Throwable {
                        if (thesisModel == null) {
                            return;
                        }
                        mViewModel.setThesis(thesisModel);
                    }
                });
        userRepository
                .getEligibleSecondSupervisors(loggedInUser)
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<List<User>>() {
                    @Override
                    public void accept(List<User> users) throws Throwable {
                        if (users == null) {
                            return;
                        }
                        mViewModel.setUsers(users);
                    }
                });
    }
}
