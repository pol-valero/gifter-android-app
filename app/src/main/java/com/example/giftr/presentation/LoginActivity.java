package com.example.giftr.presentation;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.android.volley.VolleyError;
import com.example.giftr.R;
import com.example.giftr.business.SharedUser;
import com.example.giftr.business.entities.User;
import com.example.giftr.persistence.ApiResponseCallback;
import com.example.giftr.persistence.UserDAO;
import com.example.giftr.persistence.swaggerApi.SwaggerUserDAO;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.regex.Matcher;

import io.github.muddz.styleabletoast.StyleableToast;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private Button bSignUp;
    private Button bLogIn;
    private User loggedUser;
    public final static int ID_ACTIVITY = 1;
    public final static String SHARED_PREFERENCES_PERSISTENCE = "GiftrSharedPreference";
    public final static String LOGIN_REGISTER_ACTIVITY = "ComingFromLoginOrRegister";
    private String userAccessToken;
    private SharedUser sharedUser;
    private Drawable errorIcon;
    public final static String ACTIVITY_SOURCE = "source";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().setBackgroundDrawable(null);

        sharedUser = SharedUser.getInstance(getApplicationContext());

        // Check if the activity was launched from the LogOut screen.
        checkLogout();

        // Check if the user is already saved in persistence (Shared Preferences).
        setupView();

        loggedUser = sharedUser.getLoggedUser();
        if (loggedUser != null) {
            loginUser(new SwaggerUserDAO());
        }
    }

    private void checkLogout() {
        Bundle args = getIntent().getExtras();

        if (args != null) {
            if (args.getString(ACTIVITY_SOURCE) != null && !args.getString(ACTIVITY_SOURCE).isEmpty()) {
                sharedUser.clearLoggedUser();
            }
        }
    }

    private void setupView() {
        etEmail = findViewById(R.id.login_etEmail);
        etPassword = findViewById(R.id.login_etPassword);
        bSignUp = findViewById(R.id.login_bRegister);
        bLogIn = findViewById(R.id.login_bLogin);
        errorIcon = ContextCompat.getDrawable(this, R.drawable.custom_error_icon);
        Objects.requireNonNull(errorIcon).setBounds(0, 0, errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight());

        bSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, ID_ACTIVITY);
            }
        });

        bLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkFields();
            }
        });
    }

    private void checkFields() {
        if (validEmail() && validPassword()) {
            UserDAO userDAO = new SwaggerUserDAO();
            getUserAccessToken(userDAO);
        }
    }

    private boolean validPassword() {
        if (etPassword.getText().toString().trim().isEmpty()) {
            etPassword.setError(getString(R.string.missing_field), errorIcon);
            etPassword.requestFocus();
            return false;
        }
        else {
            etPassword.setError(null);
            return true;
        }
    }

    private boolean validEmail() {
        Matcher matcher = RegisterActivity.VALID_EMAIL_ADDRESS_REGEX.matcher(etEmail.getText());

        if (matcher.matches()) {
            etEmail.setError(null);
            return true;
        }
        else {
            etEmail.setError(getString(R.string.email_not_valid), errorIcon);
            etEmail.requestFocus();
            return false;
        }
    }

    private void loginUser(UserDAO userDAO) {
        userDAO.loginUser(loggedUser.getEmail(), loggedUser.getPassword(), LoginActivity.this, new ApiResponseCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    loggedUser.setAccessToken(response.getString("accessToken"));
                    sharedUser.setLoggedUser(loggedUser);
                    changeMainActivity();

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(VolleyError error) {
            }
        });
    }

    private void getUserAccessToken(UserDAO userDAO) {
        userDAO.loginUser(etEmail.getText().toString(), etPassword.getText().toString(), LoginActivity.this, new ApiResponseCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    userAccessToken = response.getString("accessToken");
                    getUserByEmail(userDAO);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(VolleyError error) {
                if (error.networkResponse != null) {
                    int statusCode = error.networkResponse.statusCode;
                    switch (statusCode) {
                        case 401:
                            // Wrong username or password
                            etPassword.setError(getString(R.string.password_or_email_not_valid), errorIcon);
                            etPassword.requestFocus();
                            break;
                        case 406:
                            // Missing parameters
                            etEmail.setError(getString(R.string.missing_field));
                            etEmail.requestFocus();
                        default:
                            // General error with api
                            StyleableToast.makeText(LoginActivity.this, getString(R.string.toast_no_connection), R.style.changesApplied).show();
                            break;
                    }
                }
                else {
                    StyleableToast.makeText(LoginActivity.this, getString(R.string.toast_no_connection), R.style.changesApplied).show();
                }
            }
        });
    }

    private void getUserByEmail(UserDAO userDAO) {
        userDAO.getUserByEmail(etEmail.getText().toString(), userAccessToken,LoginActivity.this, new UserDAO.UserResponseCallback() {
            @Override
            public void onSuccess(User response) {
                loggedUser = response;
                loggedUser.setPassword(etPassword.getText().toString());
                loginUser(userDAO);
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }

    private void changeMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
        intent.putExtra(User.LOGGED_USER, loggedUser);
        intent.putExtra(LOGIN_REGISTER_ACTIVITY, true);
        startActivityForResult(intent, ID_ACTIVITY);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}