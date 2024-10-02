package com.example.hci_app;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import java.util.HashSet;
import java.util.Set;

public class Message_DetailsActivity extends AppCompatActivity {

    private ConstraintLayout constraintLayoutMessages;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "MessagePrefs";
    private static final String MESSAGES_KEY_PREFIX = "messages_";
    private String contactName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_details);

        constraintLayoutMessages = findViewById(R.id.constraintLayout_messages);
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Get the contact name from the intent
        contactName = getIntent().getStringExtra("contact_name");

        // Set the contact name in the header
        TextView headerTextView = findViewById(R.id.textView_header);
        headerTextView.setText(contactName);

        // Load saved messages for the contact
        loadMessages();

        ImageButton microphoneButton = findViewById(R.id.button_microphone);
        microphoneButton.setOnClickListener(v -> {
            Intent intent = new Intent(Message_DetailsActivity.this, VoiceRecognitionActivity.class);
            startActivity(intent);
        });

        EditText messageEditText = findViewById(R.id.editText_message);
        messageEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                String messageText = messageEditText.getText().toString();
                if (!messageText.trim().isEmpty()) {
                    addNewMessage(messageText);
                    saveMessage(messageText);
                    messageEditText.setText("");
                }
                return true;
            }
            return false;
        });

        Intent intent = getIntent();
        if (intent.hasExtra("message_text")) {
            String messageText = intent.getStringExtra("message_text");
            if (messageText != null) {
                addNewMessage(messageText);
                saveMessage(messageText);
            }
        }
    }

    private void addNewMessage(String messageText) {
        TextView newMessage = new TextView(this);
        newMessage.setId(View.generateViewId());
        newMessage.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        newMessage.setBackgroundResource(R.drawable.message_box_right);
        newMessage.setText(messageText);
        newMessage.setTextColor(getResources().getColor(android.R.color.white));
        newMessage.setTextSize(18);
        newMessage.setPadding(8, 8, 8, 8);

        constraintLayoutMessages.addView(newMessage);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayoutMessages);

        if (constraintLayoutMessages.getChildCount() > 1) {
            View previousMessage = constraintLayoutMessages.getChildAt(constraintLayoutMessages.getChildCount() - 2);
            constraintSet.connect(newMessage.getId(), ConstraintSet.TOP, previousMessage.getId(), ConstraintSet.BOTTOM, 8);
        } else {
            constraintSet.connect(newMessage.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 8);
        }
        constraintSet.connect(newMessage.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 8);

        constraintSet.applyTo(constraintLayoutMessages);
    }

    private void saveMessage(String messageText) {
        Set<String> messages = sharedPreferences.getStringSet(MESSAGES_KEY_PREFIX + contactName, new HashSet<>());
        messages.add(messageText);
        sharedPreferences.edit().putStringSet(MESSAGES_KEY_PREFIX + contactName, messages).apply();
    }

    private void loadMessages() {
        Set<String> messages = sharedPreferences.getStringSet(MESSAGES_KEY_PREFIX + contactName, new HashSet<>());
        for (String message : messages) {
            addNewMessage(message);
        }
    }
}
