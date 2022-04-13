package com.example.supervisionapp.ui.main;

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
import com.example.supervisionapp.data.list.model.AdvertisedThesesListItem;
import com.example.supervisionapp.data.model.LoggedInUser;
import com.example.supervisionapp.data.model.UserThesisModel;
import com.example.supervisionapp.persistence.AppDatabase;
import com.example.supervisionapp.persistence.Thesis;
import com.example.supervisionapp.persistence.ThesisRepository;
import com.example.supervisionapp.ui.list.AdvertisedThesesListAdapter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.functions.Predicate;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FragmentAdvertisedTheses extends Fragment {

    private ViewModelAdvertisedTheses mViewModel;
    private boolean initialized = false;

    public static FragmentAdvertisedTheses newInstance() {
        return new FragmentAdvertisedTheses();
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
        return inflater.inflate(R.layout.fragment_advertised_theses, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView listView = getView().findViewById(R.id.fragment_advertised_theses_advertisedTheses);
        TextView emptyThesesView = getView().findViewById(R.id.fragment_advertised_theses_emptyTheses);
        mViewModel = new ViewModelProvider(this).get(ViewModelAdvertisedTheses.class);
        mViewModel.getAdvertisedTheses().observe(getViewLifecycleOwner(), new Observer<List<AdvertisedThesesListItem>>() {
            @Override
            public void onChanged(List<AdvertisedThesesListItem> items) {
                if (items != null && !items.isEmpty()) {
                    AdvertisedThesesListAdapter advertisedThesesListAdapter = new AdvertisedThesesListAdapter(getActivity(), items);
                    listView.setAdapter(advertisedThesesListAdapter);
                    listView.setVisibility(View.VISIBLE);
                    emptyThesesView.setVisibility(View.GONE);
                } else {
                    listView.setVisibility(View.GONE);
                    emptyThesesView.setVisibility(View.VISIBLE);
                }
                listView.invalidate();
                emptyThesesView.invalidate();
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (initialized && isVisibleToUser) {
            loadData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        AppDatabase appDatabase = AppDatabase.getDatabase(getContext());
        ThesisRepository thesisRepository = new ThesisRepository(appDatabase);
        LoginRepository loginRepository = LoginRepository.getInstance(null);
        LoggedInUser loggedInUser = loginRepository.getLoggedInUser();
        thesisRepository
                .getAdvertisedTheses(loggedInUser)
                .map(new Function<List<UserThesisModel>, List<UserThesisModel>>() {
                    @Override
                    public List<UserThesisModel> apply(List<UserThesisModel> userThesisModels) throws Throwable {
                        Boolean hasThesis = thesisRepository
                                .studentHasThesis(loggedInUser)
                                .blockingGet();
                        if (hasThesis != null && hasThesis) {
                            return new ArrayList<>();
                        }
                        return userThesisModels;
                    }
                })
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<List<UserThesisModel>>() {
                    @Override
                    public void accept(List<UserThesisModel> theses) throws Throwable {
                        List<AdvertisedThesesListItem> items = new ArrayList<>(theses.size());
                        for (UserThesisModel thesis : theses) {
                            items.add(new AdvertisedThesesListItem(
                                            thesis.getId(),
                                            thesis.getTitle(),
                                            thesis.getDescription(),
                                            thesis.isAlreadyRequested()
                                    )
                            );
                        }
                        mViewModel.setAdvertisedTheses(items);
                    }
                });
    }
}
