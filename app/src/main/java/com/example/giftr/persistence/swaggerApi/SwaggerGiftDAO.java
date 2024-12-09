package com.example.giftr.persistence.swaggerApi;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.example.giftr.business.entities.Gift;
import com.example.giftr.business.entities.User;
import com.example.giftr.persistence.ApiResponseCallback;
import com.example.giftr.persistence.GiftDAO;
import com.example.giftr.persistence.UserDAO;
import com.example.giftr.persistence.requester.JsonRequester;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwaggerGiftDAO implements GiftDAO {

    private final Context context;
    private final String accessToken;
    private final Map<String, String> accept;
    private final Map<String, String> acceptContent;
    private final static String GIFTS_URL = "https://balandrau.salle.url.edu/i3/socialgift/api/v1/gifts/";
    private final static String[] API_ACCEPT_PARAMS = new String[] {"accept","application/json"};
    private final static String[] API_CONTENT_PARAMS = new String[] {"Content-Type","application/json"};

    public SwaggerGiftDAO(String accessToken, Context context) {
        this.accessToken = accessToken;
        this.context = context;

        accept = new HashMap<>();
        accept.put(API_ACCEPT_PARAMS[0], API_ACCEPT_PARAMS[1]);
        accept.put("Authorization", "Bearer " + accessToken);

        acceptContent = new HashMap<>();
        acceptContent.put(API_ACCEPT_PARAMS[0], API_ACCEPT_PARAMS[1]);
        acceptContent.put(API_CONTENT_PARAMS[0], API_CONTENT_PARAMS[1]);
        acceptContent.put("Authorization", "Bearer " + accessToken);
    }

    @Override
    public void createGift(Gift gift, GiftResponseCallback callback) {
        JsonRequester.singleRequest(Request.Method.POST, GIFTS_URL, gift.toJson(), acceptContent, context, new ApiResponseCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                callback.onSuccess(Gift.fromJsonObject(response));
            }

            @Override
            public void onError(VolleyError error) {
                callback.onError(error);
            }
        });
    }

    @Override
    public void getAllGifts(GiftListResponseCallback callback) {
        JsonRequester.listRequest(Request.Method.GET, GIFTS_URL, null, accept, context, new ApiResponseCallback.ApiListResponseCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                callback.onSuccess(Gift.fromJsonArray(response));
            }

            @Override
            public void onError(VolleyError error) {
                callback.onError(error);
            }
        });
    }

    @Override
    public void getUserByGiftID(int giftId, UserDAO.UserResponseCallback callback) {
        JsonRequester.singleRequest(Request.Method.GET, GIFTS_URL + giftId + "/user", null, accept, context, new ApiResponseCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                callback.onSuccess(User.fromJson(response));
            }

            @Override
            public void onError(VolleyError error) {
                callback.onError(error);
            }
        });
    }

    @Override
    public void getGiftByID(int giftId, GiftResponseCallback callback) {
        JsonRequester.singleRequest(Request.Method.GET, GIFTS_URL + giftId, null, accept, context, new ApiResponseCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                callback.onSuccess(Gift.fromJsonObject(response));
            }

            @Override
            public void onError(VolleyError error) {
                callback.onError(error);
            }
        });
    }

    @Override
    public void editGiftById(Gift gift, GiftResponseCallback callback) {
        JsonRequester.singleRequest(Request.Method.GET, GIFTS_URL + gift.getId(), gift.toJson(), acceptContent, context, new ApiResponseCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                callback.onSuccess(Gift.fromJsonObject(response));
            }

            @Override
            public void onError(VolleyError error) {
                callback.onError(error);
            }
        });
    }

    @Override
    public void deleteGiftById(int giftId, ApiResponseCallback callback) {
        JsonRequester.singleRequest(Request.Method.DELETE, GIFTS_URL + giftId, null, accept, context, new ApiResponseCallback() {
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
    public void bookGiftById(int giftId, ApiResponseCallback callback) {
        JsonRequester.singleRequest(Request.Method.POST, GIFTS_URL + giftId + "/book", null, accept, context, new ApiResponseCallback() {
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
    public void deleteBookGiftById(int giftId, ApiResponseCallback callback) {
        JsonRequester.singleRequest(Request.Method.DELETE, GIFTS_URL + giftId + "/book", null, accept, context, new ApiResponseCallback() {
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
