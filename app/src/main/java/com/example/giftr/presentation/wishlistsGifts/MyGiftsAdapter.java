package com.example.giftr.presentation.wishlistsGifts;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.giftr.R;
import com.example.giftr.business.SharedUser;
import com.example.giftr.business.entities.Gift;
import com.example.giftr.business.entities.User;
import com.example.giftr.business.entities.Wishlist;

import java.util.Comparator;
import java.util.List;

public class MyGiftsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Gift> gifts;
    private Activity activity;
    private User loggedUser;
    public Wishlist wishlist;
    private SharedUser sharedUser;
    private static final int OWN_WISHLIST_VIEW = 1;
    private static final int FRIEND_WISHLIST_VIEW = 2;

    public MyGiftsAdapter(Wishlist wishlist, List<Gift> gifts, Activity activity) {
        this.gifts = gifts;
        orderGifts();
        this.activity = activity;
        this.wishlist = wishlist;
        this.sharedUser = SharedUser.getInstance(activity.getApplicationContext());
    }

    public void orderGifts() {
        this.gifts.sort(Comparator.comparing(Gift::getPriority).reversed().thenComparing(Gift::isBooked));
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView;

        if (viewType == OWN_WISHLIST_VIEW) {
            itemView = inflater.inflate(R.layout.list_item_gift, parent, false);
            return new MyGiftHolder(itemView, activity, this);
        } else {
            itemView = inflater.inflate(R.layout.list_item_other_gift, parent, false);
            return new OtherGiftHolder(itemView, activity, this);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        boolean lastItem = (position == (getItemCount() - 1));
        if (viewType == OWN_WISHLIST_VIEW) {
            MyGiftHolder viewHolder1 = (MyGiftHolder) holder;
            viewHolder1.bind(gifts.get(position), lastItem);
        } else {
            OtherGiftHolder viewHolder2 = (OtherGiftHolder) holder;
            viewHolder2.bind(gifts.get(position), lastItem);
        }
    }

    @Override
    public int getItemViewType(int position) {

        // If the user created the Wishlist, the view is from the own user, not a friend.
        if (wishlist.createdByUser(sharedUser.getLoggedUser().getId())) {
            return OWN_WISHLIST_VIEW;
        } else {
            return FRIEND_WISHLIST_VIEW;
        }
    }

    @Override
    public int getItemCount() {
        return gifts.size();
    }

    public void removeRequest(int adapterPosition) {
        gifts.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
    }

}
