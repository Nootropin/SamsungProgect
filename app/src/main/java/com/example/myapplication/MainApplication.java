package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;
import com.example.myapplication.R;
import com.google.gson.Gson;

public class MainApplication extends AppCompatActivity {
    private static final String SHARED_PREFS_NAME = "myApp";
    private static final String TASK_KEY = "tasks";
    private static final String HABIT_KEY = "habits";
    private static final String COUNTER_KEY = "counters";
    private ListView mListView,hListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Init part
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        LinearLayout tasksLayout = findViewById(R.id.taskLayout);
        LinearLayout habitsLayout = findViewById(R.id.habitsLayout);
        tasksLayout.setVisibility(View.VISIBLE);
        habitsLayout.setVisibility(View.GONE);

        mListView = findViewById(R.id.listView_tasks);
        hListView = findViewById(R.id.listView_habits);
        updateListView(getApplicationContext(),mListView,getTasks(getApplication()));
        updateListView(getApplicationContext(),hListView,getHabitList(getApplication()));
        Button addTaskButton = findViewById(R.id.button_add_task);
        Button addHabitButton = findViewById(R.id.button_add_habit);
        Button swapMenuTasks = findViewById(R.id.button_change_menu_tasks);
        Button swapMenuHabits = findViewById(R.id.button_change_menu_habits);

        swapMenuHabits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tasksLayout.setVisibility(View.VISIBLE);
                habitsLayout.setVisibility(View.GONE);
            }
        });
        swapMenuTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tasksLayout.setVisibility(View.GONE);
                habitsLayout.setVisibility(View.VISIBLE);
            }
        });
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog();
            }
        });
        addHabitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialogForHabit();
            }
        });
        mListView.setOnItemClickListener((parent, view, position, id) -> {
            listOnClick(view,position);
        });
        hListView.setOnItemClickListener((parent, view, position, id) -> {
            listOnClickHabit(view,position);
        });
    }

    private void showInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Input Dialog");
        builder.setMessage("Please enter your answer:");

        View dialogView = getLayoutInflater().inflate(R.layout.input_dialog, null);
        EditText editTextAnswer = dialogView.findViewById(R.id.editTextAnswer);
        builder.setView(dialogView);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String answer = editTextAnswer.getText().toString();
                List<String> tasks = getTasks(getApplicationContext());
                tasks.add(answer);
                saveTasks(getApplicationContext(), tasks);
                updateListView(getApplicationContext(), findViewById(R.id.listView_tasks), tasks);
            }
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void showInputDialogForHabit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Input Dialog");
        builder.setMessage("Please enter your answer:");

        View dialogView = getLayoutInflater().inflate(R.layout.input_dialog, null);
        EditText editTextAnswer = dialogView.findViewById(R.id.editTextAnswer);
        builder.setView(dialogView);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String answer = editTextAnswer.getText().toString().trim();
                if (!answer.isEmpty()) {
                    List<String> habits = getHabits(getApplicationContext());
                    List<String> counters = new ArrayList<>(Collections.nCopies(habits.size() + 1, "0"));
                    habits.add(answer);
                    saveCounter(getApplicationContext(), counters);
                    saveHabits(getApplicationContext(), habits);
                    updateListView(getApplicationContext(), hListView, getHabitList(getApplication()));
                }
            }
        });

        builder.setNegativeButton("Close", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void listOnClickHabit(View view, int order)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Choose an Option");
        dialogBuilder.setMessage("What would you like to do?");
        dialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Action to perform when "Delete" is clicked
                List<String> s = getHabits(getApplicationContext());
                List<String> c = getCounters(getApplicationContext());
                c.remove(order);
                s.remove(order);
                saveHabits(getApplicationContext(),s);
                saveCounter(getApplicationContext(),c);
                hListView = findViewById(R.id.listView_habits);
                updateListView(getApplicationContext(),hListView,getHabitList(getApplicationContext()));
            }
        });
        dialogBuilder.setNegativeButton("Leave", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Action to perform when "Leave" is clicked
                dialog.dismiss();
            }
        });
        dialogBuilder.setNeutralButton("Check in", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Action to perform when "Leave" is clicked
                List<String> s = getCounters(getApplicationContext());
                int updated = Integer.parseInt(s.get(order)) + 1;
                s.set(order,String.valueOf(updated));
                saveCounter(getApplicationContext(),s);
                hListView = findViewById(R.id.listView_habits);
                updateListView(getApplicationContext(),hListView,getHabitList(getApplicationContext()));
            }
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }
    public void listOnClick(View view, int order)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Choose an Option");
        dialogBuilder.setMessage("What would you like to do?");
        dialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Action to perform when "Delete" is clicked
                List<String> s = getTasks(getApplicationContext());
                s.remove(order);
                saveTasks(getApplicationContext(),s);
                mListView = findViewById(R.id.listView_tasks);
                updateListView(getApplicationContext(),mListView,s);
            }
        });
        dialogBuilder.setNegativeButton("Leave", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Action to perform when "Leave" is clicked
                dialog.dismiss();
            }
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }
    public static void saveTasks(Context context, List<String> list) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Convert List<String> to comma-separated string
        String commaSeparatedString = String.join("ƒ", list);
        // Save the comma-separated string to SharedPreferences
        editor.putString(TASK_KEY, commaSeparatedString);
        editor.apply();
    }
    public static List<String> getTasks(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        String commaSeparatedString = sharedPreferences.getString(TASK_KEY, null);
        ArrayList<String> list = new ArrayList<>(Arrays.asList(commaSeparatedString.split("ƒ")));
        return list;
    }
    public static void saveHabits(Context context, List<String> list) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Convert List<String> to comma-separated string
        String commaSeparatedString = String.join("ƒ", list);
        // Save the comma-separated string to SharedPreferences
        editor.putString(HABIT_KEY, commaSeparatedString);
        editor.apply();
    }
    public static List<String> getHabits(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        String commaSeparatedString = sharedPreferences.getString(HABIT_KEY, null);
        ArrayList<String> list = new ArrayList<>(Arrays.asList(commaSeparatedString.split("ƒ")));
        return list;
    }
    public static void saveCounter(Context context, List<String> list) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Convert List<String> to comma-separated string
        String commaSeparatedString = String.join("ƒ", list);
        // Save the comma-separated string to SharedPreferences
        editor.putString(COUNTER_KEY, commaSeparatedString);
        editor.apply();
    }
    public static List<String> getCounters(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        String commaSeparatedString = sharedPreferences.getString(COUNTER_KEY, null);
        ArrayList<String> list = new ArrayList<>(Arrays.asList(commaSeparatedString.split("ƒ")));
        return list;
    }
    public static void updateListView(Context context, ListView listView, List<String> items) {
        // Create a new ArrayAdapter with the context and layout resource for each item
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, items);
        // Set the adapter to the ListView with the specified ID
        listView.setAdapter(adapter);
    }
    public static List<String> getHabitList(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        String habitString = sharedPreferences.getString(HABIT_KEY, null);
        String counterString = sharedPreferences.getString(COUNTER_KEY, null);

        List<String> habits = new ArrayList<>(Arrays.asList(habitString.split("ƒ")));
        List<String> counters = new ArrayList<>(Arrays.asList(counterString.split("ƒ")));

        // Add the counter for the new habit
        if (habits.size() > counters.size()) {
            counters.add(String.valueOf(0));
        }

        List<String> returnArray = new ArrayList<>();
        for (int i = 0; i < habits.size(); i++) {
            returnArray.add(habits.get(i) + "    You've checked in " + counters.get(i) + " times");
        }

        return returnArray;
    }



}