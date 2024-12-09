package com.example.giftr.presentation.mainFragments.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.giftr.presentation.FullScreenActivity;
import com.example.giftr.R;
import com.example.giftr.business.ImageViewLoader;
import com.example.giftr.business.SharedUser;
import com.example.giftr.business.entities.Gift;
import com.example.giftr.business.entities.User;
import com.example.giftr.business.entities.Wishlist;
import com.example.giftr.persistence.GiftDAO;
import com.example.giftr.persistence.UserDAO;
import com.example.giftr.persistence.WishlistDAO;
import com.example.giftr.persistence.swaggerApi.SwaggerUserDAO;
import com.example.giftr.persistence.swaggerApi.SwaggerWishlistDAO;
import com.example.giftr.presentation.MainMenuActivity;
import com.example.giftr.presentation.mainFragments.home.WishlistAdapter;
import com.example.giftr.presentation.mainFragments.messages.messageHistory.MessageHistoryActivity;
import com.example.giftr.presentation.mainFragments.profile.friendsList.FriendsActivity;
import com.example.giftr.presentation.mainFragments.profile.giftList.SentGiftAdapter;

import java.util.List;

public class FriendProfileActivity extends AppCompatActivity {

	private User friend;
	private User loggedUser;
	private TextView tvUserName;
	private TextView tvNumFriends;
	private TextView tvNumWishlists;
	private Button bSendMessage;
	private Button bFriendList;
	private TextView tvNumReserves;
	private RecyclerView rvWishlists;
	private WishlistAdapter adapterWishlists;
	private RecyclerView rvSentGifts;
	private SentGiftAdapter giftAdapter;
	private ImageView ivProfilePicture;
	private SharedUser sharedUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_profile);

		getFriend();
		getUser();
		setupView();
		setupListeners();
	}

	private void getUser() {
		sharedUser = SharedUser.getInstance(getApplicationContext());
	}

	private void getFriend() {
		Bundle args = getIntent().getExtras();
		if (args != null) {
			friend = (User) args.getSerializable(User.FRIEND_NAME_TAG);
		}
	}

	private void setupView() {
		tvUserName = findViewById(R.id.friendProfile_tvUserName);
		if (!friend.getFullName().trim().isEmpty())
			tvUserName.setText(friend.getFullName());
		else
			tvUserName.setText(R.string.unnamed_user);

		tvNumReserves = findViewById(R.id.friendProfile_tvNumReserves);
		tvNumWishlists = findViewById(R.id.friendProfile_tvNumWishlists);
		tvNumFriends = findViewById(R.id.friendProfile_tvNumFriends);

		ivProfilePicture = findViewById(R.id.friendProfile_ivProfileImage);
		ImageViewLoader imageViewLoader = new ImageViewLoader(ivProfilePicture);
		imageViewLoader.loadImage(friend.getImagePath());

		bSendMessage = findViewById(R.id.friendProfile_bSendMessage);
		bFriendList = findViewById(R.id.friendProfile_bFriends);

		rvWishlists = findViewById(R.id.friendProfile_rvWishlists);
		rvWishlists.setLayoutManager(new LinearLayoutManager(this));

		rvSentGifts = findViewById(R.id.friendProfile_rvSentGifts);
		rvSentGifts.setLayoutManager(new LinearLayoutManager(this));

		loadFriends();
		loadWishlists();
		loadReserves();
	}

	private void setupListeners() {
		bSendMessage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				messageFriend();
			}
		});

		bFriendList.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle args;
				if (getIntent().getExtras() != null) {
					args = getIntent().getExtras();
				}
				else {
					args = new Bundle();
				}

				args.putSerializable(User.FRIEND_NAME_TAG, friend);
				changeActivity(FriendsActivity.class, args);
 			}
		});

		ivProfilePicture.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FriendProfileActivity.this, FullScreenActivity.class);
				intent.putExtra(User.PROFILE_PICTURE_TAG, friend.getImagePath());
				intent.putExtra(User.FRIEND_NAME_TAG, friend);
				startActivity(intent);
			}
		});
	}

	private void messageFriend() {
		Class<? extends AppCompatActivity> newActivity = MessageHistoryActivity.class;
		Bundle args;

		if (getIntent().getExtras() != null) {
			args = getIntent().getExtras();
		}
		else {
			args = new Bundle();
		}

		args.putSerializable(User.FRIEND_NAME_TAG, friend);
		changeActivity(newActivity, args);
	}

	private void loadWishlists() {
		WishlistDAO wishlistDAO = new SwaggerWishlistDAO();
		wishlistDAO.getWishlistsFromUser(friend.getId(), sharedUser.getUserAccessToken(), this, new WishlistDAO.WishlistListResponseCallback() {
			@Override
			public void onSuccess(List<Wishlist> response) {
				tvNumWishlists.setText(String.valueOf(response.size()));
				adapterWishlists = new WishlistAdapter(response, FriendProfileActivity.this, sharedUser.getLoggedUser());
				rvWishlists.setAdapter(adapterWishlists);
			}

			@Override
			public void onError(VolleyError error) {

			}
		});
	}

	private void loadFriends() {
		UserDAO userDAO = new SwaggerUserDAO();
		userDAO.getUserFriendsById(friend.getId(), sharedUser.getUserAccessToken(), getApplicationContext(), new UserDAO.UserListResponseCallback() {
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
		userDAO.getUserReservesById(friend.getId(), sharedUser.getUserAccessToken(), getApplicationContext(), new GiftDAO.GiftListResponseCallback() {
			@Override
			public void onSuccess(List<Gift> response) {
				tvNumReserves.setText(String.valueOf(response.size()));
				giftAdapter = new SentGiftAdapter(response, FriendProfileActivity.this, sharedUser.getLoggedUser());
				rvSentGifts.setAdapter(giftAdapter);
			}

			@Override
			public void onError(VolleyError error) {

			}
		});
	}

	private void changeActivity(Class<? extends AppCompatActivity> targetActivity, Bundle extraArgs) {
		Intent intent = new Intent(FriendProfileActivity.this, targetActivity);
		intent.putExtra(User.LOGGED_USER, sharedUser.getLoggedUser());

		if (extraArgs != null) {
			intent.putExtras(extraArgs);
		}

		startActivityForResult(intent, 1);
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, MainMenuActivity.class);
		startActivity(intent);
		finish(); // Optional: finish the current activity to prevent it from appearing on the back stack
	}

	@Override
	public void onResume() {
		super.onResume();

		loadFriends();
		loadWishlists();
	}
}
