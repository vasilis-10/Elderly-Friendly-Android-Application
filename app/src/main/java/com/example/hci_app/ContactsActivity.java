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

public class ContactsActivity extends AppCompatActivity {

    private LinearLayout contactListLayout;
    private Button btnAddContact;
    private boolean isSelectingContact = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts);

        contactListLayout = findViewById(R.id.contactListLayout);
        btnAddContact = findViewById(R.id.btnAddContact);

        // Determine if the activity was started for result
        isSelectingContact = getIntent().getBooleanExtra("select_contact", false);

        // Add static contacts to ContactManager for this example
        ContactManager contactManager = ContactManager.getInstance(this);
        contactManager.addContact("1", "Μαρία", "1234567890");
        contactManager.addContact("2", "Γιώργος", "2345678901");
        contactManager.addContact("3", "Νίκος", "3456789012");
        contactManager.addContact("4", "Άννα", "4567890123");
        contactManager.addContact("5", "Εγγονός", "5678901234");
        contactManager.addContact("6", "Εγγονή", "6789012345");

        loadContacts();

        btnAddContact.setOnClickListener(v -> {
            startActivity(new Intent(ContactsActivity.this, NewContactActivity.class));
        });

        ImageButton microphoneButton = findViewById(R.id.button_microphone);
        microphoneButton.setOnClickListener(v -> {
            Intent intent = new Intent(ContactsActivity.this, VoiceRecognitionActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshContactList();
    }

    private void loadContacts() {
        ContactManager contactManager = ContactManager.getInstance(this);
        ArrayList<ContactManager.Contact> contacts = contactManager.getAllContacts();

        for (ContactManager.Contact contact : contacts) {
            addContact(contact.getId(), contact.getName(), contact.getPhone());
        }
    }

    private void refreshContactList() {
        contactListLayout.removeAllViews();
        loadContacts();
    }

    private void addContact(final String id, final String name, final String phone) {
        TextView contactTextView = new TextView(this);
        contactTextView.setText(name);
        contactTextView.setTextSize(20);
        contactTextView.setPadding(10, 10, 10, 10);
        contactTextView.setTextColor(getResources().getColor(android.R.color.black));
        contactTextView.setOnClickListener(v -> {
            if (isSelectingContact) {
                // Return the selected contact name to the calling activity
                Intent intent = new Intent();
                intent.putExtra("contact_name", name);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                // Navigate to the contact details
                Intent intent = new Intent(ContactsActivity.this, ContactDetailsActivity.class);
                intent.putExtra("CONTACT_ID", id);
                startActivity(intent);
            }
        });
        contactListLayout.addView(contactTextView);
    }
}
