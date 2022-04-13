package com.example.supervisionapp.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.supervisionapp.R;
import com.example.supervisionapp.data.LoginRepository;
import com.example.supervisionapp.data.list.model.ThesesRequestsListItem;
import com.example.supervisionapp.data.model.LoggedInUser;
import com.example.supervisionapp.data.model.SupervisionRequestTypeModel;
import com.example.supervisionapp.data.model.SupervisionRequestModel;
import com.example.supervisionapp.persistence.AppDatabase;
import com.example.supervisionapp.persistence.ThesisRepository;
import com.example.supervisionapp.ui.list.ThesesRequestsListAdapter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.functions.Consumer;

public class FragmentThesesRequests extends Fragment {
    private static final int REQUEST_CODE = 1;

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
        TextView emptyThesesView = getView().findViewById(R.id.fragment_theses_requests_emptyTheses);
        mViewModel = new ViewModelProvider(this).get(ViewModelThesesRequests.class);
        mViewModel.getThesesRequests().observe(getViewLifecycleOwner(), new Observer<List<ThesesRequestsListItem>>() {
            @Override
            public void onChanged(List<ThesesRequestsListItem> items) {
                if (items != null && !items.isEmpty()) {
                    ThesesRequestsListAdapter thesesRequestsListAdapter = new ThesesRequestsListAdapter(
                            getActivity(),
                            items,
                            REQUEST_CODE,
                            FragmentThesesRequests.this);
                    listView.setAdapter(thesesRequestsListAdapter);
                    listView.setVisibility(View.VISIBLE);
                    emptyThesesView.setVisibility(View.GONE);
                } else {
                    listView.setVisibility(View.GONE);
                    emptyThesesView.setVisibility(View.VISIBLE);
                }
                listView.invalidate();
                emptyThesesView.invalidate();
                getView()
                        .findViewById(R.id.fragment_advertised_theses_constraintLayout)
                        .invalidate();
            }
        });
        loadData();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            loadData();
        }
    }

    private void loadData() {
        AppDatabase appDatabase = AppDatabase.getDatabase(SupervisorApplication.getAppContext());
        ThesisRepository thesisRepository = new ThesisRepository(appDatabase);

        LoggedInUser loggedInUser = LoginRepository.getInstance(null).getLoggedInUser();
        thesisRepository
                .getSupervisionRequestsForUser(loggedInUser)
                .subscribe(new Consumer<List<SupervisionRequestModel>>() {
                    @Override
                    public void accept(List<SupervisionRequestModel> supervisionRequestModels) throws Throwable {
                        final List<ThesesRequestsListItem> items = new ArrayList<>(supervisionRequestModels.size());
                        for (SupervisionRequestModel thesisRequest : supervisionRequestModels) {
                            String name;
                            if (thesisRequest.getRequestType() == SupervisionRequestTypeModel.SECOND_SUPERVISOR) {
                                name = thesisRequest.getFirstSupervisorName();
                            } else {
                                name = thesisRequest.getStudentName();
                            }
                            items.add(new ThesesRequestsListItem(thesisRequest.getThesisId(), thesisRequest.getRequestingUserId(), thesisRequest.getTitle(), name, thesisRequest.getRequestType()));
                        }
                        mViewModel.setThesesRequests(items);
                    }
                });
    }
}
