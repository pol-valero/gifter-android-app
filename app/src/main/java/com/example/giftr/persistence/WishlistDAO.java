package com.example.giftr.persistence;

import android.content.Context;
import com.android.volley.VolleyError;
import com.example.giftr.business.entities.Wishlist;

import java.util.List;

public interface WishlistDAO {
	void createWishlist(Wishlist wishlist, String accessToken, Context context, WishlistResponseCallback callback);
	void getWishlistsFromUser(int userID, String accessToken, Context context, WishlistListResponseCallback callback);
	void getAllWishlists(String accessToken, Context context, WishlistListResponseCallback callback);
	void getWishlistById(int wishlistID, String accessToken, Context context, WishlistResponseCallback callback);
	void editWishlistByID(Wishlist wishlist, String accessToken, Context context, WishlistResponseCallback callback);
	void removeWishlistByID(int wishlistID, String accessToken, Context context, ApiResponseCallback callback);

	interface WishlistResponseCallback {
		void onSuccess(Wishlist response);
		void onError(VolleyError error);
	}

	interface WishlistListResponseCallback {
		void onSuccess(List<Wishlist> response);
		void onError(VolleyError error);
	}
}
