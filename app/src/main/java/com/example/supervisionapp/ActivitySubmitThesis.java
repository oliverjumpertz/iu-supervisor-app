package com.example.supervisionapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.supervisionapp.data.LoginRepository;
import com.example.supervisionapp.data.model.LoggedInUser;
import com.example.supervisionapp.databinding.ActivitySubmitThesisBinding;
import com.example.supervisionapp.persistence.AppDatabase;
import com.example.supervisionapp.persistence.Thesis;
import com.example.supervisionapp.persistence.ThesisRepository;
import com.example.supervisionapp.ui.login.LoginActivity;
import com.example.supervisionapp.ui.main.ViewModelSubmitThesis;

import java.io.File;
import java.net.URISyntaxException;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.MaybeObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ActivitySubmitThesis extends AppCompatActivity {
    private static final String LOG_TAG = "ActivitySubmitThesis";
    private static final int OPEN_PDF_REQUEST_CODE = 1337;

    private ActivitySubmitThesisBinding binding;
    private ViewModelSubmitThesis mViewModel;

    private volatile boolean isResumeAfterResult = false;

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
                ImageView uploadIcon = findViewById(R.id.activity_submit_thesis_uploadExposeIcon);
                String expose = (String) uploadIcon.getTag();
                if (expose == null || expose.isEmpty()) {
                    Toast
                            .makeText(
                                    ActivitySubmitThesis.this,
                                    R.string.no_expose_uploaded,
                                    Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                createSendToast();
                thesisRepository
                        .requestSupervision(
                                thesis.id,
                                loggedInUser,
                                subTitleView.getText().toString(),
                                descriptionView.getText().toString(),
                                expose)
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

        ImageView uploadIcon = findViewById(R.id.activity_submit_thesis_uploadExposeIcon);
        uploadIcon.setClickable(true);
        uploadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/pdf");
                startActivityForResult(intent, OPEN_PDF_REQUEST_CODE);
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

                EditText subtitleText = findViewById(R.id.activity_submit_thesis_plainTextSubTitle);
                String subtitle = thesis.subtitle;
                if (subtitle != null && !subtitle.isEmpty()) {
                    subtitleText.setText(thesis.subtitle);
                } else {
                    subtitleText.setText(R.string.empty_subtitle);
                }

                EditText description = findViewById(R.id.activity_submit_thesis_multiLineTextDescription);
                description.setText(thesis.description);

                ImageView uploadIcon = findViewById(R.id.activity_submit_thesis_uploadExposeIcon);
                uploadIcon.setTag(thesis.expose);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK
                && requestCode == OPEN_PDF_REQUEST_CODE) {
            if (data != null) {
                Uri uri = data.getData();
                Thesis thesis = mViewModel.getThesis().getValue();
                thesis.expose = uri.toString();
                mViewModel.setThesis(thesis);
                isResumeAfterResult = true;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isResumeAfterResult) {
            isResumeAfterResult = false;
            return;
        }
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
        // prevent users from going back in history after they logged out
        finishAffinity();
        startActivity(intent);
    }

    private void loadData(long thesisId) {
        AppDatabase appDatabase = AppDatabase.getDatabase(getApplicationContext());
        LoginRepository loginRepository = LoginRepository.getInstance(null);
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

    private void createSendToast() {
        Toast
                .makeText(
                        this,
                        R.string.activity_submit_thesis_sendMessage,
                        Toast.LENGTH_LONG)
                .show();
    }
}
