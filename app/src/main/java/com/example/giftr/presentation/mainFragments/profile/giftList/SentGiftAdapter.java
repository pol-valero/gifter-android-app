package com.example.giftr.presentation.mainFragments.profile.giftList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.giftr.R;
import com.example.giftr.business.entities.Gift;
import com.example.giftr.business.entities.User;

import java.util.List;

public class SentGiftAdapter extends RecyclerView.Adapter<SentGiftHolder> {

    private List<Gift> gifts;
    private Activity activity;
    private View view;
    private User loggedUser;

    public SentGiftAdapter(List<Gift> gifts, Activity activity, User loggedUser) {
        this.gifts = gifts;
        this.activity = activity;
        this.loggedUser = loggedUser;
    }

    @NonNull
    @Override
    public SentGiftHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        view = layoutInflater.inflate(R.layout.list_item_gift_sent, parent, false);
        return new SentGiftHolder(view, loggedUser, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull SentGiftHolder holder, int position) {
        holder.bind(gifts.get(position), position == gifts.size() - 1);
    }

    @Override
    public int getItemCount() {
        return gifts.size();
    }
}
