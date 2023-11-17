package com.example.assignment1_mobile_348;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.LinkedList;

public class CompletedTasks extends AppCompatActivity {

//    private TabItem tabDue;
//    private TabItem tabComplete;
    private Button btnDeleteCompleteNote;
    private ListView completeListView;
    private TabLayout tabLayout;
    private LinkedList<Task> completedTasksList; // this one will be shown in the list view
    private SharedPreferences completedPref;
    private SharedPreferences.Editor editor;
    private TaskAdapter adapter;
    private final Gson gson = new Gson();
    private final Type taskListType = new TypeToken<LinkedList<Task>>() {}.getType();
    private Task selectedTask = null;
//    private LinkedList<Task> completedTasksDatabase;
//    private LinkedList<Task> tasks1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_tasks);
        setupView();// first one
        setupSharedPreference();// second one
        addTasksToCurrentPreferenceAndList();// third one
        setupListView();// last one

        //Event Handlers
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 0:
                        switchScreen();
//                        getDueListFromPref();
                    case 1:
                        break;
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        completeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedTask = completedTasksList.get(position);
            }
        });

        btnDeleteCompleteNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNote();
            }
        });

    }

    private void setupView(){
//        tabDue = findViewById(R.id.tabDue);
//        tabComplete = findViewById(R.id.tabComplete);
        btnDeleteCompleteNote = findViewById(R.id.btnDeleteCompleteNote);
        completeListView = findViewById(R.id.completeList);
        tabLayout = findViewById(R.id.tabLayout);
        TabLayout.Tab tab = tabLayout.getTabAt(1);
        if(tab != null){
            tab.select();
        }
        completedTasksList = new LinkedList<>();
//        completedTasksDatabase = new LinkedList<Task>();

    }

    private void switchScreen(){
        Intent intent = new Intent(CompletedTasks.this, MainActivity.class);
        startActivity(intent);
    }

    private void setupSharedPreference(){
        completedPref = getSharedPreferences("completedTasksList", Context.MODE_PRIVATE);
        editor = completedPref.edit();
    }
    private void addTasksToCurrentPreferenceAndList() {
        LinkedList<Task> tasks1;
        //bring the finished tasks
        String finishedTasksJson = MainActivity.duePref.getString("finishedTask", "");
        MainActivity.editor.putString("finishedTask", "");
        MainActivity.editor.commit();
        if(finishedTasksJson == ""){
             tasks1 = new LinkedList<>();
        }else{
            tasks1 = gson.fromJson(finishedTasksJson, taskListType);
        }

        //get the finished tasks from the key
        String retrivedTasksJson = completedPref.getString("tasksKey", "");

        //store the tasks inside the list to show the list items
       if(retrivedTasksJson != ""){
           LinkedList<Task> tasks2 = gson.fromJson(retrivedTasksJson, taskListType);
           tasks2.addAll(tasks1);
           completedTasksList = tasks2;
           String storeCompletedTasksJson = gson.toJson(completedTasksList);
           editor.putString("tasksKey", storeCompletedTasksJson);
           editor.commit();
       }else{
           if(tasks1.size() > 0){
               String tasksJsonString = gson.toJson(tasks1);
               editor.putString("tasksKey",tasksJsonString);
               editor.commit();
           }
       }
    }
    private void setupListView() {
        adapter = new TaskAdapter(getApplicationContext(), R.layout.list_item, completedTasksList);
        completeListView.setAdapter(adapter);
    }
    private void deleteNote(){
        if (selectedTask != null) {
            completedTasksList.remove(selectedTask);
            adapter.notifyDataSetChanged();

            String tasksJson = gson.toJson(completedTasksList);
           editor.putString("tasksKey", tasksJson);
            editor.commit();
            selectedTask = null;
        }
    }
}