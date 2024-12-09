package com.example.giftr.presentation.mainFragments.search.usersList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.example.giftr.R;
import com.example.giftr.business.ImageViewLoader;
import com.example.giftr.business.entities.User;
import com.example.giftr.persistence.FriendDAO;
import com.example.giftr.persistence.swaggerApi.SwaggerFriendDAO;
import com.example.giftr.presentation.MainMenuActivity;
import com.example.giftr.presentation.mainFragments.profile.FriendProfileActivity;
import com.example.giftr.presentation.mainFragments.profile.NotFriendProfileActivity;
import com.example.giftr.presentation.mainFragments.profile.friendsList.FriendsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class UsersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

	private Activity activity;
	private User user;
	private User loggedUser;
	private ImageView ivProfilePicture;
	private TextView tvUsername;
	private View vDivider;

	public UsersViewHolder(@NonNull View itemView, Activity activity, User loggedUser) {
		super(itemView);
		this.loggedUser = loggedUser;

		setupView();

		this.activity = activity;
	}

	private void setupView() {
		ivProfilePicture = itemView.findViewById(R.id.users_ivProfilePicture);
		tvUsername = itemView.findViewById(R.id.users_tvUserName);
		itemView.setOnClickListener(this);
		vDivider = itemView.findViewById(R.id.search_vDivider);
	}

	public void bind(User user, boolean checkLast) {
		this.user = user;
		if (!user.getFullName().trim().isEmpty())
			tvUsername.setText(user.getFullName());
		else
			tvUsername.setText(R.string.unnamed_user);

		if (checkLast) {
			vDivider.setVisibility(View.GONE);
		}
		else {
			vDivider.setVisibility(View.VISIBLE);
		}

		ImageViewLoader imageViewLoader = new ImageViewLoader(ivProfilePicture);
		imageViewLoader.loadImage(user.getImagePath());
	}

	/**
	 * Called when a view has been clicked.
	 *
	 * @param v The view that was clicked.
	 */
	@Override
	public void onClick(View v) {
		if (user.sameUser(loggedUser)) {
			if (activity instanceof MainMenuActivity) {
				BottomNavigationView navigationView = activity.findViewById(R.id.bottomNavigationView);
				navigationView.setSelectedItemId(R.id.nav_profile);
			}
			else {
				if (activity instanceof FriendsActivity) {
					Intent intent = new Intent(activity, MainMenuActivity.class);
					intent.putExtra("fragmentName", MainMenuActivity.class.getName());
					intent.putExtra(User.LOGGED_USER, loggedUser);
					intent.putExtra(User.FRIEND_NAME_TAG, user);

					intent.putExtra(MainMenuActivity.LOAD_FRAGMENT, "Profile");
					// Change to MainActivity and start the home fragment.
					activity.startActivityForResult(intent, 3);
				}
			}
		}
		else {
			isFriend();
		}

	}

	private void isFriend() {
		FriendDAO friendDAO = new SwaggerFriendDAO();
		friendDAO.getAllFriends(MainMenuActivity.loggedUser.getAccessToken(), activity, new FriendDAO.FriendResponseCallback() {
			@Override
			public void onSuccess(List<User> response) {
				boolean isFriend = false;

				for (User friend : response) {
					if (friend.sameUser(user)) {
						isFriend = true;
						break;
					}
				}

				Class<? extends AppCompatActivity> newActivity = null;

				if (isFriend) {
					newActivity = FriendProfileActivity.class;
				}
				else {
					newActivity = NotFriendProfileActivity.class;
				}

				if (activity instanceof MainMenuActivity) {
					MainMenuActivity mainMenuActivity = (MainMenuActivity) activity;

					Bundle args;

					if (mainMenuActivity.getIntent().getExtras() != null) {
						args = mainMenuActivity.getIntent().getExtras();
					}
					else {
						args = new Bundle();
					}

					args.putSerializable(User.FRIEND_NAME_TAG, user);
					mainMenuActivity.changeActivity(newActivity, args);

				} else if (activity instanceof FriendsActivity) {
					FriendsActivity anotherActivity = (FriendsActivity) activity;

					Bundle args;

					if (anotherActivity.getIntent().getExtras() != null) {
						args = anotherActivity.getIntent().getExtras();
					}
					else {
						args = new Bundle();
					}

					args.putSerializable(User.FRIEND_NAME_TAG, user);
					anotherActivity.changeActivity(newActivity, args);
				}
			}

			@Override
			public void onError(VolleyError error) {

			}
		});
	}
}
