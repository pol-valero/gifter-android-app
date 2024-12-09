package com.example.giftr.presentation.mainFragments.profile.giftList;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.example.giftr.R;
import com.example.giftr.business.ImageViewLoader;
import com.example.giftr.business.SharedUser;
import com.example.giftr.business.entities.Gift;
import com.example.giftr.business.entities.Product;
import com.example.giftr.business.entities.User;
import com.example.giftr.business.entities.Wishlist;
import com.example.giftr.persistence.GiftDAO;
import com.example.giftr.persistence.ProductDAO;
import com.example.giftr.persistence.UserDAO;
import com.example.giftr.persistence.WishlistDAO;
import com.example.giftr.persistence.swaggerApi.SwaggerGiftDAO;
import com.example.giftr.persistence.swaggerApi.SwaggerUserDAO;
import com.example.giftr.persistence.swaggerApi.SwaggerWishlistDAO;
import com.example.giftr.presentation.MainMenuActivity;
import com.example.giftr.presentation.mainFragments.profile.FriendProfileActivity;
import com.example.giftr.presentation.mainFragments.profile.NotFriendProfileActivity;

import java.util.List;

public class SentGiftHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Activity activity;

    private Gift gift;
    private User loggedUser;
    private ImageView ivItemPicture;
    private ImageView ivUserPicture;
    private TextView tvUserGift;
    private TextView tvWishlistName;
    private Wishlist wishlist;
    private SharedUser sharedUser;
    private User friend;
    private Product product;

    private String whishlistName;

    public SentGiftHolder(@NonNull View itemView, User loggedUser, Activity activity) {
        super(itemView);
        this.loggedUser = loggedUser;
        this.sharedUser = SharedUser.getInstance(itemView.getContext());
        this.activity = activity;

        setupView();
        setupListeners();
    }

    private void setupListeners() {
        itemView.setOnClickListener(this);
    }

    private void setupView() {
        tvWishlistName = itemView.findViewById(R.id.sentGift_tvWishlistName);
        tvUserGift = itemView.findViewById(R.id.sentGift_tvUserName);
        ivItemPicture = itemView.findViewById(R.id.sentGift_ivItemPicture);
        ivUserPicture = itemView.findViewById(R.id.sentGift_ivUserPicture);
    }

    public void bind(Gift gift, boolean checkLast) {
        this.gift = gift;

        loadProduct();
        loadUser();
        loadWishlist();

        if (checkLast) {
            itemView.findViewById(R.id.sentGift_vDivider).setVisibility(View.GONE);
        } else {
            itemView.findViewById(R.id.sentGift_vDivider).setVisibility(View.VISIBLE);
        }
    }

    private void loadUser() {
        GiftDAO giftDAO = new SwaggerGiftDAO(sharedUser.getLoggedUser().getAccessToken(), activity.getApplicationContext());

        giftDAO.getUserByGiftID(gift.getId(), new UserDAO.UserResponseCallback() {
            @Override
            public void onSuccess(User response) {
                friend = response;

                ImageViewLoader imageViewLoader = new ImageViewLoader(ivUserPicture);
                imageViewLoader.loadImage(friend.getImagePath());
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }

    private void loadProduct() {
        gift.getProduct(sharedUser.getLoggedUser().getAccessToken(), itemView.getContext(), new ProductDAO.ProductResponseCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(Product response) {
                product = response;

                ImageViewLoader imageViewLoader = new ImageViewLoader(ivItemPicture);
                imageViewLoader.loadImage(product.getPhoto());
                tvUserGift.setText(activity.getString(R.string.booked) + " " + product.getName());
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }

    private void loadWishlist() {
        WishlistDAO wishlistDAO = new SwaggerWishlistDAO();
        wishlistDAO.getWishlistById(gift.getWishlist_id(), loggedUser.getAccessToken(), itemView.getContext(), new WishlistDAO.WishlistResponseCallback() {
            @Override
            public void onSuccess(Wishlist response) {
                wishlist = response;
                whishlistName = response.getName();

                loadWishlistCreator();
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }

    private void loadWishlistCreator() {
        UserDAO userDAO = new SwaggerUserDAO();
        userDAO.getUserByID(wishlist.getUserID(), sharedUser.getLoggedUser().getAccessToken(), itemView.getContext(), new UserDAO.UserResponseCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(User response) {
                friend = response;
                tvWishlistName.setText(activity.getString(R.string.wishlist_from) + " " + friend.getName() + activity.getString(R.string.wishlist_apostrof) + " " + activity.getString(R.string.wishlist_wishlist));
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        // Wait for the profile to be loaded.
        if (friend != null) {
            checkFriends();
        }
    }

    private void checkFriends() {
        UserDAO userDAO = new SwaggerUserDAO();
        userDAO.getUserFriendsById(sharedUser.getLoggedUser().getId(), sharedUser.getLoggedUser().getAccessToken(), activity.getApplicationContext(), new UserDAO.UserListResponseCallback() {
            @Override
            public void onSuccess(List<User> response) {
                boolean isFriend = false;
                for (User user : response) {
                    if (user.sameUser(friend)) {
                        isFriend = true;
                        break;
                    }
                }

                Bundle args = new Bundle();
                args.putSerializable(User.FRIEND_NAME_TAG, friend);

                if (activity instanceof MainMenuActivity) {
                    MainMenuActivity mainMenuActivity = (MainMenuActivity) activity;

                    if (isFriend) {
                        mainMenuActivity.changeActivity(FriendProfileActivity.class, args);
                    }
                    else {
                        mainMenuActivity.changeActivity(NotFriendProfileActivity.class, args);
                    }
                }
                else {
                    if (sharedUser.getLoggedUser().sameUser(friend)) {
                        changeToProfile();
                        Intent intent = new Intent(activity, MainMenuActivity.class);
                        activity.startActivity(intent);
                    }
                    else {
                        Intent intent;
                        if (isFriend) {
                            intent = new Intent(activity, FriendProfileActivity.class);
                        }
                        else {
                            intent = new Intent(activity, NotFriendProfileActivity.class);
                        }
                        intent.putExtras(args);
                        activity.startActivity(intent);
                    }
                }
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }

    public void changeToProfile() {
        // Save the fragment identifier in SharedPreferences
        SharedPreferences preferences = activity.getSharedPreferences(SharedUser.SHARED_PREFERENCES_PERSISTENCE, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(MainMenuActivity.LAST_FRAGMENT_TAG, R.id.nav_profile);
        editor.apply();
    }
}
