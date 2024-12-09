package com.example.giftr.presentation.mainFragments.search.friendrequests.recyclerview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.example.giftr.R;
import com.example.giftr.business.entities.User;
import com.example.giftr.persistence.ApiResponseCallback;
import com.example.giftr.persistence.FriendDAO;
import com.example.giftr.persistence.swaggerApi.SwaggerFriendDAO;
import com.example.giftr.presentation.mainFragments.profile.NotFriendProfileActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

public class RequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ImageView ivProfilePicture;
    private TextView tvName;
    private Button bAcceptRequest;
    private ImageButton bRejectRequest;
    private Activity activity;
    private RequestAdapter requestAdapter;
    private User friend;

    public RequestViewHolder(LayoutInflater inflater, ViewGroup parent, Activity activity, RequestAdapter adapter) {
        super(inflater.inflate(R.layout.list_item_request, parent, false));

        setupView();
        setupListeners();

        // Ens guardem l'activitat i l'adapter por poder actualitzar l'ítem per posició.
        this.activity = activity;
        this.requestAdapter = adapter;
    }

    private void setupView() {
        ivProfilePicture = itemView.findViewById(R.id.request_ivProfilePicture);
        tvName = itemView.findViewById(R.id.request_tvName);
        bAcceptRequest = itemView.findViewById(R.id.request_bAcceptRequest);
        bRejectRequest = itemView.findViewById(R.id.request_bRejectRequest);
    }

    private void setupListeners() {
        itemView.setOnClickListener(this);

        bAcceptRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptRequest();
            }
        });

        bRejectRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForConfirmation();
            }
        });
    }

    private void askForConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.dialog_remove_friend_request_title));
        builder.setMessage(activity.getString(R.string.dialog_confirmation));
        builder.setPositiveButton(activity.getString(R.string.calendar_accept), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked "Yes"
                rejectRequest();
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

    private void rejectRequest() {
        FriendDAO friendDAO = new SwaggerFriendDAO();
        friendDAO.rejectRequest(friend.getId(), requestAdapter.user.getAccessToken(), requestAdapter.context, new ApiResponseCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                disappearFriendRequest(false);
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }

    private void acceptRequest() {
        FriendDAO friendDAO = new SwaggerFriendDAO();
        friendDAO.acceptRequest(friend.getId(), requestAdapter.user.getAccessToken(), requestAdapter.context, new ApiResponseCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                disappearFriendRequest(true);
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }

    // This method removes the request from the list and notifies the adapter to make it visually changed.
    private void disappearFriendRequest(boolean accepted) {
        requestAdapter.removeRequest(getAdapterPosition());
        requestAdapter.notifyItemChanged(getAdapterPosition());
    }

    // Method that shows the current item on the recycler view and updates it.
    public void bind(User user) {
        this.friend = user;

        tvName.setText(user.getFullName());
        Picasso.get().load(user.getImagePath()).into(ivProfilePicture);
        ivProfilePicture.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        Bundle args = new Bundle();
        args.putSerializable(User.FRIEND_NAME_TAG, friend);
        Intent intent = new Intent(activity, NotFriendProfileActivity.class);
        intent.putExtras(args);
        activity.startActivity(intent);
    }
}
