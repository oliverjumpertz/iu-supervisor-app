package com.example.supervisionapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.supervisionapp.data.LoginRepository;
import com.example.supervisionapp.data.model.LoggedInUser;
import com.example.supervisionapp.data.model.SupervisoryTypeModel;
import com.example.supervisionapp.data.model.ThesisModel;
import com.example.supervisionapp.databinding.ActivityViewThesisRequestBinding;
import com.example.supervisionapp.persistence.AppDatabase;
import com.example.supervisionapp.persistence.ThesisRepository;
import com.example.supervisionapp.ui.login.LoginActivity;
import com.example.supervisionapp.ui.main.ViewModelViewThesisRequest;

import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ActivityViewThesisRequest extends AppCompatActivity {

    private ActivityViewThesisRequestBinding binding;
    private ViewModelViewThesisRequest mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewThesisRequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

        mViewModel = new ViewModelProvider(this).get(ViewModelViewThesisRequest.class);
        mViewModel.getThesis().observe(this, new Observer<ThesisModel>() {
            @Override
            public void onChanged(ThesisModel thesisModel) {
                TextView titleView = findViewById(R.id.activity_view_thesis_request_thesis_textTitle);
                titleView.setText(thesisModel.getTitle());

                TextView subTitleView = findViewById(R.id.activity_view_thesis_request_thesis_textSubTitle);
                subTitleView.setText(thesisModel.getSubTitle());

                if (thesisModel.getSupervisoryType() == SupervisoryTypeModel.FIRST_SUPERVISOR) {
                    TextView title = findViewById(R.id.activity_view_thesis_request_thesis_type_title);
                    title.setText(R.string.activity_view_thesis_request_thesis_type_title_first);

                    TextView firstSupervisorTitleView = findViewById(R.id.activity_view_thesis_request_thesis_headerFirstSupervisor);
                    firstSupervisorTitleView.setVisibility(View.GONE);

                    TextView firstSupervisorView = findViewById(R.id.activity_view_thesis_request_thesis_textFirstSupervisor);
                    firstSupervisorView.setVisibility(View.GONE);
                } else if (thesisModel.getSupervisoryType() == SupervisoryTypeModel.SECOND_SUPERVISOR) {
                    TextView title = findViewById(R.id.activity_view_thesis_request_thesis_type_title);
                    title.setText(R.string.activity_view_thesis_request_thesis_type_title_second);

                    TextView firstSupervisorTitleView = findViewById(R.id.activity_view_thesis_request_thesis_headerFirstSupervisor);
                    firstSupervisorTitleView.setVisibility(View.VISIBLE);

                    TextView firstSupervisorView = findViewById(R.id.activity_view_thesis_request_thesis_textFirstSupervisor);
                    firstSupervisorView.setVisibility(View.VISIBLE);

                    firstSupervisorView.setText(thesisModel.getFirstSupervisorName());
                }

                TextView studentView = findViewById(R.id.activity_view_thesis_request_thesis_textStudent);
                studentView.setText(thesisModel.getStudentName());

                // TODO: exposé
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
    }
}
