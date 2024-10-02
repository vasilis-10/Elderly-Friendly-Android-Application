package com.example.hci_app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.Locale;

public class VoiceRecognitionActivity extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final int REQUEST_CALL_PHONE_PERMISSION = 201;
    private SpeechRecognizer speechRecognizer;
    private TextView textViewResults;
    private ContactManager contactManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_recognition);

        Button speechButton = findViewById(R.id.button);
        textViewResults = findViewById(R.id.textView_results);
        contactManager = ContactManager.getInstance(this);

        // Request microphone and call phone permissions if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE_PERMISSION);
        }

        // Initialize the SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {}

            @Override
            public void onBeginningOfSpeech() {}

            @Override
            public void onRmsChanged(float rmsdB) {}

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {}

            @Override
            public void onError(int error) {
                String errorMessage = getErrorText(error);
                Toast.makeText(VoiceRecognitionActivity.this, "Recognition Error: " + errorMessage, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String recognizedText = matches.get(0);
                    textViewResults.setText("Αναγνωρισμένο Κείμενο: " + recognizedText);
                    handleVoiceCommand(recognizedText.toLowerCase(Locale.ROOT));
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {}

            @Override
            public void onEvent(int eventType, Bundle params) {}
        });

        speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startListening();
            }
        });
    }

    private void startListening() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "el-GR"); // Greek language
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Πείτε την εντολή σας"); // "Say your command"
        speechRecognizer.startListening(intent);
    }

    private void handleVoiceCommand(String command) {
        if (command.equals("κάλεσε την αστυνομία") || command.equals("call police")) {
            makePhoneCall("100  "); // Police
        } else if (command.equals("κάλεσε την πυροσβεστική") || command.equals("call fire station")) {
            makePhoneCall("199"); // Fire Station
        } else if (command.equals("κάλεσε το εκαβ") || command.equals("call ambulance")) {
            makePhoneCall("166"); // Ambulance
        } else if (command.startsWith("κάλεσε τον ") || command.startsWith("κάλεσε την ") || command.startsWith("call ")) {
            String contactName = command.replace("κάλεσε τον ", "").replace("κάλεσε την ", "").replace("call ", "").trim();
            ContactManager.Contact contact = contactManager.getContactByName(contactName);
            if (contact != null) {
                makePhoneCall(contact.getPhone());
            } else {
                Toast.makeText(this, "Η επαφή δεν βρέθηκε / Contact not found: " + contactName, Toast.LENGTH_SHORT).show();
            }
        } else {
            switch (command) {
                case "άνοιξε τις κλήσεις":
                case "open calls":
                    startActivity(new Intent(this, CallsActivity.class));
                    break;
                case "άνοιξε τα μηνύματα":
                case "open messages":
                    startActivity(new Intent(this, MessagesActivity.class));
                    break;
                case "άνοιξε τις υπενθυμίσεις":
                case "open reminders":
                    startActivity(new Intent(this, RemindersActivity.class));
                    break;
                case "άνοιξε τις επαφές":
                case "open contacts":
                    startActivity(new Intent(this, ContactsActivity.class));
                    break;
                case "άνοιξε το sos":
                case "open sos":
                    startActivity(new Intent(this, SOSActivity.class));
                    break;
                default:
                    Toast.makeText(this, "Άγνωστη εντολή / Unknown command: " + command, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private void makePhoneCall(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(intent);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE_PERMISSION);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ((requestCode == REQUEST_RECORD_AUDIO_PERMISSION || requestCode == REQUEST_CALL_PHONE_PERMISSION) &&
                grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission is required", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private String getErrorText(int errorCode) {
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                return "Audio recording error";
            case SpeechRecognizer.ERROR_CLIENT:
                return "Client side error";
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                return "Insufficient permissions";
            case SpeechRecognizer.ERROR_NETWORK:
                return "Network error";
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                return "Network timeout";
            case SpeechRecognizer.ERROR_NO_MATCH:
                return "No match found";
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                return "Recognition service busy";
            case SpeechRecognizer.ERROR_SERVER:
                return "Error from server";
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                return "No speech input";
            default:
                return "Unknown error";
        }
    }
}
