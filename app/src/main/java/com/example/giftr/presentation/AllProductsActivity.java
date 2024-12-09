package com.example.giftr.presentation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.example.giftr.R;
import com.example.giftr.business.SharedUser;
import com.example.giftr.business.entities.Product;
import com.example.giftr.business.entities.Wishlist;
import com.example.giftr.persistence.ProductDAO;
import com.example.giftr.persistence.swaggerApi.SwaggerProductDAO;
import com.example.giftr.presentation.productsList.ProductAdapter;

import java.util.List;
import java.util.stream.Collectors;

public class AllProductsActivity extends AppCompatActivity {

    private RecyclerView rvProducts;
    private ProductAdapter productAdapter;
    private Button bCreateProduct;
    private EditText etFilterProduct;
    private SharedUser sharedUser;
    private Wishlist wishlist;
    private List<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_products);

        sharedUser = SharedUser.getInstance(getApplicationContext());

        getWishlist();
        setupView();
        setupListeners();
    }

    private void setupListeners() {
        bCreateProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllProductsActivity.this, CreateProductActivity.class);
                startActivity(intent);
            }
        });

        etFilterProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etFilterProduct.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    // Hide the keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    // Limit the EditText to one line
                    etFilterProduct.setMaxLines(1);

                    return true;
                }
                return false;
            }
        });
    }

    public void applyFilter(String filter) {
        if (products != null) {
            List<Product> filteredUsers = products.stream()
                    .filter(product -> product.getDescription().toLowerCase().contains(filter.toLowerCase()) || product.getName().toLowerCase().contains(filter.toLowerCase()))
                    .collect(Collectors.toList());
            productAdapter.setFilter(filteredUsers);
        }
    }

    private void getWishlist() {
        Bundle args = getIntent().getExtras();

        if (args != null) {
            wishlist = (Wishlist) args.getSerializable(Wishlist.WISHLIST_TAG);
        }
    }

    private void setupView() {
        rvProducts = findViewById(R.id.products_rvProducts);
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        rvProducts.requestFocus();

        bCreateProduct = findViewById(R.id.products_bCreate);
        etFilterProduct = findViewById(R.id.products_etSearchFilter);
    }

    private void loadProducts() {
        ProductDAO productDAO = new SwaggerProductDAO();
        productDAO.getAllProducts(sharedUser.getLoggedUser().getAccessToken(), getApplicationContext(), new ProductDAO.ProductListResponseCallback() {
            @Override
            public void onSuccess(List<Product> response) {
                products = response;
                productAdapter = new ProductAdapter(response, AllProductsActivity.this, wishlist);
                rvProducts.setAdapter(productAdapter);
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadProducts();
    }
}