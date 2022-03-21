package com.example.supervisionapp.ui.list;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.supervisionapp.ActivityAdvertiseThesis;
import com.example.supervisionapp.ActivitySuperviseThesis;
import com.example.supervisionapp.R;
import com.example.supervisionapp.data.list.model.MyResearchListItem;

import java.util.ArrayList;
import java.util.List;

public class MyResearchListAdapter extends BaseAdapter implements View.OnClickListener {
    private Context context;
    private List<MyResearchListItem> items;

    public MyResearchListAdapter(Context context, List<MyResearchListItem> items) {
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
        View row = inflater.inflate(R.layout.my_research_list_item_row, viewGroup, false);
        TextView title = row.findViewById(R.id.my_research_list_item_row_textTitle);
        TextView description = row.findViewById(R.id.my_research_list_item_row_textDescription);
        MyResearchListItem item = items.get(i);
        title.setText(item.getTitle());
        description.setText(item.getDescription());
        View linearLayout = row.findViewById(R.id.my_research_list_item_row_linearLayout);
        linearLayout.setTag(index);
        linearLayout.setClickable(true);
        linearLayout.setOnClickListener(this);
        title.setTag(index);
        title.setClickable(true);
        title.setOnClickListener(this);
        description.setTag(index);
        description.setClickable(true);
        description.setOnClickListener(this);
        return row;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.my_research_list_item_row_linearLayout:
            case R.id.my_research_list_item_row_textTitle:
            case R.id.my_research_list_item_row_textDescription:
                Intent intent = new Intent(context, ActivitySuperviseThesis.class);
                String tag = (String) view.getTag();
                MyResearchListItem item = items.get(Integer.valueOf(tag));
                intent.putExtra("thesisId", item.getThesisId());
                context.startActivity(intent);
                break;
            default:
                throw new IllegalStateException("Unrecognized id passed to onClick handler");
        }
    }
}
