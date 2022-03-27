package com.example.supervisionapp.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.supervisionapp.persistence.AppDatabase;
import com.example.supervisionapp.persistence.ThesisRepository;

import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.core.MaybeObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FragmentMyThesis extends Fragment {
    private static final String LOG_TAG = "FragmentMyThesis";

    private ViewModelMyThesis mViewModel;

    public static FragmentMyThesis newInstance() {
        return new FragmentMyThesis();
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
                            R.id.fragment_my_thesis_todoIcon,
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

                TextView secondSupervisor = view.findViewById(R.id.fragment_my_thesis_textSecondSupervisor);
                secondSupervisor.setText(thesisModel.getSecondSupervisorName());

                // TODO: Exposé

                TextView status = view.findViewById(R.id.fragment_my_thesis_textStatus);
                // TODO: Enum to resource
                status.setText(thesisModel.getThesisState().name());

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
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
