package com.example.giftr.business.entities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Category {
    private int id;
    private String name;
    private String description;
    private String photo;
    private int categoryParentId;

    public Category(int id, String name, String description, String photo, int categoryParentId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.photo = photo;
        this.categoryParentId = categoryParentId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static Category fromJsonObject(JSONObject categoryObject) {
        try {
            int id = categoryObject.getInt("id");
            String name = categoryObject.getString("name");
            String description = categoryObject.getString("id");
            String photo = categoryObject.getString("id");

            int categoryParentId = 1;
            if (!categoryObject.isNull("categoryParentId")) {
                categoryParentId = categoryObject.getInt("categoryParentId");
            }

            return new Category(id, name, description, photo, categoryParentId);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Category> fromJsonArray(JSONArray categoryArray) {
        List<Category> categories = new ArrayList<>();

        for (int i = 0; i < categoryArray.length(); i++) {
            try {
                JSONObject jsonObject = categoryArray.getJSONObject(i);
                categories.add(Category.fromJsonObject(jsonObject));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        return categories;
    }
}
