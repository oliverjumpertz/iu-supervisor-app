package com.example.supervisionapp.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.supervisionapp.R;
import com.example.supervisionapp.data.list.model.AdvertisedThesesListItem;
import com.example.supervisionapp.data.list.model.ThesesRequestsListItem;
import com.example.supervisionapp.data.model.SupervisoryType;
import com.example.supervisionapp.ui.list.AdvertisedThesesListAdapter;
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
        items.add(new ThesesRequestsListItem("Die Paarung Der Fliege", "B. Scheuert", SupervisoryType.FIRST_SUPERVISOR));
        items.add(new ThesesRequestsListItem("Die Paarung Der Fliege", "V. Cool", SupervisoryType.SECOND_SUPERVISOR));
        ThesesRequestsListAdapter advertisedThesesListAdapter = new ThesesRequestsListAdapter(getActivity(), items);
        ListView listView = (ListView) getView().findViewById(R.id.fragment_theses_requests_thesesRequests);
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
        mViewModel = new ViewModelProvider(this).get(ViewModelThesesRequests.class);
        // TODO: Use the ViewModel
    }
}
