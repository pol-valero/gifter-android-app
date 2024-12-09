package com.example.giftr.presentation.mainFragments.messages.messageHistory.messageList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.giftr.R;
import com.example.giftr.business.entities.Message;

public class MsgHistoryViewHolderReceiver extends RecyclerView.ViewHolder {

    private Activity activity;
    private Message message;
    private TextView tvContent;
    private TextView tvTime;

    public MsgHistoryViewHolderReceiver(@NonNull View itemView, Activity activity) {
        super(itemView);
        setupView();

        this.activity = activity;
    }

    private void setupView() {
        tvContent = itemView.findViewById(R.id.msgHistory_tvContent);
        tvTime = itemView.findViewById(R.id.msgHistory_tvTime);
    }

    @SuppressLint("SetTextI18n")
    public void bind(Message message) {
        this.message = message;

        tvContent.setText(message.getContent());
        tvTime.setText(message.getSentTime(false) + activity.getString(R.string.time_comma) + " " + message.getHourSent());
    }
}
