package com.example.supervisionapp.ui.list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.supervisionapp.ActivityViewThesisRequest;
import com.example.supervisionapp.R;
import com.example.supervisionapp.data.list.model.ThesesRequestsListItem;
import com.example.supervisionapp.ui.main.FragmentThesesRequests;

import java.util.ArrayList;
import java.util.List;

public class ThesesRequestsListAdapter extends BaseAdapter implements View.OnClickListener {
    private Context context;
    private List<ThesesRequestsListItem> items;
    private int activityStartRequestCode;
    private Fragment fragmentContext;

    public ThesesRequestsListAdapter(Context context,
                                     List<ThesesRequestsListItem> items,
                                     int activityStartRequestCode,
                                     Fragment fragmentContext) {
        this.context = context;
        this.items = new ArrayList<>(items);
        this.activityStartRequestCode = activityStartRequestCode;
        this.fragmentContext = fragmentContext;
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
        TextView name = row.findViewById(R.id.theses_requests_list_item_row_textName);
        TextView type = row.findViewById(R.id.theses_requests_list_item_row_type);
        ThesesRequestsListItem item = items.get(i);

        title.setText(item.getTitle());
        title.setClickable(true);
        title.setOnClickListener(this);
        title.setTag(index);

        name.setText(item.getName());
        name.setClickable(true);
        name.setOnClickListener(this);
        name.setTag(index);

        type.setText(item.getRequestType().name());
        type.setClickable(true);
        type.setOnClickListener(this);
        type.setTag(index);

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
            case R.id.theses_requests_list_item_row_textName:
            case R.id.theses_requests_list_item_row_type:
                Intent intent = new Intent(context, ActivityViewThesisRequest.class);
                String tag = (String) view.getTag();
                ThesesRequestsListItem item = items.get(Integer.valueOf(tag));
                intent.putExtra("thesisId", item.getThesisId());
                intent.putExtra("userId", item.getUserId());
                intent.putExtra("requestType", item.getRequestType());
                ((Fragment) fragmentContext).startActivityForResult(intent, activityStartRequestCode);
                break;
            default:
                throw new IllegalStateException("Unrecognized id passed to onClick handler");
        }
    }
}
