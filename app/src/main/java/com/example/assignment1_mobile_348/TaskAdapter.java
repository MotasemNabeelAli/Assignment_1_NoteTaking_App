package com.example.assignment1_mobile_348;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.LinkedList;

public class TaskAdapter extends ArrayAdapter<Task> {
    private final LinkedList<Task> taskList;
    private final Context context;

    public TaskAdapter(Context context, int resource, LinkedList<Task> tasks) {
        super(context, resource, tasks);
        this.taskList = tasks;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        // Get the task at the current position
        Task task = taskList.get(position);

        // Find and update the view in the custom list item layout
        TextView descriptionTextView = convertView.findViewById(R.id.listItemDescription);

        // Set the text for the view
        descriptionTextView.setText(task.getDescription());

        return convertView;
    }
}
