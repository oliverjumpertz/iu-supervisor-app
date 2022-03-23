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
import com.example.supervisionapp.data.list.model.MyResearchListItem;
import com.example.supervisionapp.data.list.model.ThesesRequestsListItem;
import com.example.supervisionapp.data.model.LoggedInUser;
import com.example.supervisionapp.data.model.SupervisoryType;
import com.example.supervisionapp.data.model.SupervisoryTypeModel;
import com.example.supervisionapp.data.model.ThesisModel;
import com.example.supervisionapp.persistence.AppDatabase;
import com.example.supervisionapp.persistence.Thesis;
import com.example.supervisionapp.persistence.ThesisRepository;
import com.example.supervisionapp.ui.list.MyResearchListAdapter;
import com.example.supervisionapp.ui.list.ThesesRequestsListAdapter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.functions.Consumer;

public class FragmentThesesRequests extends Fragment {
    public static FragmentThesesRequests newInstance() {
        return new FragmentThesesRequests();
    }

    private ViewModelThesesRequests mViewModel;
    private boolean initialized = false;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialized = true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_theses_requests, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView listView = getView().findViewById(R.id.fragment_theses_requests_thesesRequests);
        mViewModel = new ViewModelProvider(this).get(ViewModelThesesRequests.class);
        mViewModel.getThesesRequests().observe(getViewLifecycleOwner(), new Observer<List<ThesesRequestsListItem>>() {
            @Override
            public void onChanged(List<ThesesRequestsListItem> items) {
                ThesesRequestsListAdapter thesesRequestsListAdapter = new ThesesRequestsListAdapter(getActivity(), items);
                listView.setAdapter(thesesRequestsListAdapter);
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
        thesisRepository.getSupervisorsThesesRequests(loggedInUser)
                .subscribe(new Consumer<List<ThesisModel>>() {
                    @Override
                    public void accept(List<ThesisModel> theses) throws Throwable {
                        final List<ThesesRequestsListItem> items = new ArrayList<>(theses.size());
                        for (ThesisModel thesis : theses) {
                            items.add(new ThesesRequestsListItem(thesis.getThesisId(), thesis.getTitle(), thesis.getStudentName(), thesis.getSupervisoryType()));
                        }
                        if (items.isEmpty()) {
                            items.add(new ThesesRequestsListItem(1L, "Test Titel", "Test Student", SupervisoryTypeModel.FIRST_SUPERVISOR));
                        }
                        mViewModel.setThesesRequests(items);
                    }
                });
    }
}
