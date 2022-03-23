package com.example.supervisionapp.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.supervisionapp.R;
import com.example.supervisionapp.data.LoginRepository;
import com.example.supervisionapp.data.list.model.SupervisedThesesListItem;
import com.example.supervisionapp.data.model.LoggedInUser;
import com.example.supervisionapp.data.model.SupervisoryTypeModel;
import com.example.supervisionapp.data.model.ThesisModel;
import com.example.supervisionapp.persistence.AppDatabase;
import com.example.supervisionapp.persistence.ThesisRepository;
import com.example.supervisionapp.ui.list.SupervisedThesesListAdapter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.functions.Consumer;

public class FragmentSupervisedThesis extends Fragment {

    private ViewModelSupervisedThesis mViewModel;
    private boolean initialized = false;

    public static FragmentSupervisedThesis newInstance() {
        return new FragmentSupervisedThesis();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_supervised_thesis, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialized = true;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView listView = getView().findViewById(R.id.fragment_supervised_thesis_supervisedTheses);
        mViewModel = new ViewModelProvider(this).get(ViewModelSupervisedThesis.class);
        mViewModel.getSupervisedTheses().observe(getViewLifecycleOwner(), new Observer<List<SupervisedThesesListItem>>() {
            @Override
            public void onChanged(List<SupervisedThesesListItem> items) {
                SupervisedThesesListAdapter myResearchListAdapter = new SupervisedThesesListAdapter(getActivity(), items);
                listView.setAdapter(myResearchListAdapter);
            }
        });
        updateData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (initialized && isVisibleToUser) {
            updateData();
        }
    }

    private void updateData() {
        AppDatabase appDatabase = AppDatabase.getDatabase(SupervisorApplication.getAppContext());
        ThesisRepository thesisRepository = new ThesisRepository(appDatabase);

        LoggedInUser loggedInUser = LoginRepository.getInstance(null).getLoggedInUser();
        thesisRepository.getSupervisorsSupervisedTheses(loggedInUser)
                .subscribe(new Consumer<List<ThesisModel>>() {
                    @Override
                    public void accept(List<ThesisModel> theses) throws Throwable {
                        final List<SupervisedThesesListItem> items = new ArrayList<>(theses.size());
                        for (ThesisModel thesis : theses) {
                            items.add(new SupervisedThesesListItem(thesis.getThesisId(), thesis.getTitle(), thesis.getStudentName(), thesis.getSupervisoryType()));
                        }
                        mViewModel.setSupervisedTheses(items);
                    }
                });
    }
}
