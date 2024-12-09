package com.example.giftr.presentation.mainFragments.messages;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.giftr.R;
import com.example.giftr.business.ImageViewLoader;
import com.example.giftr.business.entities.Message;
import com.example.giftr.business.entities.User;
import com.example.giftr.presentation.MainMenuActivity;
import com.example.giftr.presentation.mainFragments.messages.messageHistory.MessageHistoryActivity;

import java.util.Objects;

public class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Activity activity;
    private MessageAdapter messageAdapter;
    private ImageView ivProfilePicture;
    private TextView tvUserName;
    private TextView tvLastMessage;
    private TextView tvTimeStamp;
    private User friend;
    private View vDivider;

    public MessageViewHolder(LayoutInflater inflater, ViewGroup parent, Activity activity, MessageAdapter adapter) {
        super(inflater.inflate(R.layout.list_item_recent_messages, parent, false));

        setupView();

        // Ens guardem l'activitat i l'adapter per poder actualitzar l'ítem per posició.
        this.activity = activity;
        this.messageAdapter = adapter;
    }

    private void setupView() {
        ivProfilePicture = itemView.findViewById(R.id.messages_ivProfilePicture);
        tvUserName = itemView.findViewById(R.id.messages_tvUserName);
        tvLastMessage = itemView.findViewById(R.id.messages_tvLastMessage);
        vDivider = itemView.findViewById(R.id.messages_vDivider);
        tvTimeStamp = itemView.findViewById(R.id.messages_tvTime);
        itemView.setOnClickListener(this);
    }

    public void bind(User friend, Message message, boolean checkLast) {
        this.friend = friend;

        tvLastMessage.setText(message.getContent());
        tvUserName.setText(friend.getFullName());
        tvTimeStamp.setText(message.getSentTime(true));

        if (checkLast) {
            vDivider.setVisibility(View.GONE);
        }
        else {
            vDivider.setVisibility(View.VISIBLE);
        }

        ImageViewLoader imageViewLoader = new ImageViewLoader(ivProfilePicture);
        imageViewLoader.loadImage(friend.getImagePath());
    }

    @Override
    public void onClick(View v) {
        replaceFragment();
    }

    private void replaceFragment() {
        Bundle args;

        if (activity.getIntent().getExtras() != null) {
            args = activity.getIntent().getExtras();
        } else {
            args = new Bundle();
        }

        args.putSerializable(User.FRIEND_NAME_TAG, friend);

        // Get a reference to the MainMenuActivity containing this fragment
        MainMenuActivity mainMenuActivity = (MainMenuActivity) activity;

        // Call the replaceFragment method on the MainMenuActivity
        Objects.requireNonNull(mainMenuActivity).changeActivity(
                MessageHistoryActivity.class, args);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
