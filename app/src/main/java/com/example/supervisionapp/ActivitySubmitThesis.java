package com.example.supervisionapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.supervisionapp.data.LoginRepository;
import com.example.supervisionapp.data.model.LoggedInUser;
import com.example.supervisionapp.data.model.SupervisionRequestModel;
import com.example.supervisionapp.data.model.SupervisionRequestTypeModel;
import com.example.supervisionapp.databinding.ActivitySubmitThesisBinding;
import com.example.supervisionapp.persistence.AppDatabase;
import com.example.supervisionapp.persistence.Thesis;
import com.example.supervisionapp.persistence.ThesisRepository;
import com.example.supervisionapp.ui.login.LoginActivity;
import com.example.supervisionapp.ui.main.ViewModelSubmitThesis;
import com.example.supervisionapp.ui.main.ViewModelSuperviseThesis;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.MaybeObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ActivitySubmitThesis extends AppCompatActivity {
    private static final String LOG_TAG = "ActivitySubmitThesis";

    private ActivitySubmitThesisBinding binding;
    private ViewModelSubmitThesis mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubmitThesisBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        View arrowBack = findViewById(R.id.activity_submit_thesis_backArrow);
        arrowBack.setClickable(true);
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        View buttonSend = findViewById(R.id.activity_submit_thesis_buttonSend);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppDatabase appDatabase = AppDatabase.getDatabase(getApplicationContext());
                ThesisRepository thesisRepository = new ThesisRepository(appDatabase);
                LoginRepository loginRepository = LoginRepository.getInstance(null);
                LoggedInUser loggedInUser = loginRepository.getLoggedInUser();
                EditText subTitleView = findViewById(R.id.activity_submit_thesis_plainTextSubTitle);
                Thesis thesis = mViewModel.getThesis().getValue();
                EditText descriptionView = findViewById(R.id.activity_submit_thesis_multiLineTextDescription);
                thesisRepository
                        .requestSupervision(thesis.id,
                                loggedInUser,
                                subTitleView.getText().toString(),
                                descriptionView.getText().toString(),
                                // TODO exposé
                                "file:///foo")
                        .blockingSubscribe(new CompletableObserver() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {
                                // noop
                            }

                            @Override
                            public void onComplete() {
                                finish();
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                // TODO: error message
                                Log.e(LOG_TAG,
                                        "Couldn't request supervision, see attached stacktrace",
                                        e);
                                Toast
                                        .makeText(ActivitySubmitThesis.this,
                                                R.string.error_request_not_sent,
                                                Toast.LENGTH_LONG)
                                        .show();
                            }
                        });
                finish();
            }
        });

        View logout = findViewById(R.id.activity_submit_thesis_appBar_logout);
        logout.setClickable(true);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickLogout(view);
            }
        });

        mViewModel = new ViewModelProvider(this).get(ViewModelSubmitThesis.class);
        mViewModel.getThesis().observe(this, new Observer<Thesis>() {
            @Override
            public void onChanged(Thesis thesis) {
                if (thesis == null) {
                    return;
                }
                TextView title = findViewById(R.id.activity_submit_thesis_plainTextTitle);
                title.setText(thesis.title);

                EditText subTitle = findViewById(R.id.activity_submit_thesis_plainTextSubTitle);
                subTitle.setText(thesis.subtitle);

                EditText description = findViewById(R.id.activity_submit_thesis_multiLineTextDescription);
                description.setText(thesis.description);
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
                .getThesisById(thesisId)
                .observeOn(Schedulers.io())
                .subscribe(new MaybeObserver<Thesis>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        // noop
                    }

                    @Override
                    public void onSuccess(@NonNull Thesis thesis) {
                        mViewModel.setThesis(thesis);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        mViewModel.setThesis(null);
                    }
                });
    }
}
