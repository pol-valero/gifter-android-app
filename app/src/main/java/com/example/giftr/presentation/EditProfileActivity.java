package com.example.giftr.presentation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.android.volley.VolleyError;
import com.example.giftr.R;
import com.example.giftr.business.ImageViewLoader;
import com.example.giftr.business.SharedUser;
import com.example.giftr.business.entities.User;
import com.example.giftr.persistence.ApiResponseCallback;
import com.example.giftr.persistence.ImageDAO;
import com.example.giftr.persistence.UserDAO;
import com.example.giftr.persistence.freeimagehost.FreeImageDAO;
import com.example.giftr.persistence.swaggerApi.SwaggerUserDAO;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;

import static com.example.giftr.presentation.RegisterActivity.PICK_IMAGE_REQUEST;
import static com.example.giftr.presentation.RegisterActivity.VALID_EMAIL_ADDRESS_REGEX;
import static com.example.giftr.presentation.RegisterActivity.VALID_PASSWORD_REGEX;

import io.github.muddz.styleabletoast.StyleableToast;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView ivProfileImage;
    private EditText etName;
    private EditText etLastName;
    private EditText etEmail;
    private EditText etPassword1;
    private EditText etPassword2;
    private Button bApplyChanges;
    private ImageButton bProfileImage;
    private Bitmap profileImage = null;
    private boolean profilePictureChanged;
    private SharedUser sharedUser;
    private User loggedUser;
    private final static String IMAGE_DIR = "image/*";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        sharedUser = SharedUser.getInstance(getApplicationContext());
        loggedUser = sharedUser.getLoggedUser();

        setupView();
    }

    private void setupView() {
        ivProfileImage = findViewById(R.id.editProfile_etProfileImage);

        ImageViewLoader imageViewLoader = new ImageViewLoader(ivProfileImage);
        imageViewLoader.loadImage(loggedUser.getImagePath());

        ivProfileImage.setVisibility(View.VISIBLE);

        etName = findViewById(R.id.editProfile_etName);
        etLastName = findViewById(R.id.editProfile_etLastName);
        etEmail = findViewById(R.id.editProfile_etEmail);
        etPassword1 = findViewById(R.id.editProfile_etPassword1);
        etPassword2 = findViewById(R.id.editProfile_etPassword2);
        bApplyChanges = findViewById(R.id.editProfile_bApplyChanges);
        bProfileImage = findViewById(R.id.editProfile_bProfileImage);

        etName.setText(loggedUser.getName());
        etLastName.setText(loggedUser.getLastName());
        etEmail.setText(loggedUser.getEmail());

        bApplyChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validName() && validEmail() && checkPassword()) {
                    if (profilePictureChanged) {
                        uploadProfilePicture();
                    }
                    else {
                        uploadUser(loggedUser.getImagePath());
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

    private void uploadProfilePicture() {
        ImageDAO imageDAO = new FreeImageDAO();
        imageDAO.uploadImage(profileImage, getApplicationContext(), new ImageDAO.ImageResponseCallback() {
            @Override
            public void onSuccess(String imageURL) {
                uploadUser(imageURL);
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }

    private void uploadUser(String imageURL) {
        User tempUser = new User(etName.getText().toString(), etLastName.getText().toString(), etEmail.getText().toString(), etPassword1.getText().toString(), imageURL);
        UserDAO userDAO = new SwaggerUserDAO();
        userDAO.editUser(tempUser, loggedUser.getAccessToken(), getApplicationContext(), new UserDAO.UserResponseCallback() {
            @Override
            public void onSuccess(User response) {
                loggedUser = response;
                loggedUser.setPassword(etPassword1.getText().toString());

                getAccessToken();
            }

            @Override
            public void onError(VolleyError error) {
            }
        });
    }

    private void getAccessToken() {
        UserDAO userDAO = new SwaggerUserDAO();
        userDAO.loginUser(loggedUser.getEmail(), loggedUser.getPassword(), getApplicationContext(), new ApiResponseCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    String accessToken = response.getString("accessToken");
                    loggedUser.setAccessToken(accessToken);
                    sharedUser.setLoggedUser(loggedUser);

                    StyleableToast.makeText(EditProfileActivity.this, getString(R.string.toast_changed_applied), R.style.changesApplied).show();

                    Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                    intent.putExtra(User.LOGGED_USER, loggedUser);
                    startActivity(intent);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(VolleyError error) {
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
            etEmail.setError(getString(R.string.email_not_valid));
            etEmail.requestFocus();
            return false;
        }
    }

    private boolean validName() {
        if (etName.getText().toString().trim().isEmpty()) {
            etName.setError(getString(R.string.missing_field));
            etName.requestFocus();
            return false;
        }

        if (etLastName.getText().toString().trim().isEmpty()) {
            etLastName.setError(getString(R.string.missing_field));
            etLastName.requestFocus();
            return false;
        }

        return true;
    }

    private boolean checkPassword() {
        if (etPassword1.getText().toString().trim().isEmpty()) {
            etPassword1.setError(getString(R.string.missing_field));
            etPassword1.requestFocus();
            return false;
        }

        if (etPassword2.getText().toString().trim().isEmpty()) {
            etPassword2.setError(getString(R.string.missing_field));
            etPassword2.requestFocus();
            return false;
        }

        if (!etPassword1.getText().toString().equals(etPassword2.getText().toString())) {
            etPassword2.setError(getString(R.string.passwords_not_match));
            etPassword2.requestFocus();
            return false;
        }

        Matcher matcher = VALID_PASSWORD_REGEX.matcher(etPassword1.getText().toString());
        if (!matcher.matches()) {
            etPassword2.setError(getString(R.string.password_not_valid));
            etPassword2.requestFocus();
            return false;
        }

        return true;
    }

    public void openGallery() {
        Intent intent = new Intent();
        intent.setType(IMAGE_DIR);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.gallery_select_picture)), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                profileImage = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ivProfileImage.setImageBitmap(profileImage);
                profilePictureChanged = true;
            } catch (IOException e) {
                e.printStackTrace();
                profilePictureChanged = false;
            }
        }
    }
}