package com.example.hci_app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class NewContactActivity extends AppCompatActivity {

    private EditText editTextFirstName, editTextLastName, editTextPhoneNumber;
    private Button buttonAdd, buttonReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_contact);

        editTextFirstName = findViewById(R.id.editText_first_name);
        editTextLastName = findViewById(R.id.editText_last_name);
        editTextPhoneNumber = findViewById(R.id.editText_phone_number);
        buttonAdd = findViewById(R.id.button_add);
        buttonReturn = findViewById(R.id.button_return);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewContact();
            }
        });

        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageButton microphoneButton = findViewById(R.id.button_microphone);
        microphoneButton.setOnClickListener(v -> {
            Intent intent = new Intent(NewContactActivity.this, VoiceRecognitionActivity.class);
            startActivity(intent);
        });
    }


    private void addNewContact() {
        String firstName = editTextFirstName.getText().toString();
        String lastName = editTextLastName.getText().toString();
        String phoneNumber = editTextPhoneNumber.getText().toString();
        String fullName = firstName + " " + lastName;

        ContactManager contactManager = ContactManager.getInstance(this);
        int contactId = contactManager.getAllContacts().size() + 1;
        contactManager.addContact(String.valueOf(contactId), fullName, phoneNumber);

        Intent intent = new Intent(NewContactActivity.this, ContactsActivity.class);
        startActivity(intent);
        finish();
    }
}
