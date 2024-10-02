package com.example.hci_app;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class RemindersActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD_REMINDER = 1;
    private static final String PREFS_NAME = "RemindersPrefs";
    private static final String REMINDERS_KEY = "reminders";
    private static final String DATE_KEY = "selected_date";

    private SharedPreferences sharedPreferences;
    private LinearLayout remindersLayout;
    private TextView dateTextView;
    private Calendar calendar;
    private View selectedReminderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        remindersLayout = findViewById(R.id.linearLayout_reminders);
        dateTextView = findViewById(R.id.textView_date);
        calendar = Calendar.getInstance();

        loadDate();
        loadReminders();

        ImageButton microphoneButton = findViewById(R.id.button_microphone);
        microphoneButton.setOnClickListener(v -> {
            Intent intent = new Intent(RemindersActivity.this, VoiceRecognitionActivity.class);
            startActivity(intent);
        });

        dateTextView.setOnClickListener(v -> showDatePicker());

        ImageButton prevDayButton = findViewById(R.id.button_prev_day);
        prevDayButton.setOnClickListener(v -> {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            updateDate();
            loadReminders();
        });

        ImageButton nextDayButton = findViewById(R.id.button_next_day);
        nextDayButton.setOnClickListener(v -> {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            updateDate();
            loadReminders();
        });

        Button addReminderButton = findViewById(R.id.button_add_reminder);
        addReminderButton.setOnClickListener(v -> {
            Intent intent = new Intent(RemindersActivity.this, AddReminderActivity.class);
            intent.putExtra("date", getFormattedDate());
            startActivityForResult(intent, REQUEST_CODE_ADD_REMINDER);
        });

        Button deleteReminderButton = findViewById(R.id.button_delete);
        deleteReminderButton.setOnClickListener(v -> deleteSelectedReminder());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    updateDate();
                    loadReminders();
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void loadDate() {
        String savedDate = sharedPreferences.getString(DATE_KEY, getFormattedDate());
        dateTextView.setText(savedDate);
        try {
            calendar.setTime(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(savedDate));
        } catch (Exception e) {
            calendar.setTime(Calendar.getInstance().getTime());
        }
    }

    private void saveDate(String dateString) {
        sharedPreferences.edit().putString(DATE_KEY, dateString).apply();
    }

    private void updateDate() {
        String formattedDate = getFormattedDate();
        dateTextView.setText(formattedDate);
        saveDate(formattedDate);
    }

    private String getFormattedDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_REMINDER && resultCode == RESULT_OK) {
            String title = data.getStringExtra("title");
            String date = data.getStringExtra("date");
            String time = data.getStringExtra("time");
            saveReminder(title, date, time);
            if (date.equals(getFormattedDate())) {
                loadReminders();
            }
        }
    }

    private void loadReminders() {
        remindersLayout.removeAllViews();
        Set<String> reminders = sharedPreferences.getStringSet(REMINDERS_KEY, new HashSet<>());
        String selectedDate = getFormattedDate();
        ArrayList<String> remindersForDate = new ArrayList<>();
        for (String reminder : reminders) {
            String[] parts = reminder.split("\\|");
            if (parts.length == 3 && parts[1].equals(selectedDate)) {
                remindersForDate.add(reminder);
            }
        }

        Collections.sort(remindersForDate, new Comparator<String>() {
            @Override
            public int compare(String r1, String r2) {
                String[] parts1 = r1.split("\\|");
                String[] parts2 = r2.split("\\|");
                return parts1[2].compareTo(parts2[2]);
            }
        });

        for (String reminder : remindersForDate) {
            String[] parts = reminder.split("\\|");
            addReminderToView(parts[0], parts[2]);
        }
    }

    private void saveReminder(String title, String date, String time) {
        Set<String> reminders = new HashSet<>(sharedPreferences.getStringSet(REMINDERS_KEY, new HashSet<>()));
        reminders.add(title + "|" + date + "|" + time);
        sharedPreferences.edit().putStringSet(REMINDERS_KEY, reminders).apply();
    }

    private void deleteSelectedReminder() {
        if (selectedReminderView != null) {
            String reminderText = ((TextView) selectedReminderView).getText().toString();
            String[] parts = reminderText.split(" ");
            String title = parts[0];
            String time = parts[1];

            Set<String> reminders = new HashSet<>(sharedPreferences.getStringSet(REMINDERS_KEY, new HashSet<>()));
            String selectedDate = getFormattedDate();
            reminders.removeIf(reminder -> reminder.equals(title + "|" + selectedDate + "|" + time));
            sharedPreferences.edit().putStringSet(REMINDERS_KEY, reminders).apply();

            remindersLayout.removeView(selectedReminderView);
            selectedReminderView = null;
        }
    }

    private void addReminderToView(String title, String time) {
        TextView reminderTextView = new TextView(this);
        reminderTextView.setText(title + " " + time);
        reminderTextView.setTextSize(20);
        reminderTextView.setPadding(8, 8, 8, 8);
        reminderTextView.setOnClickListener(v -> selectReminder(reminderTextView));
        remindersLayout.addView(reminderTextView);
    }

    private void selectReminder(TextView reminderTextView) {
        if (selectedReminderView != null) {
            selectedReminderView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        }
        selectedReminderView = reminderTextView;
        selectedReminderView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_light));
    }
}
