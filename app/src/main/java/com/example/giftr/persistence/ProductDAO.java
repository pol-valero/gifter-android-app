package com.example.giftr.persistence;

import android.content.Context;

import com.android.volley.VolleyError;
import com.example.giftr.business.entities.Product;
import com.example.giftr.business.entities.Wishlist;

import java.util.List;

public interface ProductDAO {

    void createProduct(Product product, String accessToken, Context context, ProductDAO.ProductResponseCallback callback);
    void getAllProducts(String accessToken, Context context, ProductDAO.ProductListResponseCallback callback);
    void getProductById(int productId, String accessToken, Context context, ProductResponseCallback callback);

    void editProduct(Product product, String accessToken, Context context, ProductDAO.ProductResponseCallback callback);

    interface ProductResponseCallback {
        void onSuccess(Product response);
        void onError(VolleyError error);
    }

    interface ProductListResponseCallback {
        void onSuccess(List<Product> response);
        void onError(VolleyError error);
    }

}
