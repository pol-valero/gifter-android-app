package com.example.giftr.persistence;

import com.android.volley.VolleyError;

import com.example.giftr.business.entities.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public interface ApiResponseCallback {
    void onSuccess(JSONObject response);
    void onError(VolleyError error);

    interface ApiListResponseCallback {
        void onSuccess(JSONArray response);
        void onError(VolleyError error);
    }
}
