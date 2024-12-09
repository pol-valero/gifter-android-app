package com.example.giftr.business.entities;

import android.content.Context;

import com.android.volley.VolleyError;
import com.example.giftr.persistence.ProductDAO;
import com.example.giftr.persistence.swaggerApi.SwaggerProductDAO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Gift implements Serializable {
	private int id;
	private int wishlist_id;
	private String product_url;
	private int productId;
	private int priority;
	private boolean booked;

	public Gift(int id, int wishlist_id, String product_url, int priority, boolean booked) {
		this.id = id;
		this.wishlist_id = wishlist_id;
		this.product_url = product_url;
		this.priority = priority;
		this.booked = booked;

		String product_id = product_url.split("/")[product_url.split("/").length - 1];

		try {
			productId = Integer.parseInt(product_id);
		} catch (NumberFormatException e) {
			// Handle the case when the product_id is not a valid integer
			productId = 0;
		}
	}

	public Gift(int wishlist_id, String product_url, int priority) {
		this.wishlist_id = wishlist_id;
		this.product_url = product_url;
		this.priority = priority;
		this.booked = false;

		String product_id = product_url.split("/")[product_url.split("/").length - 1];

		try {
			productId = Integer.parseInt(product_id);
		} catch (NumberFormatException e) {
			// Handle the case when the product_id is not a valid integer
			productId = 0;
		}
	}

	public int getId() {
		return id;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getWishlist_id() {
		return wishlist_id;
	}

	public int getPriority() {
		return priority;
	}

	public int getProductId() {
		return productId;
	}

	public String getProduct_url() {
		return product_url;
	}

	public boolean isBooked() {
		return booked;
	}

	public void setBooked(boolean booked) {
		this.booked = booked;
	}

	public void getProduct(String accessToken, Context context, ProductDAO.ProductResponseCallback callback) {
		ProductDAO productDAO = new SwaggerProductDAO();
		productDAO.getProductById(productId, accessToken, context, new ProductDAO.ProductResponseCallback() {
			@Override
			public void onSuccess(Product response) {
				callback.onSuccess(response);
			}

			@Override
			public void onError(VolleyError error) {
				callback.onError(error);
			}
		});
	}

	public static List<Gift> fromJsonArray(JSONArray jsonArray) {
		List<Gift> gifts = new ArrayList<>();

		for (int i = 0; i < jsonArray.length(); i++) {

			try {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				Gift gift = fromJsonObject(jsonObject);
				gifts.add(gift);
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}

		return gifts;
	}

	public JSONObject toJson() {
		JSONObject gift = new JSONObject();
		try {
			gift.put("wishlist_id", wishlist_id);
			gift.put("product_url", product_url);
			gift.put("priority", priority);
			return gift;
		} catch (JSONException e) {
			return null;
		}
	}

	public JSONObject fromWishlistToJson() {
		JSONObject gift = new JSONObject();
		try {
			gift.put("id", id);
			gift.put("wishlist_id", wishlist_id);
			gift.put("product_url", product_url);
			gift.put("priority", priority);
			gift.put("booked", booked);
			return gift;
		} catch (JSONException e) {
			return null;
		}
	}

	public static Gift fromJsonObject(JSONObject giftObject) {
		try {
			int id = giftObject.getInt("id");
			int wishlist = giftObject.getInt("wishlist_id");
			String product_url = giftObject.getString("product_url");

			int priority = 0; // Default value if "priority" is not present or is null

			if (!giftObject.isNull("priority")) {
				priority = giftObject.getInt("priority");
			}

			boolean booked = false;

			if (giftObject.has("booked")) {
				Object bookedValue = giftObject.get("booked");
				if (bookedValue instanceof Integer) {
					int bookedInt = (int) bookedValue;
					booked = (bookedInt != 0);
				} else if (bookedValue instanceof Boolean) {
					booked = (boolean) bookedValue;
				}
			}
			return new Gift(id, wishlist, product_url, priority, booked);

		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}
}
