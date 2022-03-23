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
import com.example.supervisionapp.data.list.model.MyResearchListItem;
import com.example.supervisionapp.data.list.model.ThesesRequestsListItem;
import com.example.supervisionapp.data.model.SupervisoryType;
import com.example.supervisionapp.ui.list.MyResearchListAdapter;
import com.example.supervisionapp.ui.list.ThesesRequestsListAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentThesesRequests extends Fragment {

    private ViewModelThesesRequests mViewModel;

    public static FragmentThesesRequests newInstance() {
        return new FragmentThesesRequests();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final List<ThesesRequestsListItem> items = new ArrayList<>();
        ThesesRequestsListAdapter advertisedThesesListAdapter = new ThesesRequestsListAdapter(getActivity(), items);
        ListView listView = getView().findViewById(R.id.fragment_theses_requests_thesesRequests);
        listView.setAdapter(advertisedThesesListAdapter);
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
        //mViewModel.getMyResearchTheses().observe(getViewLifecycleOwner(), new Observer<List<MyResearchListItem>>() {
        //    @Override
        //    public void onChanged(List<MyResearchListItem> items) {
        //        MyResearchListAdapter myResearchListAdapter = new MyResearchListAdapter(getActivity(), items);
        //        listView.setAdapter(myResearchListAdapter);
        //    }
        //});
        //updateData();
    }

    private void updateData() {

    }
}
