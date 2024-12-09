package com.example.giftr.presentation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.android.volley.VolleyError;
import com.example.giftr.R;
import com.example.giftr.business.SharedUser;
import com.example.giftr.business.entities.User;
import com.example.giftr.persistence.ApiResponseCallback;
import com.example.giftr.persistence.ImageDAO;
import com.example.giftr.persistence.UserDAO;
import com.example.giftr.persistence.freeimagehost.FreeImageDAO;
import com.example.giftr.persistence.swaggerApi.SwaggerUserDAO;

import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.muddz.styleabletoast.StyleableToast;

public class RegisterActivity extends AppCompatActivity {
    private ImageView etProfileImage;
    private EditText etName;
    private EditText etLastName;
    private EditText etEmail;
    private EditText etPassword1;
    private EditText etPassword2;
    private TextView tvErrorMessage;
    private Button bSignUp;
    private ImageButton bProfileImage;
    private User loggedUser;
    private Bitmap profileImage = null;
    private String profileImageURL = User.DEFAULT_PROFILE_PICTURE;
    public final static int ID_ACTIVITY = 2;
    public static final int PICK_IMAGE_REQUEST = 3;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    public static final Pattern VALID_PASSWORD_REGEX = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$", Pattern.CASE_INSENSITIVE);
    private SharedUser sharedUser;
    private Drawable errorIcon;
    private static final String IMAGE_DIR = "image/*";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().setStatusBarColor(getResources().getColor(R.color.main_color));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.main_color));
        sharedUser = SharedUser.getInstance(getApplicationContext());
        setupView();
    }

    private void setupView() {
        etProfileImage = findViewById(R.id.editProfile_etProfileImage);
        Picasso.get()
                .load(profileImageURL)
                .into(etProfileImage);
        etProfileImage.setVisibility(View.VISIBLE);

        etName = findViewById(R.id.editProfile_etName);
        etLastName = findViewById(R.id.editProfile_etLastName);
        etEmail = findViewById(R.id.editProfile_etEmail);
        etPassword1 = findViewById(R.id.editProfile_etPassword1);
        etPassword2 = findViewById(R.id.editProfile_etPassword2);
        tvErrorMessage = findViewById(R.id.register_tvErrorMessage);

        tvErrorMessage.setVisibility(View.INVISIBLE);

        bSignUp = findViewById(R.id.editProfile_bSignUp);
        bProfileImage = findViewById(R.id.editProfile_bProfileImage);

        // https://stackoverflow.com/questions/13707282/customizing-the-seterror-icon-for-edittext
        errorIcon = ContextCompat.getDrawable(this, R.drawable.custom_error_icon);
        Objects.requireNonNull(errorIcon).setBounds(0, 0, errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight());

        bSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validName() && validEmail() && checkPassword()) {
                    if (profileImage != null) {
                        postImage(profileImage);
                    }
                    else {
                        UserDAO userDAO = new SwaggerUserDAO();
                        registerUser(userDAO);
                    }
                }
            }
        });

        bProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
    }

    private boolean validEmail() {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(etEmail.getText());
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

    private boolean validName() {
        if (etName.getText().toString().trim().isEmpty()) {
            etName.setError(getString(R.string.missing_field), errorIcon);
            etName.requestFocus();
            return false;
        }

        if (etLastName.getText().toString().trim().isEmpty()) {
            etLastName.setError(getString(R.string.missing_field), errorIcon);
            etLastName.requestFocus();
            return false;
        }

        return true;
    }

    private boolean checkPassword() {
        if (etPassword1.getText().toString().trim().isEmpty()) {
            etPassword1.setError(getString(R.string.missing_field), errorIcon);
            etPassword1.requestFocus();
            return false;
        }

        if (etPassword2.getText().toString().trim().isEmpty()) {
            etPassword2.setError(getString(R.string.missing_field), errorIcon);
            etPassword2.requestFocus();
            return false;
        }

        if (!etPassword1.getText().toString().equals(etPassword2.getText().toString())) {
            etPassword2.setError(getString(R.string.passwords_not_match), errorIcon);
            etPassword2.requestFocus();
            return false;
        }

        Matcher matcher = VALID_PASSWORD_REGEX.matcher(etPassword1.getText().toString());
        if (!matcher.matches()) {
            etPassword2.setError(getString(R.string.password_not_valid), errorIcon);
            etPassword2.requestFocus();
            return false;
        }

        return true;
    }

    private void registerUser(UserDAO userDAO) {
        String name = etName.getText().toString();
        String lastName = etLastName.getText().toString();

        User user = new User(name, lastName, etEmail.getText().toString(), etPassword1.getText().toString(), profileImageURL);
        userDAO.createUser(user, RegisterActivity.this, new UserDAO.UserResponseCallback() {
            @Override
            public void onSuccess(User response) {
                loggedUser = response;
                loginUser(userDAO);
            }

            @Override
            public void onError(VolleyError error) {
                if (error.networkResponse != null) {
                    int statusCode = error.networkResponse.statusCode;
                    switch (statusCode) {
                        case 409:
                            // Email address already registered
                            etEmail.setError(getString(R.string.email_already_registered));
                            etEmail.requestFocus();
                            break;
                        case 406:
                            // Missing parameters
                            etName.setError(getString(R.string.missing_field));
                            etName.requestFocus();
                        default:
                            // General error with api
                            StyleableToast.makeText(RegisterActivity.this, getString(R.string.toast_no_connection), R.style.changesApplied).show();
                            break;
                    }
                }
                else {
                    StyleableToast.makeText(RegisterActivity.this, getString(R.string.toast_no_connection), R.style.changesApplied).show();
                }
            }
        });
    }

    private void loginUser(UserDAO userDAO) {
        userDAO.loginUser(etEmail.getText().toString(), etPassword1.getText().toString(), RegisterActivity.this, new ApiResponseCallback() {
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
                // Handle the error case
            }
        });
    }

    private void changeMainActivity() {
        Intent intent = new Intent(RegisterActivity.this, MainMenuActivity.class);
        intent.putExtra(User.LOGGED_USER, loggedUser);
        startActivityForResult(intent, ID_ACTIVITY);
    }

    public void openGallery() {
        Intent intent = new Intent();
        intent.setType(IMAGE_DIR);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.gallery_select_picture)), PICK_IMAGE_REQUEST);
    }

    // Handle the result of the image selection
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                profileImage = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ImageView imageView = findViewById(R.id.editProfile_etProfileImage);
                imageView.setImageBitmap(profileImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void postImage(Bitmap imageBitmap) {
        ImageDAO imageDAO = new FreeImageDAO();
        imageDAO.uploadImage(imageBitmap, this, new ImageDAO.ImageResponseCallback() {
            @Override
            public void onSuccess(String imageURL) {
                profileImageURL = imageURL;

                UserDAO userDAO = new SwaggerUserDAO();
                registerUser(userDAO);
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }
}