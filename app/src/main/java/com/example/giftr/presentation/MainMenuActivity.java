package com.example.giftr.presentation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.giftr.R;
import com.example.giftr.business.SharedUser;
import com.example.giftr.business.entities.User;
import com.example.giftr.persistence.requester.JsonRequester;
import com.example.giftr.presentation.mainFragments.HomeFragment;
import com.example.giftr.presentation.mainFragments.MessagesFragment;
import com.example.giftr.presentation.mainFragments.ProfileFragment;
import com.example.giftr.presentation.mainFragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainMenuActivity extends AppCompatActivity {

    private BottomNavigationView navigationView;
    public static User loggedUser;
    private Fragment previousFragment;
    private int selectedMenuItemId = -1;
    public final static String FRAGMENT_ID_TAG = "FragmentID";
    public static final String LAST_FRAGMENT_TAG = "LastFragmentTag";
    public static final String LOAD_FRAGMENT = "LoadFragment";
    private SharedPreferences sharedPreferences;
    private boolean comeFromLoginOrRegister;
    private SharedUser sharedUser;
    private int lastSelectedMenuItemId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        getLoggedUser();

        setupView();
        checkLoginRegister();
        setupListeners();
    }

    private void checkLoginRegister() {
        comeFromLoginOrRegister = false;
        if (getIntent().getExtras() != null) {
            comeFromLoginOrRegister = getIntent().getBooleanExtra(LoginActivity.LOGIN_REGISTER_ACTIVITY, false);
            getIntent().putExtra(LoginActivity.LOGIN_REGISTER_ACTIVITY, false); // Esborrar que ve del login.
        }

        if (comeFromLoginOrRegister) {
            navigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    private void getLoggedUser() {
        sharedUser = SharedUser.getInstance(getApplicationContext());
        loggedUser = sharedUser.getLoggedUser();
    }

    private void setupView() {
        navigationView = findViewById(R.id.bottomNavigationView);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREFERENCES_PERSISTENCE, MODE_PRIVATE);

        // Restore the last displayed fragment
        if (fragment == null) {
            fragment = new HomeFragment();

            Bundle args = getIntent().getExtras();
            fragment.setArguments(args);
            previousFragment = fragment;

            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }

    private void setupListeners() {
        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                // Check if the newly selected item is the same as the current selection
                if (itemId == selectedMenuItemId) {
                    return false;
                }
                JsonRequester.cancelRequest();

                lastSelectedMenuItemId = selectedMenuItemId;
                selectedMenuItemId = itemId;
                switch (itemId) {
                    case R.id.nav_home -> replaceFragment(new HomeFragment());
                    case R.id.nav_search -> replaceFragment(new SearchFragment());
                    case R.id.nav_messages -> replaceFragment(new MessagesFragment());
                    case R.id.nav_profile -> replaceFragment(new ProfileFragment());
                }
                return true;
            }
        });
    }

    public void replaceFragment(Fragment newFragment) {
        FragmentManager fm = getSupportFragmentManager();

        // Get current fragment to save it incase the user wants to go back.
        previousFragment = fm.findFragmentById(R.id.fragment_container);

        // Pass the logged user instance to the new Fragment.
        Bundle args;

        if (newFragment.getArguments() != null) {
            args = newFragment.getArguments();
        }
        else {
            args = new Bundle();
        }

        args.putSerializable(User.LOGGED_USER, loggedUser);
        newFragment.setArguments(args);

        // Avoid overlapping frames, instead just replace (not add) the fragment.
        fm.beginTransaction().replace(R.id.fragment_container, newFragment).commitNow();
    }

    @Override
    public void onBackPressed() {
        if (selectedMenuItemId != R.id.nav_home) {
            navigationView.setSelectedItemId(lastSelectedMenuItemId);
        } else {
            finishAffinity();
        }
    }

    public void changeActivity(Class<? extends AppCompatActivity> targetActivity, Bundle extraArgs) {
        Intent intent = new Intent(MainMenuActivity.this, targetActivity);
        intent.putExtra(User.LOGGED_USER, loggedUser);
        intent.putExtra(LAST_FRAGMENT_TAG, selectedMenuItemId);

        saveCurrentFragment(selectedMenuItemId);

        if (extraArgs != null) {
            intent.putExtras(extraArgs);
        }

        startActivityForResult(intent, 1);
    }

    public void saveCurrentFragment(int fragmentID) {
        // Save the fragment identifier in SharedPreferences
        SharedPreferences preferences = getSharedPreferences(SharedUser.SHARED_PREFERENCES_PERSISTENCE, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(LAST_FRAGMENT_TAG, fragmentID);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Retrieve the saved fragment identifier from SharedPreferences
        SharedPreferences preferences = getSharedPreferences(LoginActivity.SHARED_PREFERENCES_PERSISTENCE, MODE_PRIVATE);
        int savedFragmentId = preferences.getInt(LAST_FRAGMENT_TAG, -1);

        if (savedFragmentId != -1 && !comeFromLoginOrRegister) {
            navigationView.setSelectedItemId(savedFragmentId);
        }
        else {
            comeFromLoginOrRegister = false;
        }
    }
}