package com.example.giftr.persistence.requester;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.giftr.persistence.ApiResponseCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

public class JsonRequester {

    private static final String TAG = JsonRequester.class.getSimpleName();

    private static RequestQueue requestQueue;

    public static void singleRequest(int method, String requestURL, JSONObject requestBody, Map<String, String> headers, Context context, ApiResponseCallback callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }

        JsonObjectRequest request = new JsonObjectRequest(method, requestURL, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("Resposta", "La resposta és: " + response.toString());
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                        error.printStackTrace();
                        Log.e("Resposta", "Hi ha hagut un error: " + error);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }
        };

        // Add the request to the queue.
        request.setTag(TAG);
        requestQueue.add(request);
    }

    public static void listRequest(int method, String requestURL, JSONArray requestBody, Map<String, String> headers, Context context, ApiResponseCallback.ApiListResponseCallback callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }

        JsonArrayRequest request = new JsonArrayRequest(method, requestURL, requestBody,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.v("Resposta", "La resposta és: " + response.toString());
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                        error.printStackTrace();
                        Log.e("Resposta", "Hi ha hagut un error: " + error);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }
        };

        // Add the request to the queue.
        request.setTag(TAG);
        requestQueue.add(request);
    }

    public static void cancelRequest() {
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
            requestQueue = null; // Reset the request queue after canceling requests
        }
    }
}
