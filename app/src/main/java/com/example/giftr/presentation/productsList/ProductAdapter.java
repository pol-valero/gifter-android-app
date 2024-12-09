package com.example.giftr.presentation.productsList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.giftr.R;
import com.example.giftr.business.SharedUser;
import com.example.giftr.business.entities.Product;
import com.example.giftr.business.entities.Wishlist;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductHolder> {

    private List<Product> products;
    private Activity activity;
    private SharedUser sharedUser;
    private Wishlist wishlist;

    public ProductAdapter(List<Product> products, Activity activity, Wishlist wishlist) {
        this.products = products;
        this.activity = activity;
        this.sharedUser = SharedUser.getInstance(activity.getApplicationContext());
        this.wishlist = wishlist;
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.list_item_product, parent, false);
        return new ProductHolder(itemView, activity, this, wishlist);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductHolder holder, int position) {
        holder.bind(products.get(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void setFilter(List<Product> filteredGifts) {
        products = filteredGifts;
        notifyDataSetChanged();
    }
}
