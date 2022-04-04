package com.example.supervisionapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.supervisionapp.data.LoginRepository;
import com.example.supervisionapp.data.model.LoggedInUser;
import com.example.supervisionapp.data.model.SupervisoryStateModel;
import com.example.supervisionapp.data.model.SupervisoryTypeModel;
import com.example.supervisionapp.data.model.ThesisModel;
import com.example.supervisionapp.data.model.ThesisStateModel;
import com.example.supervisionapp.databinding.ActivitySuperviseThesisBinding;
import com.example.supervisionapp.persistence.AppDatabase;
import com.example.supervisionapp.persistence.Thesis;
import com.example.supervisionapp.persistence.ThesisRepository;
import com.example.supervisionapp.ui.login.LoginActivity;
import com.example.supervisionapp.ui.main.ViewModelSuperviseThesis;

import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ActivitySuperviseThesis extends AppCompatActivity {

    private ActivitySuperviseThesisBinding binding;
    private ViewModelSuperviseThesis mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuperviseThesisBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
                ThesisModel thesis = mViewModel.getThesis().getValue();
                intent.putExtra("thesisId", thesis.getThesisId());
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

        Button deleteDraftButton = findViewById(R.id.activity_supervise_thesis_buttonDeleteDraft);
        deleteDraftButton.setClickable(true);
        deleteDraftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThesisModel thesisModel = mViewModel.getThesis().getValue();
                AppDatabase appDatabase = AppDatabase.getDatabase(getApplicationContext());
                ThesisRepository thesisRepository = new ThesisRepository(appDatabase);
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivitySuperviseThesis.this);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        thesisRepository.deleteThesisSupervisorDraft(thesisModel.getThesisId()).blockingAwait();
                        finish();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // noop
                    }
                });
                builder.setMessage(getResources().getString(R.string.activity_supervise_thesis_dialog_delete_draft_text))
                        .setTitle(R.string.activity_supervise_thesis_dialog_delete_draft);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        mViewModel = new ViewModelProvider(this).get(ViewModelSuperviseThesis.class);
        mViewModel.getThesis().observe(this, new Observer<ThesisModel>() {
            @Override
            public void onChanged(ThesisModel thesisModel) {
                View secondSupervisorButton = findViewById(R.id.activity_supervise_thesis_buttonSecondSupervisor);
                secondSupervisorButton.setVisibility(View.GONE);
                // only allow to send requests for a second supervisor
                // if and only if user is the first supervisor
                // and the thesis is no draft anymore,
                // which means that a student is already supervised
                // TODO prevent second request if one already sent
                if (thesisModel.getSupervisoryType() == SupervisoryTypeModel.FIRST_SUPERVISOR
                        && !thesisModel.hasSecondSupervisor()
                        && thesisModel.getSupervisoryState() != SupervisoryStateModel.DRAFT) {
                    secondSupervisorButton.setVisibility(View.VISIBLE);
                }

                Button deleteDraftButton = findViewById(R.id.activity_supervise_thesis_buttonDeleteDraft);
                deleteDraftButton.setVisibility(View.GONE);
                if (thesisModel.getSupervisoryState() == SupervisoryStateModel.DRAFT) {
                    deleteDraftButton.setVisibility(View.VISIBLE);
                }

                // TODO: enums to string resources
                TextView titleText = findViewById(R.id.activity_supervise_thesis_textTitle);
                titleText.setText(thesisModel.getTitle());
                TextView subTitleText = findViewById(R.id.activity_supervise_thesis_textSubTitle);
                subTitleText.setText(thesisModel.getSubTitle());
                TextView supervisionStateText = findViewById(R.id.activity_supervise_thesis_textSupervisionState);
                supervisionStateText.setText(thesisModel.getSupervisoryState().toString());
                TextView studentText = findViewById(R.id.activity_supervise_thesis_textStudent);
                studentText.setText(thesisModel.getStudentName());
                View exposeText = findViewById(R.id.activity_supervise_thesis_textExpose);
                exposeText.setTag(thesisModel.getExpose());
                TextView statusText = findViewById(R.id.activity_supervise_thesis_textStatus);
                statusText.setText(thesisModel.getThesisState().toString());
                TextView invoiceStateText = findViewById(R.id.activity_supervise_thesis_textInvoiceStatus);
                invoiceStateText.setText(thesisModel.getInvoiceState().toString());
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
