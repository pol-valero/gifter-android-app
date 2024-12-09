package com.example.giftr.presentation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import com.example.giftr.R;
import com.example.giftr.business.SharedUser;
import com.example.giftr.business.entities.Wishlist;
import com.example.giftr.presentation.wishlistsGifts.MyGiftsAdapter;

public class OtherWishlistGiftsActivity extends AppCompatActivity {

    private SharedUser sharedUser;
    private RecyclerView recyclerView;
    private MyGiftsAdapter adapter;
    private Wishlist wishlist;
    private TextView tvWishlistName;
    private TextView tvWishlistEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_wishlist_gifts);

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
        recyclerView = findViewById(R.id.otherWishlistgifts_rvGifts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tvWishlistName = findViewById(R.id.otherWishlistgifts_tvWishlistName);
        tvWishlistName.setText(wishlist.getName());

        tvWishlistEndDate = findViewById(R.id.otherWishlistgifts_tvWishlistEndDate);

        if (wishlist.hasExpired()) {
            tvWishlistEndDate.setText(getString(R.string.wishlist_ended) + " " + wishlist.getSentTime(false));
        }
        else {
            tvWishlistEndDate.setText(getString(R.string.wishlist_ends_on) + " " + wishlist.getSentTime(false));
        }

        adapter = new MyGiftsAdapter(wishlist, wishlist.getGifts(), this);
        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {

    }
}