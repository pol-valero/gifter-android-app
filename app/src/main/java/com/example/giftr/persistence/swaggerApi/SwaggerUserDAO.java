package com.example.giftr.persistence.swaggerApi;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.giftr.business.entities.Gift;
import com.example.giftr.business.entities.User;
import com.example.giftr.persistence.ApiResponseCallback;
import com.example.giftr.persistence.GiftDAO;
import com.example.giftr.persistence.requester.JsonRequester;
import com.example.giftr.persistence.UserDAO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwaggerUserDAO implements UserDAO {

    private final static String API_USERS_REQUEST_URL = "https://balandrau.salle.url.edu/i3/socialgift/api/v1/users/";
    private final static String API_LOGIN_USERS_REQUEST_URL = "https://balandrau.salle.url.edu/i3/socialgift/api/v1/users/login";
    private final static String API_SEARCH_USERS_REQUEST_URL = "https://balandrau.salle.url.edu/i3/socialgift/api/v1/users/search?s=";
    private final static String[] API_ACCEPT_PARAMS = new String[] {"accept","application/json"};
    private final static String[] API_CONTENT_PARAMS = new String[] {"Content-Type","application/json"};

    @Override
    public void createUser(User newUser, Context context, UserResponseCallback callback) {

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("name", newUser.getName());
            requestBody.put("last_name", newUser.getLastName());
            requestBody.put("email", newUser.getEmail());
            requestBody.put("password", newUser.getPassword());
            requestBody.put("image", newUser.getImagePath());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, String> headers = new HashMap<>();
        headers.put(API_ACCEPT_PARAMS[0], API_ACCEPT_PARAMS[1]);
        headers.put(API_CONTENT_PARAMS[0], API_CONTENT_PARAMS[1]);

        JsonRequester.singleRequest(Request.Method.POST, API_USERS_REQUEST_URL, requestBody, headers, context, new ApiResponseCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                callback.onSuccess(User.fromJson(response));
            }

            @Override
            public void onError(VolleyError error) {
                callback.onError(error);
                error.printStackTrace();
            }
        });
    }


    @Override
    public void loginUser(String email, String password, Context context, ApiResponseCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(context);

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
            requestBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, API_LOGIN_USERS_REQUEST_URL, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("resposta", "La resposta es: " + response.toString());
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                        error.printStackTrace();
                        Log.e("resposta", "Hi ha hagut un error: " + error);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put(API_ACCEPT_PARAMS[0], API_ACCEPT_PARAMS[1]);
                headers.put(API_CONTENT_PARAMS[0], API_CONTENT_PARAMS[1]);
                return headers;
            }
        };

        // Add the request to the queue.
        queue.add(request);
    }

    @Override
    public void editUser(User user, String accessToken, Context context, UserResponseCallback callback) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("name", user.getName());
            requestBody.put("last_name", user.getLastName());
            requestBody.put("email", user.getEmail());
            requestBody.put("password", user.getPassword());
            requestBody.put("image", user.getImagePath());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, String> headers = new HashMap<>();
        headers.put(API_ACCEPT_PARAMS[0], API_ACCEPT_PARAMS[1]);
        headers.put(API_CONTENT_PARAMS[0], API_CONTENT_PARAMS[1]);
        headers.put("Authorization", "Bearer " + accessToken);

        JsonRequester.singleRequest(Request.Method.PUT, API_USERS_REQUEST_URL, requestBody, headers, context, new ApiResponseCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                callback.onSuccess(User.fromJson(response));
            }

            @Override
            public void onError(VolleyError error) {
                callback.onError(error);
                error.printStackTrace();
            }
        });
    }

    @Override
    public void getUserByID(int userID, String accessToken, Context context, UserResponseCallback callback) {
        Map<String, String> headers = new HashMap<>();
        headers.put(API_ACCEPT_PARAMS[0], API_ACCEPT_PARAMS[1]);
        headers.put("Authorization", "Bearer " + accessToken);

        JsonRequester.singleRequest(Request.Method.GET, API_USERS_REQUEST_URL + userID, null, headers, context, new ApiResponseCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.i("resposta", "La resposta es: " + response.toString());
                callback.onSuccess(User.fromJson(response));
            }

            @Override
            public void onError(VolleyError error) {
                Log.e("resposta", "Hi ha hagut un error:" + error);
                callback.onError(error);
            }
        });
    }

    @Override
    public void getUserByEmail(String userEmail, String accessToken, Context context, UserResponseCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, API_SEARCH_USERS_REQUEST_URL + userEmail, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("resposta", "La resposta es: " + response.toString());
                        JSONObject user = null;

                        // Check all the list of JSONObjectsArray and look for the one with the same email (unique field).
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject userObject = response.getJSONObject(i);
                                String email = userObject.getString("email");
                                if (email.equals(userEmail)) {
                                    user = userObject;
                                    break;
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        callback.onSuccess(User.fromJson(user));
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
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };

        queue.add(jsonArrayRequest);
    }

    @Override
    public void getAllUsers(String accessToken, Context context, UserListResponseCallback callback) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(API_ACCEPT_PARAMS[0], API_ACCEPT_PARAMS[1]);
        headers.put("Authorization", "Bearer " + accessToken);

        JsonRequester.listRequest(Request.Method.GET, API_USERS_REQUEST_URL, null, headers, context, new ApiResponseCallback.ApiListResponseCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                List<User> users = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        users.add(User.fromJson(response.getJSONObject(i)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                callback.onSuccess(users);
            }

            @Override
            public void onError(VolleyError error) {
                callback.onError(error);
                error.printStackTrace();
            }
        });
    }

    @Override
    public void getUserFriendsById(int userID, String accessToken, Context context, UserListResponseCallback callback) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(API_ACCEPT_PARAMS[0], API_ACCEPT_PARAMS[1]);
        headers.put("Authorization", "Bearer " + accessToken);

        String url = API_USERS_REQUEST_URL + userID + "/friends";
        JsonRequester.listRequest(Request.Method.GET, url, null, headers, context, new ApiResponseCallback.ApiListResponseCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                List<User> users = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        users.add(User.fromJson(response.getJSONObject(i)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                callback.onSuccess(users);
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }

    @Override
    public void getUserReservesById(int userID, String accessToken, Context context, GiftDAO.GiftListResponseCallback callback) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(API_ACCEPT_PARAMS[0], API_ACCEPT_PARAMS[1]);
        headers.put("Authorization", "Bearer " + accessToken);

        String url = API_USERS_REQUEST_URL + userID + "/gifts/reserved";
        JsonRequester.listRequest(Request.Method.GET, url, null, headers, context, new ApiResponseCallback.ApiListResponseCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                List<Gift> gifts = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        gifts.add(Gift.fromJsonObject(response.getJSONObject(i)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                callback.onSuccess(gifts);
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }

    @Override
    public void searchUser(String containedWord, String accessToken, Context context, UserListResponseCallback callback) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(API_ACCEPT_PARAMS[0], API_ACCEPT_PARAMS[1]);
        headers.put("Authorization", "Bearer " + accessToken);

        JsonRequester.listRequest(Request.Method.GET, API_SEARCH_USERS_REQUEST_URL + containedWord, null, headers, context, new ApiResponseCallback.ApiListResponseCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                List<User> users = new ArrayList<>();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = (JSONObject) response.get(i);
                        users.add(User.fromJson(jsonObject));

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

                callback.onSuccess(users);
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }
}
