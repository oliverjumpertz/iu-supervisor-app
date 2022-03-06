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
import com.example.supervisionapp.data.list.model.ListItem;
import com.example.supervisionapp.ui.list.ListAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentAdvertisedTheses extends Fragment {

    private ViewModelAdvertisedTheses mViewModel;

    public static FragmentAdvertisedTheses newInstance() {
        return new FragmentAdvertisedTheses();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final List<ListItem> items = new ArrayList<>();
        items.add(new ListItem("Die Paarung Der Fliege", "Das Paarungsverhalten der gemeinen Fliege ist ein lange erforschtes Problem, das allerdings noch nicht tiefgreifend genug erforscht wurde."));
        items.add(new ListItem("Die Paarung Der Fliege", "Das Paarungsverhalten der gemeinen Fliege ist ein lange erforschtes Problem, das allerdings noch nicht tiefgreifend genug erforscht wurde."));
        ListAdapter listAdapter = new ListAdapter(getActivity(), items);
        ListView listView = (ListView) getView().findViewById(R.id.advertisedTheses);
        listView.setAdapter(listAdapter);
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
