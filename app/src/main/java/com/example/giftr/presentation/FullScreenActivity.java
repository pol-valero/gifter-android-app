package com.example.giftr.presentation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.giftr.R;
import com.example.giftr.business.ImageViewLoader;
import com.example.giftr.business.entities.User;

public class FullScreenActivity extends AppCompatActivity {

    private ImageView fullScreenImageView;
    private User user;
    private TextView tvUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        fullScreenImageView = findViewById(R.id.fullScreen_ivProfilePicture);
        user = (User) getIntent().getSerializableExtra(User.FRIEND_NAME_TAG);

        tvUsername = findViewById(R.id.fullScreen_tvUserName);
        tvUsername.setText(user.getFullName());

        // Retrieve the image URL or data from the Intent extra
        String imageUrl = getIntent().getStringExtra(User.PROFILE_PICTURE_TAG);

        ImageViewLoader imageViewLoader = new ImageViewLoader(fullScreenImageView);
        imageViewLoader.loadImage(imageUrl);
    }
}