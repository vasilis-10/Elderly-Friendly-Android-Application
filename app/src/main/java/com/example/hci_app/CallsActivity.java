package com.example.hci_app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class CallsActivity extends AppCompatActivity {

    private Button btnFavorite1, btnFavorite2, btnFavorite3, btnFavorite4;
    private Button btnContacts;
    private TextView tvDialerInput;
    private GridLayout gridLayout;
    private ImageButton callButton, backspaceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call);

        btnFavorite1 = findViewById(R.id.btnFavorite1);
        btnFavorite2 = findViewById(R.id.btnFavorite2);
        btnFavorite3 = findViewById(R.id.btnFavorite3);
        btnFavorite4 = findViewById(R.id.btnFavorite4);
        btnContacts = findViewById(R.id.btnContacts);
        tvDialerInput = findViewById(R.id.tvDialerInput);
        btnContacts = findViewById(R.id.btnContacts);
        gridLayout = findViewById(R.id.gridLayout);
        callButton = findViewById(R.id.callButton);
        backspaceButton = findViewById(R.id.backspaceButton);

        loadFavorites();

        btnContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CallsActivity.this, ContactsActivity.class));
            }
        });
        setupDialer();

        ImageButton microphoneButton = findViewById(R.id.button_microphone);
        microphoneButton.setOnClickListener(v -> {
            Intent intent = new Intent(CallsActivity.this, VoiceRecognitionActivity.class);
            startActivity(intent);
        });
    }

    private void loadFavorites() {
        ContactManager contactManager = ContactManager.getInstance(this);
        ArrayList<ContactManager.Contact> favorites = contactManager.getFavorites();

        Button[] favoriteButtons = {btnFavorite1, btnFavorite2, btnFavorite3, btnFavorite4};

        for (int i = 0; i < favoriteButtons.length; i++) {
            if (i < favorites.size()) {
                final ContactManager.Contact contact = favorites.get(i);
                favoriteButtons[i].setText(contact.getName());
                favoriteButtons[i].setVisibility(View.VISIBLE);
                favoriteButtons[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:" + contact.getPhone()));
                        startActivity(callIntent);
                    }
                });
            } else {
                favoriteButtons[i].setVisibility(View.GONE);
            }
        }
    }
    private void setupDialer() {
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View view = gridLayout.getChildAt(i);
            if (view instanceof Button) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String currentText = tvDialerInput.getText().toString();
                        Button button = (Button) v;
                        String buttonText = button.getText().toString();
                        tvDialerInput.setText(currentText + buttonText);
                    }
                });
            }
        }

        backspaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = tvDialerInput.getText().toString();
                if (!currentText.isEmpty()) {
                    tvDialerInput.setText(currentText.substring(0, currentText.length() - 1));
                }
            }
        });

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = tvDialerInput.getText().toString();
                if (!phoneNumber.isEmpty()) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + phoneNumber));
                    startActivity(callIntent);
                }
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
    }
}