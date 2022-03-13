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
import com.example.supervisionapp.data.list.model.MyResearchListItem;
import com.example.supervisionapp.databinding.FragmentMyResearchBinding;
import com.example.supervisionapp.ui.list.AdvertisedThesesListAdapter;
import com.example.supervisionapp.ui.list.MyResearchListAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentMyResearch extends Fragment {

    private ViewModelMyResearch mViewModel;

    public static FragmentMyResearch newInstance() {
        return new FragmentMyResearch();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final List<MyResearchListItem> items = new ArrayList<>();
        items.add(new MyResearchListItem("Die Paarung Der Fliege", "Das Paarungsverhalten der gemeinen Fliege ist ein lange erforschtes Problem, das allerdings noch nicht tiefgreifend genug erforscht wurde."));
        items.add(new MyResearchListItem("Die Paarung Der Fliege", "Das Paarungsverhalten der gemeinen Fliege ist ein lange erforschtes Problem, das allerdings noch nicht tiefgreifend genug erforscht wurde."));
        MyResearchListAdapter advertisedThesesListAdapter = new MyResearchListAdapter(getActivity(), items);
        ListView listView = (ListView) getView().findViewById(R.id.fragment_my_research_myResearch);
        listView.setAdapter(advertisedThesesListAdapter);
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
        mViewModel = new ViewModelProvider(this).get(ViewModelMyResearch.class);
        // TODO: Use the ViewModel
    }
}
