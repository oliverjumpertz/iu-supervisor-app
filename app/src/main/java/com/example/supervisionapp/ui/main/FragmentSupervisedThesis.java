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

public class FragmentSupervisedThesis extends Fragment {

    private ViewModelMyThesis mViewModel;

    public static FragmentSupervisedThesis newInstance() {
        return new FragmentSupervisedThesis();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView title = (TextView) view.findViewById(R.id.fragment_my_thesis_headerTitle);
        title.setText(R.string.fragment_my_thesis_header_title_no_thesis);
        view.findViewById(R.id.fragment_my_thesis_headerFirstSupervisor).setVisibility(TextView.INVISIBLE);
        view.findViewById(R.id.fragment_my_thesis_textFirstSupervisor).setVisibility(TextView.INVISIBLE);
        view.findViewById(R.id.fragment_my_thesis_headerSecondSupervisor).setVisibility(TextView.INVISIBLE);
        view.findViewById(R.id.fragment_my_thesis_textSecondSupervisor).setVisibility(TextView.INVISIBLE);
        view.findViewById(R.id.fragment_my_thesis_headerExpose).setVisibility(TextView.INVISIBLE);
        view.findViewById(R.id.fragment_my_thesis_todoIcon).setVisibility(TextView.INVISIBLE);
        view.findViewById(R.id.fragment_my_thesis_headerStatus).setVisibility(TextView.INVISIBLE);
        view.findViewById(R.id.fragment_my_thesis_textStatus).setVisibility(TextView.INVISIBLE);
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
        mViewModel = new ViewModelProvider(this).get(ViewModelMyThesis.class);
        // TODO: Use the ViewModel
    }
}
