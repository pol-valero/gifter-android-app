package com.example.giftr.persistence.swaggerApi;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.example.giftr.business.entities.Category;
import com.example.giftr.persistence.ApiResponseCallback;
import com.example.giftr.persistence.CategoryDAO;
import com.example.giftr.persistence.requester.JsonRequester;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SwaggerCategoryDAO implements CategoryDAO {

    private final Context context;
    private final String accessToken;
    private final Map<String, String> accept;
    private final Map<String, String> acceptContent;
    private final static String CATEGORIES_URL = "https://balandrau.salle.url.edu/i3/mercadoexpress/api/v1/categories/";
    private final static String[] API_ACCEPT_PARAMS = new String[] {"accept","application/json"};
    private final static String[] API_CONTENT_PARAMS = new String[] {"Content-Type","application/json"};

    public SwaggerCategoryDAO(String accessToken, Context context) {
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
    public void getAllCategories(CategoryListResponseCallback callback) {
        JsonRequester.listRequest(Request.Method.GET, CATEGORIES_URL, null, accept, context, new ApiResponseCallback.ApiListResponseCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                callback.onSuccess(Category.fromJsonArray(response));
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }

    @Override
    public void getCategoryById(int categoryId, CategoryResponseCallback callback) {
        JsonRequester.singleRequest(Request.Method.GET, CATEGORIES_URL + categoryId, null, accept, context, new ApiResponseCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                callback.onSuccess(Category.fromJsonObject(response));
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }
}
