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
import com.example.supervisionapp.ui.list.AdvertisedThesesListAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentThesesRequests extends Fragment {

    private ViewModelAdvertisedTheses mViewModel;

    public static FragmentThesesRequests newInstance() {
        return new FragmentThesesRequests();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final List<AdvertisedThesesListItem> items = new ArrayList<>();
        items.add(new AdvertisedThesesListItem("Die Paarung Der Fliege", "Das Paarungsverhalten der gemeinen Fliege ist ein lange erforschtes Problem, das allerdings noch nicht tiefgreifend genug erforscht wurde."));
        items.add(new AdvertisedThesesListItem("Die Paarung Der Fliege", "Das Paarungsverhalten der gemeinen Fliege ist ein lange erforschtes Problem, das allerdings noch nicht tiefgreifend genug erforscht wurde."));
        AdvertisedThesesListAdapter advertisedThesesListAdapter = new AdvertisedThesesListAdapter(getActivity(), items);
        ListView listView = (ListView) getView().findViewById(R.id.fragment_advertised_theses_advertisedTheses);
        listView.setAdapter(advertisedThesesListAdapter);
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
        mViewModel = new ViewModelProvider(this).get(ViewModelAdvertisedTheses.class);
        // TODO: Use the ViewModel
    }
}
