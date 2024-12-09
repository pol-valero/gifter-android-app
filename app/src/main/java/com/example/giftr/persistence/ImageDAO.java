package com.example.giftr.persistence;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.volley.VolleyError;
import com.example.giftr.business.entities.User;

public interface ImageDAO {
    void uploadImage(Bitmap imageBitmap, Context context, ImageResponseCallback callback);

    interface ImageResponseCallback {
        void onSuccess(String imageURL);
        void onError(VolleyError error);
    }
}
