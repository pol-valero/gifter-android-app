package com.example.giftr.presentation.mainFragments.home;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.example.giftr.R;
import com.example.giftr.business.SharedUser;
import com.example.giftr.business.entities.Wishlist;

import com.example.giftr.persistence.ApiResponseCallback;
import com.example.giftr.persistence.WishlistDAO;
import com.example.giftr.persistence.swaggerApi.SwaggerWishlistDAO;
import com.example.giftr.presentation.EditWishlistActivity;
import com.example.giftr.presentation.MainMenuActivity;
import com.example.giftr.presentation.MyWishlistGiftsActivity;

import org.json.JSONObject;

import io.github.muddz.styleabletoast.StyleableToast;


public class OwnWishlistHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private int color = 0;
    private int[] colors = {R.color.color0, R.color.color1, R.color.color2, R.color.color3, R.color.color4, R.color.color5, R.color.color6};
    private Wishlist wishlist;
    private WishlistAdapter wishlistAdapter;
    private TextView tvName;
    private TextView tvDescription;
    private ImageView ivImage;
    private ImageButton bRemove;
    private Button bEdit;
    private View vDivider;
    private View vColor;
    private Activity activity;
    private SharedUser sharedUser;

    public OwnWishlistHolder(@NonNull View itemView, Activity activity, WishlistAdapter wishlistAdapter) {
        super(itemView);

        setupView();

        this.activity = activity;
        this.wishlistAdapter = wishlistAdapter;
        this.sharedUser = SharedUser.getInstance(activity);

        setListeners();
    }

    private void setupView() {
        tvName = (TextView) itemView.findViewById(R.id.wishlist_own_tvTitle);
        tvDescription = (TextView) itemView.findViewById(R.id.wishlist_own_tvDescription);
        bRemove = itemView.findViewById(R.id.wishlist_own_bRemove);
        bEdit = itemView.findViewById(R.id.wishlist_own_bEdit);
        vDivider = itemView.findViewById(R.id.wishlist_own_vDivider);
        vColor = itemView.findViewById(R.id.wishlist_own_vColor);
        ivImage = itemView.findViewById(R.id.wishlist_own_ivPicture);
    }

    private void setListeners() {

        itemView.setOnClickListener(this);

        bEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = itemView.getContext();
                GradientDrawable shapeDrawable = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.squircle);

                shapeDrawable.setColor(ContextCompat.getColor(context, colors[color]));

                Bundle args = new Bundle();
                args.putSerializable(Wishlist.WISHLIST_TAG, wishlist);
                ((MainMenuActivity) activity).changeActivity(EditWishlistActivity.class, args);
            }
        });

        bRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForConfirmation();
            }
        });
    }

    private void askForConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.dialog_wishlist_delete));
        builder.setMessage(activity.getString(R.string.dialog_confirmation));
        builder.setPositiveButton(activity.getString(R.string.calendar_accept), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked "Yes"
                if (wishlist.hasGifts()) {
                    StyleableToast.makeText(activity, activity.getString(R.string.gifts_remove_all), R.style.changesApplied).show();
                }
                else {
                    deleteWishlist();
                }
            }
        });
        builder.setNegativeButton(activity.getString(R.string.calendar_deny), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked "No"
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteWishlist() {
        WishlistDAO wishlistDAO = new SwaggerWishlistDAO();
        wishlistDAO.removeWishlistByID(wishlist.getID(), sharedUser.getUserAccessToken(), activity.getApplicationContext(), new ApiResponseCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                wishlistAdapter.removeRequest(getAdapterPosition());
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void bind(Wishlist wishlist, boolean checkLast, int color) {
        this.color = color;
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
        shapeDrawable.setColor(ContextCompat.getColor(context, colors[(color + 1) % 7]));
    }


    @Override
    public void onClick(View v) {
        MainMenuActivity mainMenuActivity = (MainMenuActivity) activity;

        Bundle args = new Bundle();
        args.putSerializable(Wishlist.WISHLIST_TAG, wishlist);

        mainMenuActivity.changeActivity(MyWishlistGiftsActivity.class, args);
    }
}
