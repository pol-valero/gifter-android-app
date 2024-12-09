package com.example.giftr.persistence;

import android.content.Context;

import com.android.volley.VolleyError;
import com.example.giftr.business.entities.User;

import java.util.List;

public interface FriendDAO {
    void getAllFriends(String accessToken, Context context, FriendResponseCallback friendResponseCallback);
    void getAllRequests(String accessToken, Context context, FriendResponseCallback friendResponseCallback);
    void sendRequest(int requestUserID, String accessToken, Context context, ApiResponseCallback apiResponseCallback);
    void acceptRequest(int requestUserID, String accessToken, Context context, ApiResponseCallback apiResponseCallback);
    void rejectRequest(int requestUserID, String accessToken, Context context, ApiResponseCallback apiResponseCallback);

    interface FriendResponseCallback {
        void onSuccess(List<User> response);
        void onError(VolleyError error);
    }
}
