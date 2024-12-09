package com.example.giftr.business;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.giftr.business.entities.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class SharedUser {
    private static volatile SharedUser instance;                 // Holds the single instance of the class
    private User loggedUser;                            // The shared variable
    private final SharedPreferences sharedPreferences;  // SharedPreferences instance for data persistence
    public final static String SHARED_PREFERENCES_PERSISTENCE = "GiftrSharedPreference";

    // Private constructor to prevent instantiation from outside the class
    private SharedUser(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_PERSISTENCE, Context.MODE_PRIVATE);
        loggedUser = getFromSharedPrefs();
    }

    // Method to get the single instance of the class (ensuring thread-safety).
    public static synchronized SharedUser getInstance(Context context) {
        if (instance == null) {
            synchronized (SharedUser.class) {
                if (instance == null) {
                    instance = new SharedUser(context);
                }
            }
        }
        return instance;
    }

    // Getter for the shared variable
    public User getLoggedUser() {
        loggedUser = getFromSharedPrefs(); // Check persistence to get the latest logged user
        return loggedUser;
    }

    public String getUserAccessToken() {
        return getLoggedUser().getAccessToken();
    }

    // Setter for the shared variable
    public void setLoggedUser(User user) {
        loggedUser = user;
        saveToSharedPrefs(user);
    }

    public void clearLoggedUser() {
        loggedUser = null;
        saveToSharedPrefs(null);
    }

    private void saveToSharedPrefs(User user) {
        Gson gson = new Gson();
        String userObject = gson.toJson(user);

        // Get the editor to modify the shared preferences and apply the key-pair value.
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(User.LOGGED_USER, userObject);
        editor.apply();
    }

    private User getFromSharedPrefs() {
        String objectJSON = sharedPreferences.getString(User.LOGGED_USER, "");

        if (!objectJSON.isEmpty()) {
            Type type = new TypeToken<User>(){}.getType();
            return new Gson().fromJson(objectJSON, type);
        }

        return null;
    }
}

