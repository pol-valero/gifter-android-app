package com.example.giftr.persistence;

import com.android.volley.VolleyError;
import com.example.giftr.business.entities.Category;

import java.util.List;

public interface CategoryDAO {

    void getAllCategories(CategoryListResponseCallback callback);
    void getCategoryById(int categoryId, CategoryResponseCallback callback);

    interface CategoryResponseCallback {
        void onSuccess(Category response);
        void onError(VolleyError error);
    }

    interface CategoryListResponseCallback {
        void onSuccess(List<Category> response);
        void onError(VolleyError error);
    }
}
