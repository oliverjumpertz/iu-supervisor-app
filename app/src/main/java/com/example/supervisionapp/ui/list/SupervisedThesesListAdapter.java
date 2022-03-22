package com.example.supervisionapp.ui.list;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.supervisionapp.ActivitySuperviseThesis;
import com.example.supervisionapp.ActivityViewThesisRequest;
import com.example.supervisionapp.R;
import com.example.supervisionapp.data.list.model.SupervisedThesesListItem;

import java.util.ArrayList;
import java.util.List;

public class SupervisedThesesListAdapter extends BaseAdapter implements View.OnClickListener {
    private Context context;
    private List<SupervisedThesesListItem> items;

    public SupervisedThesesListAdapter(Context context, List<SupervisedThesesListItem> items) {
        this.context = context;
        this.items = new ArrayList<>(items);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.supervised_theses_list_item_row, viewGroup, false);
        TextView title = (TextView) row.findViewById(R.id.supervised_theses_list_item_row_textTitle);
        TextView student = (TextView) row.findViewById(R.id.supervised_theses_list_item_row_textStudent);
        TextView supervisoryType = (TextView) row.findViewById(R.id.supervised_theses_list_item_row_type);
        SupervisedThesesListItem item = items.get(i);
        title.setText(item.getTitle());
        student.setText(item.getStudent());
        supervisoryType.setText(item.getSupervisoryType().toString());
        View linearLayout = row.findViewById(R.id.supervised_theses_list_item_row_horizontalLinearLayout);
        linearLayout.setClickable(true);
        linearLayout.setOnClickListener(this);
        View innerLinearLayout = row.findViewById(R.id.supervised_theses_list_item_row_verticalLinearLayout);
        innerLinearLayout.setClickable(true);
        innerLinearLayout.setOnClickListener(this);
        title.setClickable(true);
        title.setOnClickListener(this);
        student.setClickable(true);
        student.setOnClickListener(this);
        supervisoryType.setClickable(true);
        supervisoryType.setOnClickListener(this);
        return row;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            // TODO
            case R.id.supervised_theses_list_item_row_horizontalLinearLayout:
            case R.id.supervised_theses_list_item_row_verticalLinearLayout:
            case R.id.supervised_theses_list_item_row_textTitle:
            case R.id.supervised_theses_list_item_row_textStudent:
            case R.id.supervised_theses_list_item_row_type:
                Intent intent = new Intent(context, ActivitySuperviseThesis.class);
                context.startActivity(intent);
                // TODO: send bundle so activity knows what to load or show
                break;
            default:
                throw new IllegalStateException("Unrecognized id passed to onClick handler");
        }
    }
}
