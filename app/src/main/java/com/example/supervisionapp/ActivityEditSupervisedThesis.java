package com.example.supervisionapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.supervisionapp.data.LoginRepository;
import com.example.supervisionapp.data.model.InvoiceStateModel;
import com.example.supervisionapp.data.model.LoggedInUser;
import com.example.supervisionapp.data.model.SupervisoryTypeModel;
import com.example.supervisionapp.data.model.ThesisModel;
import com.example.supervisionapp.data.model.ThesisStateModel;
import com.example.supervisionapp.databinding.ActivityEditSupervisedThesisBinding;
import com.example.supervisionapp.persistence.AppDatabase;
import com.example.supervisionapp.persistence.Thesis;
import com.example.supervisionapp.persistence.ThesisRepository;
import com.example.supervisionapp.ui.login.LoginActivity;
import com.example.supervisionapp.ui.main.ViewModelEditSupervisedThesis;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ActivityEditSupervisedThesis extends AppCompatActivity {
    private ActivityEditSupervisedThesisBinding binding;
    private ViewModelEditSupervisedThesis mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditSupervisedThesisBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        View arrowBack = findViewById(R.id.activity_edit_supervised_thesis_backArrow);
        arrowBack.setClickable(true);
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        View logout = findViewById(R.id.activity_edit_supervised_thesis_appBar_logout);
        logout.setClickable(true);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickLogout(view);
            }
        });

        mViewModel = new ViewModelProvider(this).get(ViewModelEditSupervisedThesis.class);
        mViewModel.getThesis().observe(this, new Observer<ThesisModel>() {
            @Override
            public void onChanged(ThesisModel thesisModel) {
                TextView supervisorText = findViewById(R.id.activity_edit_supervised_thesis_textSupervisor);
                supervisorText.setText(thesisModel.getSecondSupervisorName());

                TextView titleText = findViewById(R.id.activity_edit_supervised_thesis_textTitle);
                titleText.setText(thesisModel.getTitle());

                TextView subTitleText = findViewById(R.id.activity_edit_supervised_thesis_textSubTitle);
                String subtitle = thesisModel.getSubTitle();
                if (subtitle != null && !subtitle.isEmpty()) {
                    subTitleText.setText(thesisModel.getSubTitle());
                } else {
                    subTitleText.setText(R.string.empty_subtitle);
                }

                TextView supervisionStateText = findViewById(R.id.activity_edit_supervised_thesis_textSupervisionState);
                supervisionStateText.setText(getResources().getString(thesisModel.getSupervisoryState().getResourceId()));

                TextView studentText = findViewById(R.id.activity_edit_supervised_thesis_textStudent);
                studentText.setText(thesisModel.getStudentName());

                Spinner statusSpinner = findViewById(R.id.activity_edit_supervised_thesis_statusSpinner);
                TextView statusText = findViewById(R.id.activity_edit_supervised_thesis_statusText);

                if (thesisModel.getSupervisoryType() == SupervisoryTypeModel.FIRST_SUPERVISOR) {
                    statusSpinner.setVisibility(View.VISIBLE);
                    ArrayAdapter<String> thesisStateAdapter = new ArrayAdapter<>(ActivityEditSupervisedThesis.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
                    for (ThesisStateModel thesisState : ThesisStateModel.values()) {
                        if (thesisState.getSortPosition() >= thesisModel.getThesisState().getSortPosition()) {
                            thesisStateAdapter.add(getResources().getString(thesisState.getResourceId()));
                        }
                    }
                    thesisStateAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
                    statusSpinner.setAdapter(thesisStateAdapter);

                    statusText.setVisibility(View.GONE);
                } else {
                    statusSpinner.setVisibility(View.GONE);
                    statusText.setVisibility(View.VISIBLE);
                    statusText.setText(getResources().getString(thesisModel.getThesisState().getResourceId()));
                }


                Spinner invoiceStateText = findViewById(R.id.activity_edit_supervised_thesis_textInvoiceStatus);
                ArrayAdapter<String> invoiceStateAdapter = new ArrayAdapter<>(ActivityEditSupervisedThesis.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
                for (InvoiceStateModel invoiceState : InvoiceStateModel.values()) {
                    if (invoiceState.getSortPosition() >= thesisModel.getInvoiceState().getSortPosition()) {
                        if (invoiceState.getSortPosition() > InvoiceStateModel.UNFINISHED.getSortPosition()
                                && thesisModel.getThesisState().getSortPosition() <= ThesisStateModel.TURNED_IN.getSortPosition()) {
                            continue;
                        }
                        if (invoiceState == InvoiceStateModel.INVOICE_PAID
                                && thesisModel.getInvoiceState().getSortPosition() < InvoiceStateModel.INVOICE_PAID.getSortPosition()) {
                            continue;
                        }
                        invoiceStateAdapter.add(getResources().getString(invoiceState.getResourceId()));
                    }
                }
                invoiceStateAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
                invoiceStateText.setAdapter(invoiceStateAdapter);

                if (thesisModel.getExpose() != null
                        && !thesisModel.getExpose().isEmpty()) {
                    ImageView exposeIcon = findViewById(R.id.activity_edit_supervised_thesis_iconExpose);
                    exposeIcon.setClickable(true);
                    exposeIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.parse(thesisModel.getExpose()), "application/pdf");
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            Intent sendIntent = Intent.createChooser(intent, null);
                            startActivity(sendIntent);
                        }
                    });
                }
            }
        });

        Map<String, ThesisStateModel> thesisStateMapping = new HashMap<>();
        for (ThesisStateModel thesisState : ThesisStateModel.values()) {
            thesisStateMapping.put(getResources().getString(thesisState.getResourceId()), thesisState);
        }

        Map<String, InvoiceStateModel> invoiceStateMapping = new HashMap<>();
        for (InvoiceStateModel invoiceState : InvoiceStateModel.values()) {
            invoiceStateMapping.put(getResources().getString(invoiceState.getResourceId()), invoiceState);
        }

        Button buttonSave = findViewById(R.id.activity_edit_supervised_thesis_buttonSave);
        buttonSave.setClickable(true);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppDatabase appDatabase = AppDatabase.getDatabase(getApplicationContext());
                ThesisRepository thesisRepository = new ThesisRepository(appDatabase);
                LoginRepository loginRepository = LoginRepository.getInstance(null);
                LoggedInUser loggedInUser = loginRepository.getLoggedInUser();
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEditSupervisedThesis.this);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ThesisModel currentlyEditedThesis = mViewModel.getThesis().getValue();
                        thesisRepository
                                .getThesisById(currentlyEditedThesis.getThesisId())
                                .blockingSubscribe(new Consumer<Thesis>() {
                                    @Override
                                    public void accept(Thesis thesis) throws Throwable {
                                        Spinner statusText = findViewById(R.id.activity_edit_supervised_thesis_statusSpinner);
                                        String thesisState = (String) statusText.getSelectedItem();
                                        ThesisStateModel thesisStateModel = thesisStateMapping.get(thesisState);

                                        Spinner invoiceStateText = findViewById(R.id.activity_edit_supervised_thesis_textInvoiceStatus);
                                        String invoiceState = (String) invoiceStateText.getSelectedItem();
                                        InvoiceStateModel invoiceStateModel = invoiceStateMapping.get(invoiceState);

                                        thesisRepository
                                                .updateThesis(
                                                        thesis.id,
                                                        loggedInUser,
                                                        thesisStateModel,
                                                        invoiceStateModel)
                                                .observeOn(Schedulers.io())
                                                .blockingAwait();
                                        finish();
                                    }
                                });
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // noop
                    }
                });
                builder.setMessage(R.string.activity_edit_supervised_thesis_dialog_question)
                        .setTitle(R.string.activity_edit_supervised_thesis_dialog_title);

                AlertDialog dialog = builder.create();
                dialog.show();
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
