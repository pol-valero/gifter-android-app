package com.example.giftr.presentation.mainFragments.home;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.giftr.R;
import com.example.giftr.business.entities.Wishlist;
import com.example.giftr.presentation.OtherWishlistGiftsActivity;

public class FriendWishlistHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private int[] colors = {R.color.color0, R.color.color1, R.color.color2, R.color.color3, R.color.color4, R.color.color5, R.color.color6};
    private Wishlist wishlist;
    private WishlistAdapter wishlistAdapter;
    private TextView tvName;
    private TextView tvDescription;
    private ImageView ivImage;
    private View vDivider;
    private View vColor;
    private Activity activity;

    public FriendWishlistHolder(@NonNull View itemView, Activity activity, WishlistAdapter wishlistAdapter) {
        super(itemView);

        setupView();

        this.activity = activity;
        this.wishlistAdapter = wishlistAdapter;
    }

    private void setupView() {
        tvName = (TextView) itemView.findViewById(R.id.wishlist_friend_tvTitle);
        tvDescription = (TextView) itemView.findViewById(R.id.wishlist_friend_tvDescription);
        vColor = itemView.findViewById(R.id.wishlist_friend_vColor);
        vDivider = itemView.findViewById(R.id.wishlist_friend_vDivider);
        ivImage = itemView.findViewById(R.id.wishlist_friend_ivPicture);

        itemView.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    public void bind(Wishlist wishlist, boolean checkLast, int color) {
        Context context = itemView.getContext();
        GradientDrawable shapeDrawable = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.squircle);

        shapeDrawable.setColor(ContextCompat.getColor(context, colors[color]));

        this.wishlist = wishlist;

        if (wishlist.hasExpired()) {
            tvName.setText(activity.getString(R.string.expired_wishlist) + " " + wishlist.getName());
        }
        else {
            tvName.setText(wishlist.getName());
        }

        tvDescription.setText(wishlist.getDescription());
        vColor.setBackgroundResource(R.drawable.squircle);

        if (checkLast) {
            vDivider.setVisibility(View.GONE);
        }
        else {
            vDivider.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(activity, OtherWishlistGiftsActivity.class);

        Bundle args = new Bundle();
        args.putSerializable(Wishlist.WISHLIST_TAG, wishlist);
        intent.putExtras(args);

        activity.startActivity(intent);
    }
}
