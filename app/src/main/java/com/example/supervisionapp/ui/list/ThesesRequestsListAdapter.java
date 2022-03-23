package com.example.supervisionapp.ui.list;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.supervisionapp.ActivityViewThesisRequest;
import com.example.supervisionapp.R;
import com.example.supervisionapp.data.list.model.ThesesRequestsListItem;

import java.util.ArrayList;
import java.util.List;

public class ThesesRequestsListAdapter extends BaseAdapter implements View.OnClickListener {
    private Context context;
    private List<ThesesRequestsListItem> items;

    public ThesesRequestsListAdapter(Context context, List<ThesesRequestsListItem> items) {
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
        View row = inflater.inflate(R.layout.theses_requests_list_item_row, viewGroup, false);
        TextView title = row.findViewById(R.id.theses_requests_list_item_row_textTitle);
        TextView student = row.findViewById(R.id.theses_requests_list_item_row_textStudent);
        TextView supervisoryType = row.findViewById(R.id.theses_requests_list_item_row_type);
        ThesesRequestsListItem item = items.get(i);

        title.setText(item.getTitle());
        title.setClickable(true);
        title.setOnClickListener(this);
        title.setTag(index);

        student.setText(item.getStudent());
        student.setClickable(true);
        student.setOnClickListener(this);
        student.setTag(index);

        supervisoryType.setText(item.getSupervisoryType().toString());
        supervisoryType.setClickable(true);
        supervisoryType.setOnClickListener(this);
        supervisoryType.setTag(index);

        View linearLayout = row.findViewById(R.id.theses_requests_list_item_row_horizontalLinearLayout);
        linearLayout.setClickable(true);
        linearLayout.setOnClickListener(this);
        linearLayout.setTag(index);

        View innerLinearLayout = row.findViewById(R.id.theses_requests_list_item_row_verticalLinearLayout);
        innerLinearLayout.setClickable(true);
        innerLinearLayout.setOnClickListener(this);
        innerLinearLayout.setTag(index);

        return row;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.theses_requests_list_item_row_horizontalLinearLayout:
            case R.id.theses_requests_list_item_row_verticalLinearLayout:
            case R.id.theses_requests_list_item_row_textTitle:
            case R.id.theses_requests_list_item_row_textStudent:
            case R.id.theses_requests_list_item_row_type:
                Intent intent = new Intent(context, ActivityViewThesisRequest.class);
                String tag = (String) view.getTag();
                ThesesRequestsListItem item = items.get(Integer.valueOf(tag));
                intent.putExtra("thesisId", item.getThesisId());
                context.startActivity(intent);
                break;
            default:
                throw new IllegalStateException("Unrecognized id passed to onClick handler");
        }
    }
}
