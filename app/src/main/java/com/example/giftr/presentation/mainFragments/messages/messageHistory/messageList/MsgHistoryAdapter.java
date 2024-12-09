package com.example.giftr.presentation.mainFragments.messages.messageHistory.messageList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.giftr.R;
import com.example.giftr.business.SharedUser;
import com.example.giftr.business.entities.Message;

import java.util.List;

public class MsgHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int SENDER_MESSAGE_TYPE = 1;
    private static final int RECEIVER_MESSAGE_TYPE = 2;
    private List<Message> messageHistory;
    private Activity activity;
    private SharedUser sharedUser;

    public MsgHistoryAdapter(List<Message> messageHistory, Activity activity) {
        this.messageHistory = messageHistory;
        this.activity = activity;
        this.sharedUser = SharedUser.getInstance(activity.getApplicationContext());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView;

        if (viewType == SENDER_MESSAGE_TYPE) {
            itemView = inflater.inflate(R.layout.list_item_message_sender, parent, false);
            return new MsgHistoryViewHolderSender(itemView, activity);
        } else {
            itemView = inflater.inflate(R.layout.list_item_message_receiver, parent, false);
            return new MsgHistoryViewHolderReceiver(itemView, activity);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        if (viewType == SENDER_MESSAGE_TYPE) {
            // Bind data for ViewHolderType1
            MsgHistoryViewHolderSender viewHolder1 = (MsgHistoryViewHolderSender) holder;
            viewHolder1.bind(messageHistory.get(position));
        } else if (viewType == RECEIVER_MESSAGE_TYPE) {
            // Bind data for ViewHolderType2
            MsgHistoryViewHolderReceiver viewHolder2 = (MsgHistoryViewHolderReceiver) holder;
            viewHolder2.bind(messageHistory.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return messageHistory.size();
    }

    @Override
    public int getItemViewType(int position) {
        // Return the view type based on the position or other conditions
        if (messageHistory.get(position).sentBy(sharedUser.getLoggedUser().getId())) {
            return SENDER_MESSAGE_TYPE;
        } else {
            return RECEIVER_MESSAGE_TYPE;
        }
    }

    public void updateMessages(List<Message> response) {
        this.messageHistory = response;
        notifyDataSetChanged();
    }
}
