package com.example.supervisionapp.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.supervisionapp.R;

public class FragmentMyThesis extends Fragment {

    private ViewModelMyThesis mViewModel;

    public static FragmentMyThesis newInstance() {
        return new FragmentMyThesis();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView title = (TextView) view.findViewById(R.id.headerTitle);
        title.setText(R.string.fragment_my_thesis_header_title_no_thesis);
        view.findViewById(R.id.headerFirstSupervisor).setVisibility(TextView.INVISIBLE);
        view.findViewById(R.id.textFirstSupervisor).setVisibility(TextView.INVISIBLE);
        view.findViewById(R.id.headerSecondSupervisor).setVisibility(TextView.INVISIBLE);
        view.findViewById(R.id.textSecondSupervisor).setVisibility(TextView.INVISIBLE);
        view.findViewById(R.id.headerExpose).setVisibility(TextView.INVISIBLE);
        view.findViewById(R.id.todoIcon).setVisibility(TextView.INVISIBLE);
        view.findViewById(R.id.headerStatus).setVisibility(TextView.INVISIBLE);
        view.findViewById(R.id.textStatus).setVisibility(TextView.INVISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_thesis, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ViewModelMyThesis.class);
        // TODO: Use the ViewModel
    }
}
