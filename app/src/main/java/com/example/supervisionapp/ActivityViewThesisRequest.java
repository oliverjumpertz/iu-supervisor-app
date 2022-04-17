package com.example.supervisionapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.example.supervisionapp.data.model.SupervisoryTypeModel;
import com.example.supervisionapp.data.model.ThesisModel;
import com.example.supervisionapp.databinding.ActivityViewThesisRequestBinding;
import com.example.supervisionapp.persistence.AppDatabase;
import com.example.supervisionapp.persistence.ThesisRepository;
import com.example.supervisionapp.ui.login.LoginActivity;
import com.example.supervisionapp.ui.main.ViewModelViewThesisRequest;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.MaybeObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ActivityViewThesisRequest extends AppCompatActivity {
    private static final String LOG_TAG = "ViewThesisRequest";

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
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
        View buttonAccept = findViewById(R.id.activity_view_thesis_request_thesis_buttonAccept);
        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppDatabase appDatabase = AppDatabase.getDatabase(getApplicationContext());
                ThesisRepository thesisRepository = new ThesisRepository(appDatabase);
                SupervisionRequestModel request = mViewModel
                        .getSupervisionRequest()
                        .getValue();
                createAcceptToast(request);
                thesisRepository
                        .acceptSupervisionRequest(request)
                        .subscribe(new CompletableObserver() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {
                                // noop
                            }

                            @Override
                            public void onComplete() {
                                setResult(Activity.RESULT_OK);
                                finish();
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                Log.e(LOG_TAG, "An error occurred", e);
                                Toast
                                        .makeText(
                                                ActivityViewThesisRequest.this,
                                                R.string.activity_view_thesis_request_thesis_accept_error,
                                                Toast.LENGTH_LONG
                                        )
                                        .show();
                            }
                        });
            }
        });
        View buttonReject = findViewById(R.id.activity_view_thesis_request_thesis_buttonReject);
        buttonReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SupervisionRequestModel request = mViewModel
                        .getSupervisionRequest()
                        .getValue();
                createRejectToast(request);
                AppDatabase appDatabase = AppDatabase.getDatabase(getApplicationContext());
                ThesisRepository thesisRepository = new ThesisRepository(appDatabase);
                SupervisionRequestModel supervisionRequest = mViewModel
                        .getSupervisionRequest()
                        .getValue();
                thesisRepository
                        .rejectSupervisionRequest(supervisionRequest)
                        .blockingAwait();
                setResult(Activity.RESULT_CANCELED);
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
        mViewModel.getSupervisionRequest().observe(this, new Observer<SupervisionRequestModel>() {
            @Override
            public void onChanged(SupervisionRequestModel supervisionRequest) {
                if (supervisionRequest == null) {
                    return;
                }

                TextView titleView = findViewById(R.id.activity_view_thesis_request_thesis_textTitle);
                titleView.setText(supervisionRequest.getTitle());

                TextView subTitleView = findViewById(R.id.activity_view_thesis_request_thesis_textSubTitle);
                subTitleView.setText(supervisionRequest.getSubTitle());

                if (supervisionRequest.getRequestType() == SupervisionRequestTypeModel.SUPERVISION) {
                    TextView title = findViewById(R.id.activity_view_thesis_request_thesis_type_title);
                    title.setText(R.string.activity_view_thesis_request_thesis_type_title_first);

                    TextView firstSupervisorTitleView = findViewById(R.id.activity_view_thesis_request_thesis_headerFirstSupervisor);
                    firstSupervisorTitleView.setVisibility(View.GONE);

                    TextView firstSupervisorView = findViewById(R.id.activity_view_thesis_request_thesis_textFirstSupervisor);
                    firstSupervisorView.setVisibility(View.GONE);
                } else if (supervisionRequest.getRequestType() == SupervisionRequestTypeModel.SECOND_SUPERVISOR) {
                    TextView title = findViewById(R.id.activity_view_thesis_request_thesis_type_title);
                    title.setText(R.string.activity_view_thesis_request_thesis_type_title_second);

                    TextView firstSupervisorTitleView = findViewById(R.id.activity_view_thesis_request_thesis_headerFirstSupervisor);
                    firstSupervisorTitleView.setVisibility(View.VISIBLE);

                    TextView firstSupervisorView = findViewById(R.id.activity_view_thesis_request_thesis_textFirstSupervisor);
                    firstSupervisorView.setVisibility(View.VISIBLE);

                    firstSupervisorView.setText(supervisionRequest.getFirstSupervisorName());
                }

                TextView studentView = findViewById(R.id.activity_view_thesis_request_thesis_textStudent);
                studentView.setText(supervisionRequest.getStudentName());

                if (supervisionRequest.getExpose() != null
                        && !supervisionRequest.getExpose().isEmpty()) {
                    ImageView exposeIcon = findViewById(R.id.activity_view_thesis_request_thesis_iconExpose);
                    exposeIcon.setClickable(true);
                    exposeIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.parse(supervisionRequest.getExpose()), "application/pdf");
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            Intent sendIntent = Intent.createChooser(intent, null);
                            startActivity(sendIntent);
                        }
                    });
                }
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
        Long userId = bundle.getLong("userId");
        if (userId == null) {
            return;
        }
        SupervisionRequestTypeModel requestType = (SupervisionRequestTypeModel) bundle.get("requestType");
        loadData(thesisId, userId, requestType);
    }

    public void onClickLogout(View view) {
        LoginRepository loginRepository = LoginRepository.getInstance(null);
        loginRepository.logout();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        // prevent users from going back in history after they logged out
        finish();
    }

    private void loadData(long thesisId, long userId, SupervisionRequestTypeModel requestType) {
        AppDatabase appDatabase = AppDatabase.getDatabase(getApplicationContext());
        ThesisRepository thesisRepository = new ThesisRepository(appDatabase);
        thesisRepository
                .getSupervisionRequestByThesisAndUser(thesisId, userId)
                .observeOn(Schedulers.io())
                .subscribe(new MaybeObserver<SupervisionRequestModel>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        // noop
                    }

                    @Override
                    public void onSuccess(@NonNull SupervisionRequestModel supervisionRequest) {
                        mViewModel.setSupervisionRequest(supervisionRequest);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(LOG_TAG, "An error occurred", e);
                    }

                    @Override
                    public void onComplete() {
                        mViewModel.setSupervisionRequest(null);
                    }
                });
    }

    private void createAcceptToast(SupervisionRequestModel request) {
        int messageId;
        if (request.getRequestType() == SupervisionRequestTypeModel.SECOND_SUPERVISOR) {
            messageId = R.string.activity_view_thesis_request_thesis_acceptInfoMessageSupervisor;
        } else {
            messageId = R.string.activity_view_thesis_request_thesis_acceptInfoMessageStudent;
        }
        Toast
                .makeText(
                        ActivityViewThesisRequest.this,
                        getResources().getString(messageId),
                        Toast.LENGTH_LONG
                )
                .show();
    }

    private void createRejectToast(SupervisionRequestModel request) {
        int messageId;
        if (request.getRequestType() == SupervisionRequestTypeModel.SECOND_SUPERVISOR) {
            messageId = R.string.activity_view_thesis_request_thesis_rejectInfoMessageSupervisor;
        } else {
            messageId = R.string.activity_view_thesis_request_thesis_rejectInfoMessageStudent;
        }
        Toast
                .makeText(
                        ActivityViewThesisRequest.this,
                        getResources().getString(messageId),
                        Toast.LENGTH_LONG
                )
                .show();
    }
}
