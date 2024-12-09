package com.example.giftr.persistence.requester;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.giftr.persistence.ApiResponseCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class StringRequester {
    public static void singleRequest(int method, String requestURL, Map<String, String> params, Map<String, String> headers, Context context, ApiResponseCallback callback) {
        // Instantiate the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        // Create the POST request
        StringRequest stringRequest = new StringRequest(method, requestURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Success callback with the uploaded image URL
                        try {
                            callback.onSuccess(new JSONObject(response));
                        } catch (JSONException e) {
                            callback.onError(null);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Error callback with error message
                callback.onError(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };

        // Add the request to the RequestQueue
        requestQueue.add(stringRequest);
    }
}
