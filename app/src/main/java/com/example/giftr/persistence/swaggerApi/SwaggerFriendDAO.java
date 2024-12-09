package com.example.giftr.persistence.swaggerApi;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.giftr.business.entities.User;
import com.example.giftr.persistence.ApiResponseCallback;
import com.example.giftr.persistence.FriendDAO;
import com.example.giftr.persistence.requester.JsonRequester;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwaggerFriendDAO implements FriendDAO {

    private final static String FRIENDS_URL = "https://balandrau.salle.url.edu/i3/socialgift/api/v1/friends/";
    private final static String FRIENDS_REQUESTS_URL = "https://balandrau.salle.url.edu/i3/socialgift/api/v1/friends/requests";
    private final static String[] API_ACCEPT_PARAMS = new String[] {"accept","application/json"};

    @Override
    public void getAllFriends(String accessToken, Context context, FriendResponseCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, FRIENDS_URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("resposta", "La resposta es: " + response.toString());
                        List<User> requests = new ArrayList<>();

                        // Check all the list of JSONObjectsArray and look for the one with the same email (unique field).
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject userObject = response.getJSONObject(i);
                                requests.add(User.fromJson(userObject));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        callback.onSuccess(requests);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("resposta", "Hi ha hagut un error:" + error);
                        callback.onError(error);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();

                // Auth header including the access token of the user logged in.
                headers.put(API_ACCEPT_PARAMS[0], API_ACCEPT_PARAMS[1]);
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };

        queue.add(jsonArrayRequest);
    }

    @Override
    public void getAllRequests(String accessToken, Context context, FriendResponseCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, FRIENDS_REQUESTS_URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("resposta", "La resposta es: " + response.toString());
                        List<User> requests = new ArrayList<>();

                        // Check all the list of JSONObjectsArray and look for the one with the same email (unique field).
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject userObject = response.getJSONObject(i);
                                requests.add(User.fromJson(userObject));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        callback.onSuccess(requests);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("resposta", "Hi ha hagut un error:" + error);
                        callback.onError(error);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();

                // Auth header including the access token of the user logged in.
                headers.put(API_ACCEPT_PARAMS[0], API_ACCEPT_PARAMS[1]);
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };

        queue.add(jsonArrayRequest);
    }

    @Override
    public void sendRequest(int requestUserID, String accessToken, Context context, ApiResponseCallback callback) {
        JSONObject requestBody = new JSONObject();

        Map<String, String> headers = new HashMap<>();
        headers.put(API_ACCEPT_PARAMS[0], API_ACCEPT_PARAMS[1]);
        headers.put("Authorization", "Bearer " + accessToken);

        JsonRequester.singleRequest(Request.Method.POST, FRIENDS_URL + requestUserID, requestBody, headers, context, new ApiResponseCallback() {
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
    public void acceptRequest(int requestUserID, String accessToken, Context context, ApiResponseCallback callback) {
        JSONObject requestBody = new JSONObject();

        Map<String, String> headers = new HashMap<>();
        headers.put(API_ACCEPT_PARAMS[0], API_ACCEPT_PARAMS[1]);
        headers.put("Authorization", "Bearer " + accessToken);

        JsonRequester.singleRequest(Request.Method.PUT, FRIENDS_URL + requestUserID, requestBody, headers, context, new ApiResponseCallback() {
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
    public void rejectRequest(int requestUserID, String accessToken, Context context, ApiResponseCallback callback) {
        Map<String, String> headers = new HashMap<>();
        headers.put(API_ACCEPT_PARAMS[0], API_ACCEPT_PARAMS[1]);
        headers.put("Authorization", "Bearer " + accessToken);

        JsonRequester.singleRequest(Request.Method.DELETE, FRIENDS_URL + requestUserID, null, headers, context, new ApiResponseCallback() {
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

}
