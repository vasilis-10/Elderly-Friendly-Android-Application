package com.example.hci_app;
import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ContactManager {
    private static ContactManager instance;
    private HashMap<String, Contact> contacts;
    private Set<String> favorites;
    private static final String PREFS_NAME = "contacts";
    private SharedPreferences sharedPreferences;

    private ContactManager(Context context) {
        contacts = new HashMap<>();
        favorites = new HashSet<>();
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadContacts();
        loadFavorites();
    }

    public static synchronized ContactManager getInstance(Context context) {
        if (instance == null) {
            instance = new ContactManager(context);
        }
        return instance;
    }

    public void addContact(String id, String name, String phone) {
        contacts.put(id, new Contact(id, name, phone));
        saveContacts();
    }

    public Contact getContact(String id) {
        return contacts.get(id);
    }

    public Contact getContactByName(String name) {
        for (Contact contact : contacts.values()) {
            if (contact.getName().equalsIgnoreCase(name)) {
                return contact;
            }
        }
        return null;
    }


    public ArrayList<Contact> getAllContacts() {
        return new ArrayList<>(contacts.values());
    }

    public ArrayList<Contact> getFavorites() {
        ArrayList<Contact> favoriteContacts = new ArrayList<>();
        for (String id : favorites) {
            Contact contact = contacts.get(id);
            if (contact != null) {
                favoriteContacts.add(contact);
            }
        }
        return favoriteContacts;
    }

    public void addFavorite(String id) {
        favorites.add(id);
        saveFavorites();
    }

    public void removeFavorite(String id) {
        favorites.remove(id);
        saveFavorites();
    }

    public boolean isFavorite(String id) {
        return favorites.contains(id);
    }

    private void saveContacts() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (Map.Entry<String, Contact> entry : contacts.entrySet()) {
            String id = entry.getKey();
            Contact contact = entry.getValue();
            editor.putString(id + "_name", contact.getName());
            editor.putString(id + "_phone", contact.getPhone());
        }
        editor.apply();
    }

    private void loadContacts() {
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            if (key.endsWith("_name")) {
                String id = key.substring(0, key.indexOf("_name"));
                String name = (String) entry.getValue();
                String phone = sharedPreferences.getString(id + "_phone", null);
                contacts.put(id, new Contact(id, name, phone));
            }
        }
    }

    private void saveFavorites() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("favorites", favorites);
        editor.apply();
    }

    private void loadFavorites() {
        favorites = sharedPreferences.getStringSet("favorites", new HashSet<String>());
    }

    public static class Contact {
        private String id;
        private String name;
        private String phone;

        public Contact(String id, String name, String phone) {
            this.id = id;
            this.name = name;
            this.phone = phone;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getPhone() {
            return phone;
        }
    }
}
