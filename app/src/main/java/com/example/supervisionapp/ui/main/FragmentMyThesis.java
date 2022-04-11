package com.example.supervisionapp.ui.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.supervisionapp.R;
import com.example.supervisionapp.data.LoginRepository;
import com.example.supervisionapp.data.model.LoggedInUser;
import com.example.supervisionapp.data.model.ThesisModel;
import com.example.supervisionapp.data.model.ThesisStateModel;
import com.example.supervisionapp.persistence.AppDatabase;
import com.example.supervisionapp.persistence.ThesisRepository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.MaybeObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FragmentMyThesis extends Fragment {
    private static final String LOG_TAG = "FragmentMyThesis";

    private ViewModelMyThesis mViewModel;
    private boolean initialized = false;

    public static FragmentMyThesis newInstance() {
        return new FragmentMyThesis();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialized = true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_thesis, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ViewModelMyThesis.class);
        mViewModel.getThesis().observe(getViewLifecycleOwner(), new Observer<ThesisModel>() {
            @Override
            public void onChanged(ThesisModel thesisModel) {
                View view = getView();
                TextView title = view.findViewById(R.id.fragment_my_thesis_headerTitle);
                if (thesisModel == null) {
                    title.setText(R.string.fragment_my_thesis_header_title_no_thesis);
                    setAllGone(view, Arrays.asList(
                            R.id.fragment_my_thesis_headerFirstSupervisor,
                            R.id.fragment_my_thesis_textFirstSupervisor,
                            R.id.fragment_my_thesis_headerSecondSupervisor,
                            R.id.fragment_my_thesis_textSecondSupervisor,
                            R.id.fragment_my_thesis_headerExpose,
                            R.id.fragment_my_thesis_iconExpose,
                            R.id.fragment_my_thesis_headerStatus,
                            R.id.fragment_my_thesis_textStatus
                    ));
                    return;
                }
                title.setText(R.string.fragment_my_thesis_header_title_thesis);

                TextView titleText = view.findViewById(R.id.fragment_my_thesis_textTitle);
                titleText.setText(thesisModel.getTitle());

                TextView firstSupervisor = view.findViewById(R.id.fragment_my_thesis_textFirstSupervisor);
                firstSupervisor.setText(thesisModel.getFirstSupervisorName());

                if (thesisModel.hasSecondSupervisor()) {
                    TextView secondSupervisorHeader = view.findViewById(R.id.fragment_my_thesis_headerSecondSupervisor);
                    secondSupervisorHeader.setVisibility(View.VISIBLE);
                    secondSupervisorHeader.setText(R.string.fragment_my_thesis_header_second_supervisor);

                    TextView secondSupervisor = view.findViewById(R.id.fragment_my_thesis_textSecondSupervisor);
                    secondSupervisor.setVisibility(View.VISIBLE);
                    secondSupervisor.setText(thesisModel.getSecondSupervisorName());
                }

                TextView headerExpose = view.findViewById(R.id.fragment_my_thesis_headerExpose);
                headerExpose.setVisibility(View.VISIBLE);
                headerExpose.setText(R.string.fragment_my_thesis_header_expose);

                TextView headerStatus = view.findViewById(R.id.fragment_my_thesis_headerStatus);
                headerStatus.setVisibility(View.VISIBLE);
                headerStatus.setText(R.string.fragment_my_thesis_header_status);

                TextView status = view.findViewById(R.id.fragment_my_thesis_textStatus);
                status.setText(thesisModel.getThesisState().getResourceId());

                if (thesisModel.getExpose() != null
                        && !thesisModel.getExpose().isEmpty()) {
                    ImageView exposeIcon = view.findViewById(R.id.fragment_my_thesis_iconExpose);
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
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (initialized && isVisibleToUser) {
            loadData();
        }
    }

    private void loadData() {
        AppDatabase appDatabase = AppDatabase.getDatabase(getContext());
        ThesisRepository thesisRepository = new ThesisRepository(appDatabase);
        LoggedInUser loggedInUser = LoginRepository.getInstance(null).getLoggedInUser();
        thesisRepository
                .getStudentThesis(loggedInUser)
                .observeOn(Schedulers.io())
                .subscribe(new MaybeObserver<ThesisModel>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        // noop
                    }

                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull ThesisModel thesisModel) {
                        mViewModel.setThesis(thesisModel);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Log.e(LOG_TAG, "An unexpected error occurred", e);
                        Toast.makeText(getContext(), getResources().getString(R.string.error_loading_data), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                        mViewModel.setThesis(null);
                    }
                });
    }

    private void setAllGone(View view, List<Integer> ids) {
        for (Integer id : ids) {
            view.findViewById(id).setVisibility(View.GONE);
        }
    }
}
