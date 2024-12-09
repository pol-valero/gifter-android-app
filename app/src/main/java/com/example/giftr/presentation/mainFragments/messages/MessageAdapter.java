package com.example.giftr.presentation.mainFragments.messages;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.giftr.business.FriendMessagePair;
import com.example.giftr.business.entities.Message;
import com.example.giftr.business.entities.User;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    private List<User> users;
    private List<Message> messages;
    private final Activity activity;

    public MessageAdapter(List<FriendMessagePair> friendMessagePair, Activity activity) {
        this.users = new ArrayList<>();
        this.messages = new ArrayList<>();

        for (int i = 0; i < friendMessagePair.size(); i++) {
            User friend = friendMessagePair.get(i).getFriend();
            Message message = friendMessagePair.get(i).getMessage();
            users.add(friend);
            messages.add(message);
        }

        this.activity = activity;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new MessageViewHolder(layoutInflater, parent, activity, this);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.bind(users.get(position), messages.get(position), position == users.size() - 1);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
