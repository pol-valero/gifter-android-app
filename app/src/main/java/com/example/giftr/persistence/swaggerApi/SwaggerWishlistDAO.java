package com.example.giftr.persistence.swaggerApi;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.example.giftr.business.entities.Wishlist;
import com.example.giftr.persistence.ApiResponseCallback;
import com.example.giftr.persistence.WishlistDAO;
import com.example.giftr.persistence.requester.JsonRequester;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwaggerWishlistDAO implements WishlistDAO {

	private final static String WISHLIST_URL = "https://balandrau.salle.url.edu/i3/socialgift/api/v1/wishlists/";
	private final static String user_WISHLIST_URL = "https://balandrau.salle.url.edu/i3/socialgift/api/v1/users/";
	private final static String[] API_ACCEPT_PARAMS = new String[] {"accept","application/json"};
	private final static String[] API_CONTENT_PARAMS = new String[] {"Content-Type","application/json"};

	// TODO: Posar accessToken i Context com a private final (extra).
	@Override
	public void createWishlist(Wishlist wishlist, String accessToken, Context context, WishlistResponseCallback callback) {
		JSONObject requestBody = new JSONObject();
		try {
			requestBody.put("name", wishlist.getName());
			requestBody.put("description", wishlist.getDescription());
			requestBody.put("end_date", wishlist.getEndDate());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		Map<String, String> headers = new HashMap<>();
		headers.put(API_ACCEPT_PARAMS[0], API_ACCEPT_PARAMS[1]);
		headers.put(API_CONTENT_PARAMS[0], API_CONTENT_PARAMS[1]);
		headers.put("Authorization", "Bearer " + accessToken);
		System.out.println("Token  " + accessToken);

		JsonRequester.singleRequest(Request.Method.POST, WISHLIST_URL, requestBody, headers, context, new ApiResponseCallback() {
			@Override
			public void onSuccess(JSONObject response) {
				callback.onSuccess(Wishlist.fromResponseJson(response));
			}

			@Override
			public void onError(VolleyError error) {
				callback.onError(error);
				error.printStackTrace();
			}
		});
	}

	@Override
	public void getWishlistsFromUser(int userID, String accessToken, Context context, WishlistListResponseCallback callback) {
		Map<String, String> headers = new HashMap<>();
		headers.put(API_ACCEPT_PARAMS[0], API_ACCEPT_PARAMS[1]);
		headers.put("Authorization", "Bearer " + accessToken);

		JsonRequester.listRequest(Request.Method.GET, user_WISHLIST_URL + userID + "/wishlists", null, headers, context, new ApiResponseCallback.ApiListResponseCallback() {
			@Override
			public void onSuccess(JSONArray response) {
				List<Wishlist> wishlists = new ArrayList<>();

				for (int i = 0; i < response.length(); i++) {
					try {
						JSONObject wishlist = response.getJSONObject(i);
						wishlists.add(Wishlist.fromJson(wishlist));
					} catch (JSONException e) {
						throw new RuntimeException(e);
					}
				}

				callback.onSuccess(wishlists);
			}

			@Override
			public void onError(VolleyError error) {

			}
		});
	}

	@Override
	public void getAllWishlists(String accessToken, Context context, WishlistListResponseCallback callback) {
		Map<String, String> headers = new HashMap<>();
		headers.put(API_ACCEPT_PARAMS[0], API_ACCEPT_PARAMS[1]);
		headers.put("Authorization", "Bearer " + accessToken);

		JsonRequester.listRequest(Request.Method.GET, WISHLIST_URL, null, headers, context, new ApiResponseCallback.ApiListResponseCallback() {
			@Override
			public void onSuccess(JSONArray response) {
				List<Wishlist> wishlists = new ArrayList<>();

				for (int i = 0; i < response.length(); i++) {
					try {
						JSONObject wishlist = response.getJSONObject(i);
						wishlists.add(Wishlist.fromJson(wishlist));
					} catch (JSONException e) {
						throw new RuntimeException(e);
					}
				}

				callback.onSuccess(wishlists);
			}

			@Override
			public void onError(VolleyError error) {

			}
		});
	}

	@Override
	public void getWishlistById(int wishlistID, String accessToken, Context context, WishlistResponseCallback callback) {
		Map<String, String> headers = new HashMap<>();
		headers.put(API_ACCEPT_PARAMS[0], API_ACCEPT_PARAMS[1]);
		headers.put("Authorization", "Bearer " + accessToken);

		JsonRequester.singleRequest(Request.Method.GET, WISHLIST_URL + wishlistID, null, headers, context, new ApiResponseCallback() {
			@Override
			public void onSuccess(JSONObject response) {
				callback.onSuccess(Wishlist.fromResponseJson(response));
			}

			@Override
			public void onError(VolleyError error) {

			}
		});
	}

	@Override
	public void editWishlistByID(Wishlist wishlist, String accessToken, Context context, WishlistResponseCallback callback) {
		JSONObject requestBody = new JSONObject();

		try {
			requestBody = wishlist.toJson();
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}

		Map<String, String> headers = new HashMap<>();
		headers.put(API_ACCEPT_PARAMS[0], API_ACCEPT_PARAMS[1]);
		headers.put(API_CONTENT_PARAMS[0], API_CONTENT_PARAMS[1]);
		headers.put("Authorization", "Bearer " + accessToken);

		JsonRequester.singleRequest(Request.Method.PUT, WISHLIST_URL + wishlist.getID(), requestBody, headers, context, new ApiResponseCallback() {
			@Override
			public void onSuccess(JSONObject response) {
				callback.onSuccess(Wishlist.fromResponseJson(response));
			}

			@Override
			public void onError(VolleyError error) {

			}
		});
	}

	@Override
	public void removeWishlistByID(int wishlistID, String accessToken, Context context, ApiResponseCallback callback) {
		Map<String, String> headers = new HashMap<>();
		headers.put(API_ACCEPT_PARAMS[0], API_ACCEPT_PARAMS[1]);
		headers.put("Authorization", "Bearer " + accessToken);

		JsonRequester.singleRequest(Request.Method.DELETE, WISHLIST_URL + wishlistID, null, headers, context, new ApiResponseCallback() {
			@Override
			public void onSuccess(JSONObject response) {
				callback.onSuccess(response);
			}

			@Override
			public void onError(VolleyError error) {

			}
		});
	}
}
