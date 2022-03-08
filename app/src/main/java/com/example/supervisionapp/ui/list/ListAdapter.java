package com.example.supervisionapp.ui.list;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.supervisionapp.R;
import com.example.supervisionapp.data.list.model.ListItem;
import com.example.supervisionapp.ui.main.ActivitySubmitThesis;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends BaseAdapter implements View.OnClickListener {
    private Context context;
    private List<ListItem> items;

    public ListAdapter(Context context, List<ListItem> items) {
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
        View row = inflater.inflate(R.layout.list_item_row, viewGroup, false);
        TextView title = (TextView) row.findViewById(R.id.list_item_row_textTitle);
        TextView description = (TextView) row.findViewById(R.id.list_item_row_textDescription);
        ListItem item = items.get(i);
        title.setText(item.getTitle());
        description.setText(item.getDescription());
        View linearLayout = row.findViewById(R.id.list_item_row_linearLayout);
        linearLayout.setClickable(true);
        linearLayout.setOnClickListener(this);
        return row;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.list_item_row_linearLayout:
                Intent intent = new Intent(context, ActivitySubmitThesis.class);
                context.startActivity(intent);
                break;
            default:
                throw new IllegalStateException("Unrecognized id passed to onClick handler");
        }
    }
}
