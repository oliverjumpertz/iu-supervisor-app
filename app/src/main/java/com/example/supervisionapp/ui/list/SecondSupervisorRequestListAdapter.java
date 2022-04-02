package com.example.supervisionapp.ui.list;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.supervisionapp.R;
import com.example.supervisionapp.data.list.model.SecondSupervisorRequestListItem;
import com.example.supervisionapp.persistence.AppDatabase;
import com.example.supervisionapp.persistence.ThesisRepository;

import java.util.ArrayList;
import java.util.List;

public class SecondSupervisorRequestListAdapter extends BaseAdapter implements View.OnClickListener {
    private Context context;
    private List<SecondSupervisorRequestListItem> items;

    public SecondSupervisorRequestListAdapter(Context context, List<SecondSupervisorRequestListItem> items) {
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
        View row = inflater.inflate(R.layout.second_supervisor_request_list_item_row, viewGroup, false);
        TextView name = row.findViewById(R.id.second_supervisor_request_list_item_row_textName);
        SecondSupervisorRequestListItem item = items.get(i);
        name.setText(item.getName());
        name.setTag(index);
        name.setClickable(true);
        name.setOnClickListener(this);
        View linearLayout = row.findViewById(R.id.second_supervisor_request_list_item_row_linearLayout);
        linearLayout.setClickable(true);
        linearLayout.setOnClickListener(this);
        linearLayout.setTag(index);
        return row;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.second_supervisor_request_list_item_row_linearLayout:
            case R.id.second_supervisor_request_list_item_row_textName:
                AppDatabase appDatabase = AppDatabase.getDatabase(context);
                ThesisRepository thesisRepository = new ThesisRepository(appDatabase);
                String index = (String) view.getTag();
                SecondSupervisorRequestListItem item = items.get(Integer.valueOf(index));
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        thesisRepository
                                .requestSecondSupervisor(item.getThesisId(), item.getUserId())
                                .blockingAwait();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // noop
                    }
                });
                builder.setMessage("Soll " + item.getName() + " um Zweitbetreuung gebeten werden?")
                        .setTitle(R.string.activity_second_supervisor_request_dialog_title);

                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            default:
                throw new IllegalStateException("Unrecognized id passed to onClick handler");
        }
    }
}
