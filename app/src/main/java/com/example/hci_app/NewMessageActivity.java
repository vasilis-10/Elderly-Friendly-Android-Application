package com.example.hci_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class NewMessageActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CONTACTS = 1;

    private Button contactSearchButton;
    private EditText messageEditText;
    private String contactName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_message);

        contactName = getIntent().getStringExtra("contact_name");
        messageEditText = findViewById(R.id.message_body);

        contactSearchButton = findViewById(R.id.contact_search_button);
        contactSearchButton.setOnClickListener(v -> {
            Intent intent = new Intent(NewMessageActivity.this, ContactsActivity.class);
            intent.putExtra("select_contact", true);
            startActivityForResult(intent, REQUEST_CODE_CONTACTS);
        });

        ImageButton microphoneButton = findViewById(R.id.button_microphone);
        microphoneButton.setOnClickListener(v -> {
            Intent intent = new Intent(NewMessageActivity.this, VoiceRecognitionActivity.class);
            startActivity(intent);
        });

        Button sendButton = findViewById(R.id.send_button);
        sendButton.setOnClickListener(v -> {
            String messageText = messageEditText.getText().toString();
            if (!messageText.trim().isEmpty() && contactName != null && !contactName.isEmpty()) {
                Intent intent = new Intent(NewMessageActivity.this, Message_DetailsActivity.class);
                intent.putExtra("message_text", messageText);
                intent.putExtra("contact_name", contactName);
                startActivity(intent);
                finish();
            }
        });

        Button cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(v -> {
            Intent intent = new Intent(NewMessageActivity.this, MessagesActivity.class);
            startActivity(intent);
            finish();
        });

        messageEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.showSoftInput(messageEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        // If contact name is provided, set it in the contactSearchButton
        if (contactName != null && !contactName.isEmpty()) {
            contactSearchButton.setText(contactName);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CONTACTS && resultCode == RESULT_OK) {
            contactName = data.getStringExtra("contact_name");
            contactSearchButton.setText(contactName);
        }
    }
}
