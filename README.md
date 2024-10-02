# Elderly-Friendly Android Application

## Overview

This project is an **Android application** designed with an intuitive and simplified user interface, specifically tailored for elderly users. The app provides core functionalities such as **making calls**, **sending messages**, **setting reminders**, and **SOS emergency assistance**, all designed to be user-friendly. Additionally, the app integrates **voice recognition** to allow hands-free interaction, making it easier for elderly users to navigate the app and perform tasks.

## Features

- **Contacts Management**: Add, manage, and call contacts from the app’s built-in contact manager. Users can mark favorite contacts for quick access and make calls directly from the app.
- **Messaging**: Simple and accessible messaging system where users can compose, send, and view messages from their contacts.
- **Reminders**: Users can set, manage, and delete reminders for important tasks like taking medication or attending appointments.
- **SOS Emergency Functionality**: A dedicated SOS section allows users to call emergency services like the police, fire department, or ambulance with one tap.
- **Voice Recognition**: Users can control the app using voice commands, with support for both Greek and English, to perform tasks such as making calls, sending messages, and accessing reminders.

## Key Classes and Files

1. **`ContactManager.java`**: Manages contacts and favorites. Uses **SharedPreferences** for persistent storage of contact data.
2. **`ContactsActivity.java`**: Displays a list of contacts and allows users to make calls. Integrates voice commands to select contacts via speech.
3. **`MessagesActivity.java`**: Displays a list of contacts for messaging. Users can compose new messages or view previous conversations.
4. **`NewMessageActivity.java`**: Allows users to send new messages to contacts, with an intuitive interface for quick navigation.
5. **`RemindersActivity.java`**: Displays and manages reminders. Users can add, delete, and modify reminders with ease.
6. **`SOSActivity.java`**: Provides instant access to emergency services. Supports voice commands for hands-free emergency calls.
7. **`VoiceRecognitionActivity.java`**: Handles all voice recognition features in the app, allowing users to control the app through voice commands.

