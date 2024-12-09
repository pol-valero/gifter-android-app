package com.example.giftr.persistence;

import android.content.Context;

import com.android.volley.VolleyError;
import com.example.giftr.business.entities.Message;

import java.util.List;

public interface MessageDAO {
    void sendMessage(Message message, String accessToken, Context context, ApiResponseCallback callback);
    void getUsersMessaging(String accessToken, Context context, UserDAO.UserListResponseCallback callback);
    void getMessagesWithUser(int userID, String accessToken, Context context, MessageResponseCallback callback);

    interface MessageResponseCallback {
        void onSuccess(List<Message> response);
        void onError(VolleyError error);
    }
}
