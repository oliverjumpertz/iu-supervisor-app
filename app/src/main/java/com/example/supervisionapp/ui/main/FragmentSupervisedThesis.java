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
import com.example.supervisionapp.data.list.model.SupervisedThesesListItem;
import com.example.supervisionapp.data.list.model.ThesesRequestsListItem;
import com.example.supervisionapp.data.model.SupervisoryType;
import com.example.supervisionapp.ui.list.SupervisedThesesListAdapter;
import com.example.supervisionapp.ui.list.ThesesRequestsListAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentSupervisedThesis extends Fragment {

    private ViewModelSupervisedThesis mViewModel;

    public static FragmentSupervisedThesis newInstance() {
        return new FragmentSupervisedThesis();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final List<SupervisedThesesListItem> items = new ArrayList<>();
        items.add(new SupervisedThesesListItem("Die Paarung Der Fliege", "B. Scheuert", SupervisoryType.FIRST_SUPERVISOR));
        items.add(new SupervisedThesesListItem("Die Paarung Der Fliege", "V. Cool", SupervisoryType.SECOND_SUPERVISOR));
        SupervisedThesesListAdapter advertisedThesesListAdapter = new SupervisedThesesListAdapter(getActivity(), items);
        ListView listView = (ListView) getView().findViewById(R.id.fragment_supervised_thesis_supervisedTheses);
        listView.setAdapter(advertisedThesesListAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_supervised_thesis, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ViewModelSupervisedThesis.class);
        // TODO: Use the ViewModel
    }
}
