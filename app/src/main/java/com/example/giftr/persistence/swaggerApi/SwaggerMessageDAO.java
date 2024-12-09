package com.example.giftr.persistence.swaggerApi;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.example.giftr.business.entities.Message;
import com.example.giftr.business.entities.User;
import com.example.giftr.persistence.ApiResponseCallback;
import com.example.giftr.persistence.MessageDAO;
import com.example.giftr.persistence.UserDAO;
import com.example.giftr.persistence.requester.JsonRequester;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwaggerMessageDAO implements MessageDAO {
    private final static String MESSAGES_URL = "https://balandrau.salle.url.edu/i3/socialgift/api/v1/messages/";
    private final static String MESSAGES_HISTORY_URL = "https://balandrau.salle.url.edu/i3/socialgift/api/v1/messages/users/";
    private final static String[] API_ACCEPT_PARAMS = new String[] {"accept","application/json"};
    private final static String[] API_CONTENT_PARAMS = new String[] {"Content-Type","application/json"};

    @Override
    public void sendMessage(Message message, String accessToken, Context context, ApiResponseCallback callback) {
        Map<String, String> headers = new HashMap<>();
        headers.put(API_ACCEPT_PARAMS[0], API_ACCEPT_PARAMS[1]);
        headers.put(API_CONTENT_PARAMS[0], API_CONTENT_PARAMS[1]);
        headers.put("Authorization", "Bearer " + accessToken);

        JSONObject requestBody = new JSONObject();
        try {
            requestBody = message.toJson();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonRequester.singleRequest(Request.Method.POST, MESSAGES_URL, requestBody, headers, context, new ApiResponseCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                callback.onSuccess(response);
            }

            @Override
            public void onError(VolleyError error) {
                callback.onError(error);
            }
        });
    }

    @Override
    public void getUsersMessaging(String accessToken, Context context, UserDAO.UserListResponseCallback callback) {
        Map<String, String> headers = new HashMap<>();
        headers.put(API_ACCEPT_PARAMS[0], API_ACCEPT_PARAMS[1]);
        headers.put("Authorization", "Bearer " + accessToken);

        JsonRequester.listRequest(Request.Method.GET, MESSAGES_HISTORY_URL, null, headers, context, new ApiResponseCallback.ApiListResponseCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                List<User> users = new ArrayList<>();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject user = response.getJSONObject(i);
                        users.add(User.fromJson(user));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }


                callback.onSuccess(users);
            }

            @Override
            public void onError(VolleyError error) {
                callback.onError(error);
            }
        });
    }

    @Override
    public void getMessagesWithUser(int userID, String accessToken, Context context, MessageResponseCallback callback) {
        Map<String, String> headers = new HashMap<>();
        headers.put(API_ACCEPT_PARAMS[0], API_ACCEPT_PARAMS[1]);
        headers.put(API_CONTENT_PARAMS[0], API_CONTENT_PARAMS[1]);
        headers.put("Authorization", "Bearer " + accessToken);

        JsonRequester.listRequest(Request.Method.GET, MESSAGES_URL + userID, null, headers, context, new ApiResponseCallback.ApiListResponseCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                List<Message> messages = new ArrayList<>();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject message = response.getJSONObject(i);
                        messages.add(Message.fromJson(message));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

                callback.onSuccess(messages);
            }

            @Override
            public void onError(VolleyError error) {
                callback.onError(error);
            }
        });
    }
}
