package com.example.hci_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MessagesActivity extends AppCompatActivity {

    private LinearLayout contactsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages);

        contactsContainer = findViewById(R.id.contacts_container);

        loadContacts();

        Button newMessageButton = findViewById(R.id.btnNew_message);
        newMessageButton.setOnClickListener(v -> {
            Intent intent = new Intent(MessagesActivity.this, ContactsActivity.class);
            startActivity(intent);
        });

        ImageButton microphoneButton = findViewById(R.id.button_microphone);
        microphoneButton.setOnClickListener(v -> {
            Intent intent = new Intent(MessagesActivity.this, VoiceRecognitionActivity.class);
            startActivity(intent);
        });
    }

    private void loadContacts() {
        ContactManager contactManager = ContactManager.getInstance(this);
        ArrayList<ContactManager.Contact> contacts = contactManager.getAllContacts();

        for (ContactManager.Contact contact : contacts) {
            addContactToView(contact);
        }
    }

    private void addContactToView(ContactManager.Contact contact) {
        TextView contactTextView = new TextView(this);
        contactTextView.setText(contact.getName());
        contactTextView.setTextSize(20);
        contactTextView.setPadding(10, 10, 10, 10);
        contactTextView.setTextColor(getResources().getColor(android.R.color.black));
        contactTextView.setOnClickListener(v -> {
            Intent intent = new Intent(MessagesActivity.this, Message_DetailsActivity.class);
            intent.putExtra("contact_name", contact.getName());
            startActivity(intent);
        });
        contactsContainer.addView(contactTextView);
    }
}

