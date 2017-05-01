package fi.oulu.mobisocial.kummi_application;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class TaskAdapter extends ArrayAdapter<Task> {
    private static final String TAG = "TASKERI ADAPTER";
    public TaskAdapter(Context context, ArrayList<Task> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Task task = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }
        // Lookup view for data population
        TextView tvInfo = (TextView) convertView.findViewById(R.id.tvInfo);
        TextView tvType = (TextView) convertView.findViewById(R.id.tvTime);
        // Populate the data into the template view using the data object
        tvInfo.setText(task.getInfo());
        tvType.setText(task.getTimeOrLocation());
        // Return the completed view to render on screen
        return convertView;
    }
}