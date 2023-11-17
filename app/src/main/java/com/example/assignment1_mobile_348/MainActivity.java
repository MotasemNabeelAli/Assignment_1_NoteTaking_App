package com.example.assignment1_mobile_348;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.content.SharedPreferences;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import java.lang.reflect.Type;
import java.util.LinkedList;
import android.content.Context;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends AppCompatActivity {

    //UI Elements
    private ListView dueList;
    private EditText textNoteDescription;
    private Button btnAddNote;
    private Button btnDeleteNote;
    private Button btnFinish;

    private TabLayout tabLayout;

    //Adapter
    static TaskAdapter adapter;

    //Shared and GSON
    static SharedPreferences duePref;
    static SharedPreferences.Editor editor;
    private final Gson gson = new Gson();
    private LinkedList<Task> dueTasksList;
    private LinkedList<Task> completedTasksList;

    // to select from list
    private Task selectedTask = null;
    private final Type taskListType = new TypeToken<LinkedList<Task>>() {}.getType();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupView();
        setupSharedPreference();
        loadTasksFromSharedPref();
        setupListView();

        // Event Handlers
        btnAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskDescription = textNoteDescription.getText().toString();
                if (!taskDescription.isEmpty()) {
                    Task task = new Task(taskDescription, false);
                    dueTasksList.add(task);

                    saveTasksToSharedPref();
                    adapter.notifyDataSetChanged();

                    textNoteDescription.setText(null);
                }
            }
        });

        dueList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedTask = dueTasksList.get(position);
            }
        });


        btnDeleteNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNote();
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        switchScreen();
                        getDueListFromPref();
                        break;
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Handle tab unselection if needed
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Handle tab reselection if needed
            }
        });


        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedTask != null) {
                    // Change the status of the task to complete
                    selectedTask.setStatus(true);

                    // Add to finished tasks & update the finished database
                    completedTasksList.add(selectedTask);
                    String tasksFinishedJson = gson.toJson(completedTasksList);
                    editor.putString("finishedTask", tasksFinishedJson);
                    editor.commit();

                    // Remove the finished task from the due tasks
                    dueTasksList.remove(selectedTask);
                    adapter.notifyDataSetChanged();

                    // Update the due database
                    String tasksJson = gson.toJson(dueTasksList);
                    editor.putString("dueTasksList", tasksJson);
                    editor.commit();


                }
            }
        });

    }
    //locating and setting the ui elements
    private void setupView(){
//        tabDue = findViewById(R.id.tabDue);
//        tabComplete = findViewById(R.id.tabComplete);
        dueList = findViewById(R.id.dueList);
        textNoteDescription = findViewById(R.id.textNoteDescription);
        btnAddNote = findViewById(R.id.btnAddNote);
        btnDeleteNote = findViewById(R.id.btnDeleteDueNote);
        tabLayout = findViewById(R.id.tabLayout);
        btnFinish = findViewById(R.id.btnFinish);
        completedTasksList = new LinkedList<>();
    }

    //setting up the shared Preference database
    private void setupSharedPreference(){
        duePref = getSharedPreferences("dueTasksList", Context.MODE_PRIVATE);
        editor = duePref.edit();
    }

    //switching between the activities
    private void switchScreen(){
        Intent intent = new Intent(MainActivity.this, CompletedTasks.class);
        startActivity(intent);
    }

    private void deleteNote(){
        if (selectedTask != null) {
            dueTasksList.remove(selectedTask);
            adapter.notifyDataSetChanged();

            String tasksJson = gson.toJson(dueTasksList);
            editor.putString("dueTasksList", tasksJson);
            editor.commit();

            selectedTask = null;
        }
    }

    private void getDueListFromPref(){
        String dueTasksJsonFormat = duePref.getString("dueTasksList", "");
        dueTasksList = gson.fromJson(dueTasksJsonFormat,taskListType);
        adapter.notifyDataSetChanged();
    }

    // Method to set up the ListView
    private void setupListView() {
        adapter = new TaskAdapter(getApplicationContext(), R.layout.list_item, dueTasksList);
        dueList.setAdapter(adapter);
    }

    // Method to load tasks from shared preferences
    private void loadTasksFromSharedPref() {
        String tasksJSON = duePref.getString("dueTasksList", "");
        if (!tasksJSON.isEmpty()) {
            dueTasksList = gson.fromJson(tasksJSON, taskListType);
        } else {
            dueTasksList = new LinkedList<>();
        }
    }

    // Method to save tasks to shared preferences
    private void saveTasksToSharedPref() {
        String tasksJson = gson.toJson(dueTasksList);
        editor.putString("dueTasksList", tasksJson);
        editor.apply();
    }
}