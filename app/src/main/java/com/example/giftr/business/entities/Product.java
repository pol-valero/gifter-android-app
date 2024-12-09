package com.example.giftr.business.entities;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class Product implements Serializable {

    private int id;
    private String name;
    private String description;
    private String productUrl;
    private String link;
    private String photo;
    private float price;
    private int[] categoryIds;
    public final static String DEFAULT_PRODUCT_PICTURE = "https://i.imgur.com/mCxfzLe.png";
    public final static String DEFAULT_PRODUCT_PICTURE_SALLE = "https://balandrau.salle.url.edu/i3/repositoryimages/thumbnail/1a.png";
    private final static String PRODUCT_URL = "https://balandrau.salle.url.edu/i3/mercadoexpress/api/v1/products/";
    public final static String PRODUCT_TAG = "ProductTag";
    public final static String PRODUCT_PRICE_REGEX = "\\d{1,3}(?:[.,]\\d{3})*(?:[.,]\\d{2})";

    public Product(String name, String description, String link, String photo, float price, int[] categoryIds) {
        this.name = name;
        this.description = description;
        this.link = link;
        this.photo = photo;
        this.price = price;
        this.categoryIds = categoryIds;

        if (!validImage()) {
            this.photo = DEFAULT_PRODUCT_PICTURE;
        }
    }

    public Product(int id, String name, String description, String link, String photo, float price, int[] categoryIds) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.link = link;
        this.photo = photo;
        this.price = price;
        this.categoryIds = categoryIds;
        this.productUrl = PRODUCT_URL + id;

        if (!validImage()) {
            this.photo = DEFAULT_PRODUCT_PICTURE;
        }
    }

    public int getId() {
        return id;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public String getPhoto() {
        return photo;
    }

    public float getPrice() {
        return price;
    }

    public String getFormattedPrice() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return decimalFormat.format(price) + " €";
    }

    // Transform a JSONObject into a Product instance.
    public static Product fromJson(JSONObject jsonObject) {
        int id = 0;
        String name = null;
        String description = null;
        String link = null;
        String photo = null;
        float price = 0;
        int[] categoryIds = new int[0];

        try {
            id = jsonObject.getInt("id");
            name = jsonObject.getString("name");
            description = jsonObject.getString("description");
            link = jsonObject.getString("link");
            photo = jsonObject.getString("photo");
            price = (float) jsonObject.getDouble("price");

            if (!jsonObject.isNull("categoryIds")) {
                JSONArray categories = jsonObject.getJSONArray("categoryIds");

                if (categories.length() > 0) {
                    categoryIds = new int[categories.length()];
                    for (int i = 0; i < categories.length(); i++) {
                        categoryIds[i] = categories.getInt(i);
                    }
                }
                else {
                    categoryIds = new int[]{29};
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new Product(id, name, description, link, photo, price, categoryIds);
    }

    public JSONObject toJson() {
        JSONObject productObject = new JSONObject();

        try {
            productObject.put("name", name);
            productObject.put("description", description);
            productObject.put("link", link);
            productObject.put("photo", photo);
            productObject.put("price", price);
            productObject.put("categoryIds", categoryIds);

            // Create a Gson object
//            Gson gson = new Gson();

            // Convert int[] to JsonArray
//            JsonArray categories = gson.toJsonTree(categoryIds).getAsJsonArray();
//            productObject.put("categoryIds", categories);
        } catch (JSONException e) {
            return null;
        }

        return productObject;
    }

    public boolean validImage() {
        return Pattern.compile(User.VALID_IMAGE_URL_REGEX).matcher(photo).matches() && !photo.equals(DEFAULT_PRODUCT_PICTURE_SALLE);
    }

    public void setId(int id) {
        this.id = id;
    }

}
