package com.example.giftr.persistence;

import android.content.Context;

import com.android.volley.VolleyError;
import com.example.giftr.business.entities.User;
import com.example.giftr.presentation.LoginActivity;

import org.json.JSONObject;

import java.util.List;

public interface UserDAO {
    void createUser(User newUser, Context context, UserResponseCallback callback);
    void editUser(User user, String accessToken, Context context, UserResponseCallback callback);
    void loginUser(String email, String password, Context context, ApiResponseCallback callback);
    void getUserByID(int userID, String accessToken, Context context, UserResponseCallback callback);
    void getUserByEmail(String userEmail, String accessToken, Context context, UserResponseCallback callback);
    void getAllUsers(String accessToken, Context context, UserListResponseCallback callback);
    void getUserFriendsById(int userID, String accessToken, Context context, UserListResponseCallback callback);
    void getUserReservesById(int userID, String accessToken, Context context, GiftDAO.GiftListResponseCallback callback);
    void searchUser(String containedWord, String accessToken, Context context, UserListResponseCallback callback);

    interface UserResponseCallback {
        void onSuccess(User response);
        void onError(VolleyError error);
    }

    interface UserListResponseCallback {
        void onSuccess(List<User> response);
        void onError(VolleyError error);
    }
}
