package com.example.supervisionapp.ui.list;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.supervisionapp.R;
import com.example.supervisionapp.data.list.model.AdvertisedThesesListItem;
import com.example.supervisionapp.ActivitySubmitThesis;

import java.util.ArrayList;
import java.util.List;

public class AdvertisedThesesListAdapter extends BaseAdapter implements View.OnClickListener {
    private Context context;
    private List<AdvertisedThesesListItem> items;

    public AdvertisedThesesListAdapter(Context context, List<AdvertisedThesesListItem> items) {
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
        View row = inflater.inflate(R.layout.advertised_theses_list_item_row, viewGroup, false);
        TextView title = (TextView) row.findViewById(R.id.advertised_theses_list_item_row_textTitle);
        TextView description = (TextView) row.findViewById(R.id.advertised_theses_list_item_row_textDescription);
        AdvertisedThesesListItem item = items.get(i);
        title.setText(item.getTitle());
        description.setText(item.getDescription());
        View linearLayout = row.findViewById(R.id.advertised_theses_list_item_row_linearLayout);
        linearLayout.setClickable(true);
        linearLayout.setOnClickListener(this);
        title.setClickable(true);
        title.setOnClickListener(this);
        description.setClickable(true);
        description.setOnClickListener(this);
        return row;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.advertised_theses_list_item_row_linearLayout:
            case R.id.advertised_theses_list_item_row_textTitle:
            case R.id.advertised_theses_list_item_row_textDescription:
                Intent intent = new Intent(context, ActivitySubmitThesis.class);
                context.startActivity(intent);
                // TODO: send bundle so activity knows what to load or show
                break;
            default:
                throw new IllegalStateException("Unrecognized id passed to onClick handler");
        }
    }
}
