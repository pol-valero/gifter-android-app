package com.example.giftr.business;

import com.example.giftr.business.entities.Message;
import com.example.giftr.business.entities.User;

public class FriendMessagePair {
    private User friend;
    private Message message;

    public FriendMessagePair(User friend, Message message) {
        this.friend = friend;
        this.message = message;
    }

    public User getFriend() {
        return friend;
    }

    public Message getMessage() {
        return message;
    }
}

