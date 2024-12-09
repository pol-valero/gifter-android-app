package com.example.giftr.presentation.mainFragments.home;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.giftr.R;
import com.example.giftr.business.entities.User;
import com.example.giftr.business.entities.Wishlist;

import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Wishlist> wishlists;
    private Activity activity;
    private User loggedUser;
    private static final int OWN_WISHLIST_VIEW = 1;
    private static final int FRIEND_WISHLIST_VIEW = 2;

    public WishlistAdapter (List<Wishlist> wishlists, Activity activity, User loggedUser) {
        this.wishlists = wishlists;
        this.activity = activity;
        this.loggedUser = loggedUser;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView;

        if (viewType == OWN_WISHLIST_VIEW) {
            itemView = inflater.inflate(R.layout.list_item_wishlist_own, parent, false);
            return new OwnWishlistHolder(itemView, activity, this);
        } else {
            itemView = inflater.inflate(R.layout.list_item_wishlist_friend, parent, false);
            return new FriendWishlistHolder(itemView, activity, this);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        boolean lastItem = (position == (getItemCount() - 1));
        if (viewType == OWN_WISHLIST_VIEW) {
            OwnWishlistHolder viewHolder1 = (OwnWishlistHolder) holder;
            viewHolder1.bind(wishlists.get(position), lastItem, position % 7);
        } else {
            FriendWishlistHolder viewHolder2 = (FriendWishlistHolder) holder;
            viewHolder2.bind(wishlists.get(position), lastItem, position % 7);
        }
    }

    @Override
    public int getItemCount() {
        return wishlists.size();
    }

    public void removeRequest(int adapterPosition) {
        wishlists.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);

        if (adapterPosition > 0) {
            notifyItemChanged(adapterPosition - 1);
        }
    }

    @Override
    public int getItemViewType(int position) {

        // If the user created the Wishlist, the view is from the own user, not a friend.
        if (wishlists.get(0).createdByUser(loggedUser.getId())) {
            return OWN_WISHLIST_VIEW;
        } else {
            return FRIEND_WISHLIST_VIEW;
        }
    }
}
