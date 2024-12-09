package com.example.giftr.persistence;

import com.android.volley.VolleyError;
import com.example.giftr.business.entities.Gift;

import java.util.List;

public interface GiftDAO {
    void createGift(Gift gift, GiftResponseCallback callback);
    void getAllGifts(GiftListResponseCallback callback);
    void getUserByGiftID(int giftId, UserDAO.UserResponseCallback callback);
    void getGiftByID(int giftId, GiftResponseCallback callback);
    void editGiftById(Gift gift, GiftResponseCallback callback);
    void deleteGiftById(int giftId, ApiResponseCallback callback);
    void bookGiftById(int giftId, ApiResponseCallback callback);
    void deleteBookGiftById(int giftId, ApiResponseCallback callback);

    interface GiftResponseCallback {
        void onSuccess(Gift response);
        void onError(VolleyError error);
    }

    interface GiftListResponseCallback {
        void onSuccess(List<Gift> response);
        void onError(VolleyError error);
    }
}
