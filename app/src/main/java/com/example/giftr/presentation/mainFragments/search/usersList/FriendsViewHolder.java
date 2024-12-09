package com.example.giftr.presentation.mainFragments.search.usersList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.example.giftr.R;
import com.example.giftr.business.ImageViewLoader;
import com.example.giftr.business.entities.User;
import com.example.giftr.persistence.ApiResponseCallback;
import com.example.giftr.persistence.FriendDAO;
import com.example.giftr.persistence.swaggerApi.SwaggerFriendDAO;
import com.example.giftr.presentation.mainFragments.profile.FriendProfileActivity;

import org.json.JSONObject;

public class FriendsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private User loggedUser;
    private Activity activity;
    private User friend;
    private ImageView ivProfilePicture;
    private TextView tvUsername;
    private Button bRemoveFriend;
    private UsersAdapter usersAdapter;

    private View vDivider;

    public FriendsViewHolder(@NonNull View itemView, Activity activity, User loggedUser, UsersAdapter usersAdapter) {
        super(itemView);
        this.loggedUser = loggedUser;

        setupView();
        setupListeners();

        this.activity = activity;
        this.usersAdapter = usersAdapter;
    }

    private void setupListeners() {
        bRemoveFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForConfirmation();
            }
        });
    }

    private void removeFriend() {
        FriendDAO friendDAO = new SwaggerFriendDAO();
        friendDAO.rejectRequest(friend.getId(), loggedUser.getAccessToken(), activity.getApplicationContext(), new ApiResponseCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                usersAdapter.removeFriend(getAdapterPosition());
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }

    private void askForConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.dialog_remove_friend_title));
        builder.setMessage(activity.getString(R.string.dialog_confirmation));
        builder.setPositiveButton(activity.getString(R.string.calendar_accept), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked "Yes"
                removeFriend();
            }
        });
        builder.setNegativeButton(activity.getString(R.string.calendar_deny), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked "No"
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setupView() {
        ivProfilePicture = itemView.findViewById(R.id.friends_ivProfilePicture);
        tvUsername = itemView.findViewById(R.id.friends_tvUserName);
        itemView.setOnClickListener(this);
        vDivider = itemView.findViewById(R.id.friends_vDivider);
        bRemoveFriend = itemView.findViewById(R.id.friends_bRemoveFriend);
    }

    public void bind(User user, boolean checkLast) {
        this.friend = user;
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

    @Override
    public void onClick(View v) {
        gotoFriendProfile();
    }

    private void gotoFriendProfile() {
        Bundle args = new Bundle();

        if (activity.getIntent().getExtras() != null) {
            args = activity.getIntent().getExtras();
        }

        args.putSerializable(User.FRIEND_NAME_TAG, friend);
        changeActivity(args);
    }

    private void changeActivity(Bundle extraArgs) {
        Intent intent = new Intent(activity, FriendProfileActivity.class);
        intent.putExtra(User.LOGGED_USER, loggedUser);

        if (extraArgs != null) {
            intent.putExtras(extraArgs);
        }

        activity.startActivityForResult(intent, 1);
    }
}
