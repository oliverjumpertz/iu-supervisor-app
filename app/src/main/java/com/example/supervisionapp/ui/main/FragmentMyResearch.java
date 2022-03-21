package com.example.supervisionapp.ui.main;

import android.app.Activity;
import android.content.Intent;
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

import com.example.supervisionapp.ActivityAdvertiseThesis;
import com.example.supervisionapp.R;
import com.example.supervisionapp.data.LoginRepository;
import com.example.supervisionapp.data.list.model.MyResearchListItem;
import com.example.supervisionapp.data.model.LoggedInUser;
import com.example.supervisionapp.persistence.AppDatabase;
import com.example.supervisionapp.persistence.Thesis;
import com.example.supervisionapp.persistence.ThesisRepository;
import com.example.supervisionapp.ui.list.MyResearchListAdapter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.functions.Consumer;

public class FragmentMyResearch extends Fragment {
    private static final int REQUEST_CODE = 1;

    private ViewModelMyResearch mViewModel;
    private boolean initialized = false;

    public static FragmentMyResearch newInstance() {
        return new FragmentMyResearch();
    }

    private void updateData() {
        AppDatabase appDatabase = AppDatabase.getDatabase(SupervisorApplication.getAppContext());
        ThesisRepository thesisRepository = new ThesisRepository(appDatabase);

        LoggedInUser loggedInUser = LoginRepository.getInstance(null).getLoggedInUser();
        thesisRepository.getSupervisorsAdvertisedTheses(loggedInUser)
                .subscribe(new Consumer<List<Thesis>>() {
                    @Override
                    public void accept(List<Thesis> theses) throws Throwable {
                        final List<MyResearchListItem> items = new ArrayList<>(theses.size());
                        for (Thesis thesis : theses) {
                            items.add(new MyResearchListItem(thesis.id, thesis.title, thesis.description));
                        }
                        mViewModel.setMyResearchTheses(items);
                    }
                });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View buttonAdd = view.findViewById(R.id.fragment_my_research_advertiseThesisButton);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ActivityAdvertiseThesis.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        initialized = true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            updateData();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (initialized && isVisibleToUser) {
            updateData();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_research, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView listView = getView().findViewById(R.id.fragment_my_research_myResearch);
        mViewModel = new ViewModelProvider(this).get(ViewModelMyResearch.class);
        mViewModel.getMyResearchTheses().observe(getViewLifecycleOwner(), new Observer<List<MyResearchListItem>>() {
            @Override
            public void onChanged(List<MyResearchListItem> items) {
                MyResearchListAdapter myResearchListAdapter = new MyResearchListAdapter(getActivity(), items);
                listView.setAdapter(myResearchListAdapter);
            }
        });
        updateData();
    }
}
