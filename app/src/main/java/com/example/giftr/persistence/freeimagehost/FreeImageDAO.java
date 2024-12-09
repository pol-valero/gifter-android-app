package com.example.giftr.persistence.freeimagehost;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.example.giftr.persistence.ApiResponseCallback;
import com.example.giftr.persistence.ImageDAO;
import com.example.giftr.persistence.requester.StringRequester;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class FreeImageDAO implements ImageDAO {
    private static final String API_REQUEST_URL = "https://freeimage.host/api/1/upload";
    private final static String[] API_DEV_KEY_PARAMS = new String[] {"key", "X"};    // X = Own developer api key.
    private final static String[] API_ACTION_PARAMS = new String[] {"action", "upload"};
    private final static String[] API_FORMAT_PARAMS = new String[] {"format", "json"};

    @Override
    public void uploadImage(Bitmap imageBitmap, Context context, ImageResponseCallback callback) {

        // Convert Bitmap to Base64 encoded string
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        // Create the request parameters
        Map<String, String> params = new HashMap<>();
        params.put(API_DEV_KEY_PARAMS[0], API_DEV_KEY_PARAMS[1]); // Replace with your API key
        params.put("source", encodedImage);

        StringRequester.singleRequest(Request.Method.POST, API_REQUEST_URL, params, new HashMap<>(), context, new ApiResponseCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONObject image = response.getJSONObject("image");
                    String url = image.getString("url");

                    callback.onSuccess(url);
                } catch (JSONException e) {
                    callback.onError(null);
                }
            }

            @Override
            public void onError(VolleyError error) {
                callback.onError(error);
            }
        });

    }
}
