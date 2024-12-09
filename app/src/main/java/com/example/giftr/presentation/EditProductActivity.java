package com.example.giftr.presentation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.example.giftr.R;
import com.example.giftr.business.CategoryAdapter;
import com.example.giftr.business.ImageViewLoader;
import com.example.giftr.business.SharedUser;
import com.example.giftr.business.entities.Category;
import com.example.giftr.business.entities.Product;
import com.example.giftr.persistence.CategoryDAO;
import com.example.giftr.persistence.ImageDAO;
import com.example.giftr.persistence.ProductDAO;
import com.example.giftr.persistence.freeimagehost.FreeImageDAO;
import com.example.giftr.persistence.swaggerApi.SwaggerCategoryDAO;
import com.example.giftr.persistence.swaggerApi.SwaggerProductDAO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.github.muddz.styleabletoast.StyleableToast;

public class EditProductActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 3;
    private Bitmap productImage = null;
    private String productImageUrl;
    private ImageView ivImage;
    private EditText etName;
    private EditText etDescription;
    private EditText etLink;
    private EditText etPrice;
    private Button bEdit;
    private Spinner sCategories;
    private List<Category> categoryList = new ArrayList<>();
    private SharedUser sharedUser;
    private ImageButton bUploadImage;
    private Product product;
    private final static String IMAGE_DIR = "image/*";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        sharedUser = SharedUser.getInstance(getApplicationContext());
        product = (Product) getIntent().getSerializableExtra(Product.PRODUCT_TAG);

        setupView();
        setupListeners();
    }

    private void setupListeners() {
        bEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etName.getText().toString().isEmpty() || etDescription.getText().toString().isEmpty() || etLink.getText().toString().isEmpty() || etPrice.getText().toString().isEmpty() || !priceValid()) {
                    if (etName.getText().toString().isEmpty()) {
                        etName.setError(getString(R.string.some_fields_empty));
                        etName.requestFocus();
                    } else if (etDescription.getText().toString().isEmpty()) {
                        etDescription.setError(getString(R.string.some_fields_empty));
                        etDescription.requestFocus();
                    } else if (etLink.getText().toString().isEmpty()) {
                        etLink.setError(getString(R.string.some_fields_empty));
                        etLink.requestFocus();
                    }
                    else if (etPrice.getText().toString().isEmpty()) {
                        etPrice.setError(getString(R.string.some_fields_empty));
                        etPrice.requestFocus();
                    } else {
                        etPrice.setError(getString(R.string.price_format_invalid));
                        etPrice.requestFocus();
                    }

                } else {

                    if (productImage != null) {
                        postImage(productImage);
                    } else {
                        productImageUrl = Product.DEFAULT_PRODUCT_PICTURE;
                        editProduct();
                    }
                }
            }
        });

        bUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    private boolean priceValid() {
        return Pattern.compile(Product.PRODUCT_PRICE_REGEX).matcher(etPrice.getText().toString()).matches();
    }

    private void editProduct() {
        Category category = (Category) sCategories.getSelectedItem();
        String priceString = etPrice.getText().toString().replace(",", ".");

        Product newProduct = new Product(product.getId(), etName.getText().toString(), etDescription.getText().toString(), etLink.getText().toString(), productImageUrl, Float.parseFloat(priceString), new int[]{category.getId()});
        ProductDAO productDAO = new SwaggerProductDAO();
        productDAO.editProduct(newProduct, sharedUser.getLoggedUser().getAccessToken(), EditProductActivity.this, new ProductDAO.ProductResponseCallback() {
            @Override
            public void onSuccess(Product response) {
                StyleableToast.makeText(EditProductActivity.this, getString(R.string.product_modified), R.style.changesApplied).show();
                finish();
            }

            @Override
            public void onError(VolleyError error) {
                error.printStackTrace();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setupView() {
        ivImage = findViewById(R.id.editproduct_ivImage);
        etName = findViewById(R.id.editproduct_etName);
        etDescription = findViewById(R.id.editproduct_etDescription);
        etLink = findViewById(R.id.editproduct_etLink);
        etPrice = findViewById(R.id.editproduct_etPrice);
        bEdit = findViewById(R.id.editproduct_bEdit);
        bUploadImage = findViewById(R.id.editproduct_bProfileImage);

        sCategories = findViewById(R.id.editproduct_sCategories);
        loadCategories();

        etName.setText(product.getName());
        etDescription.setText(product.getDescription());
        etLink.setText(product.getLink());
        etPrice.setText(Float.toString(product.getPrice()));

        ImageViewLoader imageViewLoader = new ImageViewLoader(ivImage);
        imageViewLoader.loadImage(product.getPhoto());
    }

    private void loadCategories() {
        CategoryDAO categoryDAO = new SwaggerCategoryDAO(sharedUser.getLoggedUser().getAccessToken(), getApplicationContext());
        categoryDAO.getAllCategories(new CategoryDAO.CategoryListResponseCallback() {
            @Override
            public void onSuccess(List<Category> response) {
                categoryList = response;

                // IDs of the categories to keep
                List<Integer> idsToKeep = Arrays.asList(29, 30, 31, 32, 33, 34);

                // Filter the list and keep only the categories with matching IDs
                List<Category> filteredCategories = categoryList.stream()
                        .filter(category -> idsToKeep.contains(category.getId()))
                        .collect(Collectors.toList());

                CategoryAdapter adapter = new CategoryAdapter(getApplicationContext(), filteredCategories);
                sCategories.setAdapter(adapter);
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }

    public void openGallery() {
        Intent intent = new Intent();
        intent.setType(IMAGE_DIR);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.gallery_select_picture)), PICK_IMAGE_REQUEST);
    }

    // Handle the result of the image selection
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                productImage = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ivImage.setImageBitmap(productImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void postImage(Bitmap imageBitmap) {
        ImageDAO imageDAO = new FreeImageDAO();
        imageDAO.uploadImage(imageBitmap, this, new ImageDAO.ImageResponseCallback() {
            @Override
            public void onSuccess(String imageURL) {
                productImageUrl = imageURL;
                editProduct();
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }

}