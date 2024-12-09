package com.example.giftr.presentation.mainFragments.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.example.giftr.presentation.FullScreenActivity;
import com.example.giftr.R;
import com.example.giftr.business.ImageViewLoader;
import com.example.giftr.business.SharedUser;
import com.example.giftr.business.entities.Gift;
import com.example.giftr.business.entities.User;
import com.example.giftr.business.entities.Wishlist;
import com.example.giftr.persistence.*;
import com.example.giftr.persistence.swaggerApi.SwaggerFriendDAO;
import com.example.giftr.persistence.swaggerApi.SwaggerUserDAO;
import com.example.giftr.persistence.swaggerApi.SwaggerWishlistDAO;
import com.example.giftr.presentation.MainMenuActivity;

import org.json.JSONObject;

import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;

public class NotFriendProfileActivity extends AppCompatActivity {

	private User friend;
	private User loggedUser;
	private TextView tvUserName;
	private TextView tvNumWishlists;
	private TextView tvNumFriends;

	private TextView tvNumReserves;
	private Button bSendRequest;
	private ImageView ivProfilePicture;
	SharedUser sharedUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_not_friend_profile);

		getFriend();
		getUser();
		setupView();
		setupListeners();
	}

	private void getUser() {
		sharedUser = SharedUser.getInstance(getApplicationContext());
		loggedUser = sharedUser.getLoggedUser();
	}

	private void getFriend() {
		Bundle args = getIntent().getExtras();
		if (args != null) {
			friend = (User) args.getSerializable(User.FRIEND_NAME_TAG);
		}
	}

	private void setupView() {
		tvUserName = findViewById(R.id.notfriend_tvUserName);
		if (!friend.getFullName().trim().isEmpty())
			tvUserName.setText(friend.getFullName());
		else
			tvUserName.setText(R.string.unnamed_user);

		bSendRequest = findViewById(R.id.notfriend_bSendRequest);

		tvNumReserves = findViewById(R.id.notfriend_tvNumReserves);
		loadReserves();

		tvNumWishlists = findViewById(R.id.notfriend_tvNumWishlists);
		loadWishlists();

		tvNumFriends = findViewById(R.id.notfriend_tvNumFriends);
		loadFriends();

		ivProfilePicture = findViewById(R.id.notfriend_ivProfileImage);
		ImageViewLoader imageViewLoader = new ImageViewLoader(ivProfilePicture);
		imageViewLoader.loadImage(friend.getImagePath());
	}

	private void loadFriends() {
		UserDAO userDAO = new SwaggerUserDAO();
		userDAO.getUserFriendsById(friend.getId(), loggedUser.getAccessToken(), getApplicationContext(), new UserDAO.UserListResponseCallback() {
			@Override
			public void onSuccess(List<User> response) {
				tvNumFriends.setText(String.valueOf(response.size()));
			}

			@Override
			public void onError(VolleyError error) {
			}
		});
	}

	private void loadReserves() {
		UserDAO userDAO = new SwaggerUserDAO();
		userDAO.getUserReservesById(friend.getId(), loggedUser.getAccessToken(), getApplicationContext(), new GiftDAO.GiftListResponseCallback() {
			@Override
			public void onSuccess(List<Gift> response) {
				tvNumReserves.setText(String.valueOf(response.size()));
			}

			@Override
			public void onError(VolleyError error) {

			}
		});
	}

	private void loadWishlists() {
		WishlistDAO wishlistDAO = new SwaggerWishlistDAO();
		wishlistDAO.getWishlistsFromUser(friend.getId(), MainMenuActivity.loggedUser.getAccessToken(), this, new WishlistDAO.WishlistListResponseCallback() {
			@Override
			public void onSuccess(List<Wishlist> response) {
				tvNumWishlists.setText(String.valueOf(response.size()));
			}

			@Override
			public void onError(VolleyError error) {

			}
		});
	}

	private void setupListeners() {
		bSendRequest.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("UseCompatLoadingForDrawables")
			@Override
			public void onClick(View v) {
				bSendRequest.setBackground(getDrawable(R.drawable.round_gray_button));
				FriendDAO friendDAO = new SwaggerFriendDAO();
				friendDAO.sendRequest(friend.getId(), MainMenuActivity.loggedUser.getAccessToken(), NotFriendProfileActivity.this, new ApiResponseCallback() {
					@Override
					public void onSuccess(JSONObject response) {

					}

					@Override
					public void onError(VolleyError error) {
						if (error.networkResponse != null) {
							int statusCode = error.networkResponse.statusCode;
							switch (statusCode) {
								case 409:
									// Friend already requested
									StyleableToast.makeText(NotFriendProfileActivity.this, getString(R.string.toast_already_friend_request), R.style.changesApplied).show();
									break;
								default:
									// General error with api
									StyleableToast.makeText(NotFriendProfileActivity.this, getString(R.string.toast_no_connection), R.style.changesApplied).show();
									break;
							}
						}
						else {
							StyleableToast.makeText(NotFriendProfileActivity.this, getString(R.string.toast_no_connection), R.style.changesApplied).show();
						}
					}
				});
			}
		});

		ivProfilePicture.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(NotFriendProfileActivity.this, FullScreenActivity.class);
				intent.putExtra(User.PROFILE_PICTURE_TAG, friend.getImagePath());
				intent.putExtra(User.FRIEND_NAME_TAG, friend);
				startActivity(intent);
			}
		});
	}
}
