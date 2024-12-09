package com.example.giftr.presentation.productsList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.example.giftr.R;
import com.example.giftr.business.ImageViewLoader;
import com.example.giftr.business.SharedUser;
import com.example.giftr.business.entities.Gift;
import com.example.giftr.business.entities.Product;
import com.example.giftr.business.entities.Wishlist;
import com.example.giftr.persistence.GiftDAO;
import com.example.giftr.persistence.swaggerApi.SwaggerGiftDAO;
import com.example.giftr.presentation.EditProductActivity;

import io.github.muddz.styleabletoast.StyleableToast;

public class ProductHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Activity activity;
    private ProductAdapter productAdapter;
    private TextView tvProductName;
    private TextView tvProductDescription;
    private TextView tvProductPrice;
    private ImageView ivProductPicture;
    private ImageView ivEditProduct;
    private Button bAddProduct;
    private Product product;
    private SharedUser sharedUser;
    private Wishlist wishlist;
    private int priority;

    public ProductHolder(@NonNull View itemView, Activity activity, ProductAdapter productAdapter, Wishlist wishlist) {
        super(itemView);

        this.activity = activity;
        this.productAdapter = productAdapter;
        this.sharedUser = SharedUser.getInstance(activity.getApplicationContext());
        this.wishlist = wishlist;

        setupView();
        setupListeners();
    }

    private void setupListeners() {
        itemView.setOnClickListener(this);

        ivEditProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, EditProductActivity.class);
                intent.putExtra(Product.PRODUCT_TAG, product);

                activity.startActivity(intent);
            }
        });

        bAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showIntegerInputDialog(activity);
            }
        });
    }

    private void addGiftToWishlist() {
        GiftDAO giftDAO = new SwaggerGiftDAO(sharedUser.getLoggedUser().getAccessToken(), activity.getApplicationContext());

        Gift gift = new Gift(wishlist.getWishlistID(), product.getProductUrl(), priority);
        giftDAO.createGift(gift, new GiftDAO.GiftResponseCallback() {
            @Override
            public void onSuccess(Gift response) {
                StyleableToast.makeText(activity, activity.getString(R.string.products_addedGift), R.style.changesApplied).show();
                activity.finish();
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }

    private void showIntegerInputDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(activity.getString(R.string.dialog_gift_priority));
        builder.setMessage(activity.getString(R.string.dialog_priority_bounds));

        // Create an EditText view to accept integer input
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton(activity.getString(R.string.dialog_add_gift), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get the entered integer value
                String stringValue = input.getText().toString();
                int integerValue = Integer.parseInt(stringValue);

                if (integerValue >= 0 && integerValue <= 100) {
                    priority = integerValue;
                    addGiftToWishlist();
                } else {
                    // Show an error message and ask again
                    Toast.makeText(context, activity.getString(R.string.dialog_priority), Toast.LENGTH_SHORT).show();
                    showIntegerInputDialog(context); // Recursive call to show the dialog again
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void openBrowser() {
        // Create a Uri object from the URL string
        Uri webpage = Uri.parse(product.getLink());

        // Create an intent with ACTION_VIEW to open the URL in a browser
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

        // Verify that there is an app available to handle the intent
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            // Start the intent if there is an app available
            activity.startActivity(intent);
        }
    }

    private void setupView() {
        tvProductName = itemView.findViewById(R.id.products_tvProductName);
        tvProductDescription = itemView.findViewById(R.id.products_tvProductDescription);
        tvProductPrice = itemView.findViewById(R.id.products_tvPrice);
        ivProductPicture = itemView.findViewById(R.id.products_ivProductImg);
        ivEditProduct = itemView.findViewById(R.id.products_ivEditProduct);
        bAddProduct = itemView.findViewById(R.id.products_bAdd);
    }

    @SuppressLint("SetTextI18n")
    public void bind(Product product) {
        this.product = product;

        tvProductName.setText(product.getName());
        tvProductDescription.setText(product.getDescription());
        tvProductPrice.setText(product.getFormattedPrice());

        ImageViewLoader imageViewLoader = new ImageViewLoader(ivProductPicture);
        imageViewLoader.loadImage(product.getPhoto());
    }

    @Override
    public void onClick(View v) {
        openBrowser();
    }
}
