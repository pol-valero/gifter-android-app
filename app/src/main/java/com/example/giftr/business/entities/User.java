package com.example.giftr.business.entities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.regex.Pattern;

public class User implements Serializable {
    private final String name;
    private final String lastName;
    private final String email;
    private String password;
    private String imagePath;
    private int id;
    private String accessToken;
    public final static String LOGGED_USER = "loggedUser";
    public final static String FRIEND_NAME_TAG = "friendUser";
    public final static String PROFILE_PICTURE_TAG = "UserProfilePicture";
    public final static String VALID_IMAGE_URL_REGEX = "(http(s?):)([/|.|\\w|\\s|-])*/([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";
    public final static String DEFAULT_PROFILE_PICTURE = "https://t4.ftcdn.net/jpg/02/15/84/43/360_F_215844325_ttX9YiIIyeaR7Ne6EaLLjMAmy4GvPC69.jpg";

    // User used to register
    public User(String name, String lastName, String email, String password, String imagePath) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.imagePath = imagePath;
        this.password = password;
    }

    public User(String name, String lastName, String email) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.imagePath = DEFAULT_PROFILE_PICTURE;
    }

    public User(String name, String lastName, String email, String imagePath) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }
    public String getFullName() {
        return name + " " + lastName;
    }
    public String getEmail() {
        return email;
    }
    public String getImagePath() {
        return imagePath;
    }
    public String getLastName() {
        return lastName;
    }
    public String getPassword() {
        return password;
    }
    public String getAccessToken() {
        return accessToken;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    // Transform a JSONObject into a User instance.
    public static User fromJson(JSONObject jsonObject) {
        String name = null;
        String lastName = null;
        String email = null;
        String imagePath = null;

        try {
            name = jsonObject.getString("name");
            lastName = jsonObject.getString("last_name");
            email = jsonObject.getString("email");
            imagePath = jsonObject.getString("image");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        User user = new User(name, lastName, email, imagePath);

        if (!user.validProfileImage()) {
            user.setImagePath(DEFAULT_PROFILE_PICTURE);
        }

        if (jsonObject.has("id")) {
            int id = 0;

            try {
                id = jsonObject.getInt("id");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            user.setId(id);
        }

        if (jsonObject.has("accessToken")) {
            String accessToken = null;

            try {
                accessToken = jsonObject.getString("accessToken");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            user.setAccessToken(accessToken);
        }

        return user;
    }

    public boolean validProfileImage() {
        return Pattern.compile(VALID_IMAGE_URL_REGEX).matcher(imagePath).matches();
    }

    public boolean sameUser(User otherUser) {
        return this.id == otherUser.getId();
    }
}
