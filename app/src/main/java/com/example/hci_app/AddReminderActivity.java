package com.example.hci_app;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class AddReminderActivity extends AppCompatActivity {

    private EditText etTitle, etDate, etTime;
    private Button btnAdd, btnCancel;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_reminder);

        etTitle = findViewById(R.id.etTitle);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        btnAdd = findViewById(R.id.btnAdd);
        btnCancel = findViewById(R.id.btnCancel);

        calendar = Calendar.getInstance();

        String selectedDate = getIntent().getStringExtra("date");
        etDate.setText(selectedDate);

        etTitle.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showKeyboard(etTitle);
            }
        });

        etDate.setOnClickListener(v -> showDatePicker());
        etTime.setOnClickListener(v -> showTimePicker());

        btnAdd.setOnClickListener(v -> addReminder());
        btnCancel.setOnClickListener(v -> clearFields());
    }

    private void showKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    etDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> etTime.setText(hourOfDay + ":" + String.format("%02d", minute)),
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    private void addReminder() {
        String title = etTitle.getText().toString();
        String date = etDate.getText().toString();
        String time = etTime.getText().toString();

        if (!title.isEmpty() && !date.isEmpty() && !time.isEmpty()) {
            Intent intent = new Intent();
            intent.putExtra("title", title);
            intent.putExtra("date", date);
            intent.putExtra("time", time);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void clearFields() {
        etTitle.setText("");
        etDate.setText("");
        etTime.setText("");
    }
}
