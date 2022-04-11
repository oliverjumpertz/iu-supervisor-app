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
import com.example.supervisionapp.data.list.model.MyResearchListItem;
import com.example.supervisionapp.data.list.model.SupervisedThesesListItem;
import com.example.supervisionapp.data.model.SupervisoryTypeModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        String index = String.valueOf(i);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row = inflater.inflate(R.layout.supervised_theses_list_item_row, viewGroup, false);
        TextView title = row.findViewById(R.id.supervised_theses_list_item_row_textTitle);
        TextView student = row.findViewById(R.id.supervised_theses_list_item_row_textStudent);
        TextView supervisoryType = row.findViewById(R.id.supervised_theses_list_item_row_type);
        SupervisedThesesListItem item = items.get(i);

        title.setText(item.getTitle());
        title.setTag(index);
        title.setClickable(true);
        title.setOnClickListener(this);

        student.setText(item.getStudent());
        student.setTag(index);
        student.setClickable(true);
        student.setOnClickListener(this);

        supervisoryType.setText(item.getSupervisoryType().getResourceId());
        supervisoryType.setClickable(true);
        supervisoryType.setOnClickListener(this);
        supervisoryType.setTag(index);

        View linearLayout = row.findViewById(R.id.supervised_theses_list_item_row_horizontalLinearLayout);
        linearLayout.setClickable(true);
        linearLayout.setOnClickListener(this);
        linearLayout.setTag(index);

        View innerLinearLayout = row.findViewById(R.id.supervised_theses_list_item_row_verticalLinearLayout);
        innerLinearLayout.setClickable(true);
        innerLinearLayout.setOnClickListener(this);
        innerLinearLayout.setTag(index);

        return row;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.supervised_theses_list_item_row_horizontalLinearLayout:
            case R.id.supervised_theses_list_item_row_verticalLinearLayout:
            case R.id.supervised_theses_list_item_row_textTitle:
            case R.id.supervised_theses_list_item_row_textStudent:
            case R.id.supervised_theses_list_item_row_type:
                Intent intent = new Intent(context, ActivitySuperviseThesis.class);
                String tag = (String) view.getTag();
                SupervisedThesesListItem item = items.get(Integer.valueOf(tag));
                intent.putExtra("thesisId", item.getThesisId());
                context.startActivity(intent);
                break;
            default:
                throw new IllegalStateException("Unrecognized id passed to onClick handler");
        }
    }
}
