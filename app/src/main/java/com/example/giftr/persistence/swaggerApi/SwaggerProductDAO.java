package com.example.giftr.persistence.swaggerApi;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.example.giftr.business.entities.Product;
import com.example.giftr.persistence.ApiResponseCallback;
import com.example.giftr.persistence.ProductDAO;
import com.example.giftr.persistence.requester.JsonRequester;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwaggerProductDAO implements ProductDAO {

    private final static String PRODUCT_URL = "https://balandrau.salle.url.edu/i3/mercadoexpress/api/v1/products/";
    private final static String[] API_ACCEPT_PARAMS = new String[] {"accept","application/json"};
    private final static String[] API_CONTENT_PARAMS = new String[] {"Content-Type","application/json"};
    @Override
    public void editProduct(Product product, String accessToken, Context context, ProductDAO.ProductResponseCallback callback) {
        Map<String, String> headers = new HashMap<>();
        headers.put(API_ACCEPT_PARAMS[0], API_ACCEPT_PARAMS[1]);
        headers.put(API_CONTENT_PARAMS[0], API_CONTENT_PARAMS[1]);
        headers.put("Authorization", "Bearer " + accessToken);

        JsonRequester.singleRequest(Request.Method.PUT, PRODUCT_URL + product.getId(), product.toJson(), headers, context, new ApiResponseCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                callback.onSuccess(Product.fromJson(response));
            }

            @Override
            public void onError(VolleyError error) {
                callback.onError(error);
                error.printStackTrace();
            }
        });
    }

    @Override
    public void createProduct(Product product, String accessToken, Context context, ProductDAO.ProductResponseCallback callback) {
        Map<String, String> headers = new HashMap<>();
        headers.put(API_ACCEPT_PARAMS[0], API_ACCEPT_PARAMS[1]);
        headers.put(API_CONTENT_PARAMS[0], API_CONTENT_PARAMS[1]);
        headers.put("Authorization", "Bearer " + accessToken);

        JsonRequester.singleRequest(Request.Method.POST, PRODUCT_URL, product.toJson(), headers, context, new ApiResponseCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                callback.onSuccess(Product.fromJson(response));
            }

            @Override
            public void onError(VolleyError error) {
                callback.onError(error);
                error.printStackTrace();
            }
        });
    }


    @Override
    public void getAllProducts(String accessToken, Context context, ProductDAO.ProductListResponseCallback callback) {
        Map<String, String> headers = new HashMap<>();
        headers.put(API_ACCEPT_PARAMS[0], API_ACCEPT_PARAMS[1]);
        headers.put("Authorization", "Bearer " + accessToken);

        JsonRequester.listRequest(Request.Method.GET, PRODUCT_URL, null, headers, context, new ApiResponseCallback.ApiListResponseCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                List<Product> products = new ArrayList<>();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject product = response.getJSONObject(i);
                        products.add(Product.fromJson(product));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

                callback.onSuccess(products);
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }

    @Override
    public void getProductById(int productId, String accessToken, Context context, ProductResponseCallback callback) {
        Map<String, String> headers = new HashMap<>();
        headers.put(API_ACCEPT_PARAMS[0], API_ACCEPT_PARAMS[1]);
        headers.put("Authorization", "Bearer " + accessToken);

        JsonRequester.singleRequest(Request.Method.GET, PRODUCT_URL + productId, null, headers, context, new ApiResponseCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                callback.onSuccess(Product.fromJson(response));
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }


}
