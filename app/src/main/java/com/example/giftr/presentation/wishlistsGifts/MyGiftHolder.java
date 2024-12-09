package com.example.giftr.presentation.wishlistsGifts;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
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

public class MyGiftHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Gift gift;
    private Product product;
    private MyGiftsAdapter giftsAdapter;
    private TextView tvGiftName;
    private TextView tvDescription;
    private TextView tvPrice;
    private TextView tvPriority;
    private ImageView ivImage;
    private ImageButton bRemove;
    private Button bBooked;
    private Activity activity;
    private SharedUser sharedUser;
    public MyGiftHolder(@NonNull View itemView, Activity activity, MyGiftsAdapter giftsAdapter) {
        super(itemView);

        tvGiftName = (TextView) itemView.findViewById(R.id.myWishlistgifts_tvGiftName);
        tvDescription = (TextView) itemView.findViewById(R.id.myWishlistgifts_tvGiftDescription);
        bRemove = itemView.findViewById(R.id.myWishlistgifts_bRemoveGift);
        ivImage = itemView.findViewById(R.id.myWishlistgifts_ivGiftImg);
        tvPrice = itemView.findViewById(R.id.myWishlistgifts_tvPrice);
        tvPriority = itemView.findViewById(R.id.myWishlistgifts_tvGiftPriority);
        bBooked = itemView.findViewById(R.id.myWishlistgifts_bBooked);

        this.activity = activity;
        this.giftsAdapter = giftsAdapter;
        this.sharedUser = SharedUser.getInstance(itemView.getContext());

        setListeners();
    }

    private void setListeners() {

        itemView.setOnClickListener(this);

        bRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForConfirmation();
            }
        });
    }

    private void askForConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Gift delete confirmation");
        builder.setMessage("Do you want to proceed?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked "Yes"
                deleteGift();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked "No"
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteGift() {
        GiftDAO giftDAO = new SwaggerGiftDAO(sharedUser.getLoggedUser().getAccessToken(), activity.getApplicationContext());
        giftDAO.deleteGiftById(gift.getId(), new ApiResponseCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                giftsAdapter.removeRequest(getAdapterPosition());
            }

            @Override
            public void onError(VolleyError error) {

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

        Drawable drawable;
        if (gift.isBooked()) {
            bBooked.setText(R.string.booked);
            drawable = AppCompatResources.getDrawable(activity, R.drawable.option_pink_button);
            bBooked.setBackground(drawable);
        } else {
            bBooked.setText(R.string.not_booked);
            drawable = AppCompatResources.getDrawable(activity, R.drawable.option_button);
        }

        bBooked.setBackground(drawable);
        bBooked.setVisibility(View.VISIBLE);

        tvPriority.setText(String.valueOf(gift.getPriority()));
    }

    @Override
    public void onClick(View v) {
//        Intent intent = new Intent(activity, EditProductActivity.class);
//        intent.putExtra("gift", gift);
//
//        activity.startActivity(intent);
        showIntegerInputDialog(activity);
    }

    private void showIntegerInputDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your gift's priority");
        builder.setMessage("Remember: 0 = lowest ... 100 = highest");

        // Create an EditText view to accept integer input
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get the entered integer value
                String stringValue = input.getText().toString();
                int integerValue = Integer.parseInt(stringValue);

                if (integerValue >= 0 && integerValue <= 100) {
                    int priority = integerValue;
                    gift.setPriority(priority);
                    editGift();
                } else {
                    // Show an error message and ask again
                    StyleableToast.makeText(activity, activity.getString(R.string.dialog_priority), R.style.changesApplied).show();
                    showIntegerInputDialog(context); // Recursive call to show the dialog again
                }
            }
        });

        builder.setNegativeButton(activity.getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void editGift() {
        GiftDAO giftDAO = new SwaggerGiftDAO(sharedUser.getUserAccessToken(), activity);
        giftDAO.editGiftById(gift, new GiftDAO.GiftResponseCallback() {
            @Override
            public void onSuccess(Gift response) {
                gift = response;
                giftsAdapter.orderGifts();
                giftsAdapter.notifyItemChanged(getAdapterPosition());
                StyleableToast.makeText(activity, activity.getString(R.string.gift_modified), R.style.changesApplied).show();
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }
}
