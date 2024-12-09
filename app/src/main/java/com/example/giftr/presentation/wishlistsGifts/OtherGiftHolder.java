package com.example.giftr.presentation.wishlistsGifts;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
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
import com.example.giftr.persistence.ApiResponseCallback;
import com.example.giftr.persistence.GiftDAO;
import com.example.giftr.persistence.ProductDAO;
import com.example.giftr.persistence.swaggerApi.SwaggerGiftDAO;

import org.json.JSONObject;

import io.github.muddz.styleabletoast.StyleableToast;

public class OtherGiftHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Gift gift;
    private Product product;
    private MyGiftsAdapter giftsAdapter;
    private TextView tvGiftName;
    private TextView tvDescription;
    private TextView tvPrice;
    private TextView tvPriority;
    private ImageView ivImage;
    private Button bBooked;
    private Activity activity;
    private SharedUser sharedUser;

    public OtherGiftHolder(@NonNull View itemView, Activity activity, MyGiftsAdapter giftsAdapter) {
        super(itemView);

        tvGiftName = (TextView) itemView.findViewById(R.id.otherWishlistgifts_tvGiftName);
        tvDescription = (TextView) itemView.findViewById(R.id.otherWishlistgifts_tvGiftDescription);
        ivImage = itemView.findViewById(R.id.otherWishlistgifts_ivGiftImg);
        tvPrice = itemView.findViewById(R.id.otherWishlistgifts_tvPrice);
        tvPriority = itemView.findViewById(R.id.otherWishlistgifts_tvGiftPriority);
        bBooked = itemView.findViewById(R.id.otherWishlistgifts_bBooked);

        this.activity = activity;
        this.giftsAdapter = giftsAdapter;
        this.sharedUser = SharedUser.getInstance(itemView.getContext());

        setListeners();
    }

    private void setListeners() {
        itemView.setOnClickListener(this);

        bBooked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!gift.isBooked() && !giftsAdapter.wishlist.hasExpired()) {
                    GiftDAO giftDAO = new SwaggerGiftDAO(sharedUser.getLoggedUser().getAccessToken(), activity.getApplicationContext());
                    giftDAO.bookGiftById(gift.getId(), new ApiResponseCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            gift.setBooked(true);
                            bBooked.setText(R.string.booked);
                            bBooked.setBackgroundResource(R.drawable.option_button);
                            giftsAdapter.orderGifts();
                        }

                        @Override
                        public void onError(VolleyError error) {
                        }
                    });
                }
                else {
                    if (giftsAdapter.wishlist.hasExpired()) {
                        StyleableToast.makeText(activity, activity.getString(R.string.wishlist_expired), R.style.changesApplied).show();
                    }
                    else {
                        StyleableToast.makeText(activity, activity.getString(R.string.gift_already_booked), R.style.changesApplied).show();
                    }
                }
            }
        });
    }

    public void bind(Gift gift, boolean lastItem) {
        this.gift = gift;

        gift.getProduct(sharedUser.getLoggedUser().getAccessToken(), itemView.getContext(), new ProductDAO.ProductResponseCallback() {
            @Override
            public void onSuccess(Product response) {
                product = response;
                tvGiftName.setText(product.getName());
                tvDescription.setText(product.getDescription());
                tvPrice.setText(product.getFormattedPrice());

                ImageViewLoader imageViewLoader = new ImageViewLoader(ivImage);
                imageViewLoader.loadImage(product.getPhoto());
            }

            @Override
            public void onError(VolleyError error) {

            }
        });

        if (gift.isBooked()) {
            bBooked.setText(R.string.booked);
            bBooked.setBackgroundResource(R.drawable.option_button);
        } else {
            bBooked.setText(R.string.book_gift);
            bBooked.setBackgroundResource(R.drawable.option_pink_button);
        }

        tvPriority.setText(String.valueOf(gift.getPriority()));
    }

    @Override
    public void onClick(View v) {

    }
}
