package com.example.giftr.presentation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.example.giftr.R;
import com.example.giftr.business.SharedUser;
import com.example.giftr.business.entities.Wishlist;
import com.example.giftr.persistence.WishlistDAO;
import com.example.giftr.persistence.swaggerApi.SwaggerWishlistDAO;
import com.example.giftr.presentation.wishlistsGifts.MyGiftsAdapter;

public class MyWishlistGiftsActivity extends AppCompatActivity {

    private SharedUser sharedUser;
    private RecyclerView recyclerView;
    private MyGiftsAdapter adapter;
    private Button bAddGift;
    private Wishlist wishlist;
    private TextView tvWishlistName;
    private TextView tvWishlistDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mywishlist_gifts);

        sharedUser = SharedUser.getInstance(getApplicationContext());
        getWishlist();
        setupView();
        setupListeners();
    }

    private void getWishlist() {
        Bundle args = getIntent().getExtras();

        if (args != null) {
            wishlist = (Wishlist) args.getSerializable(Wishlist.WISHLIST_TAG);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setupView() {
        recyclerView = findViewById(R.id.mywishlistgifts_rvGifts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bAddGift = findViewById(R.id.mywishlistgifts_bAddGift);

        tvWishlistName = findViewById(R.id.mywishlistgifts_tvWishlistName);
        tvWishlistName.setText(wishlist.getName());

        tvWishlistDate = findViewById(R.id.myWishlistgifts_tvWishlistEndDate);
        tvWishlistDate.setText(getString(R.string.wishlist_ends_on) + " " + wishlist.getSentTime(false));
    }

    private void setupListeners() {
        bAddGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyWishlistGiftsActivity.this, AllProductsActivity.class);
                intent.putExtras(getIntent().getExtras());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadWishlist();
    }

    public void loadWishlist() {
        WishlistDAO wishlistDAO = new SwaggerWishlistDAO();
        wishlistDAO.getWishlistById(wishlist.getWishlistID(), sharedUser.getLoggedUser().getAccessToken(), getApplicationContext(), new WishlistDAO.WishlistResponseCallback() {
            @Override
            public void onSuccess(Wishlist response) {
                wishlist = response;
                adapter = new MyGiftsAdapter(wishlist, wishlist.getGifts(), MyWishlistGiftsActivity.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }
}
