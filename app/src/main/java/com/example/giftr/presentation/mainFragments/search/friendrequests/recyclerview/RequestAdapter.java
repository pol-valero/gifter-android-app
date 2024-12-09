package com.example.giftr.presentation.mainFragments.search.friendrequests.recyclerview;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.giftr.business.entities.User;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestViewHolder> {
    private List<User> friendRequests;
    private final Activity activity;
    public User user;
    public Context context;

    public RequestAdapter(List<User> friendRequests, Activity activity, User user, Context context) {
        this.friendRequests = friendRequests;
        this.activity = activity;
        this.user = user;
        this.context = context;
    }

    /**
     * Called when RecyclerView needs a new of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link }. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary } calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     */
    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        return new RequestViewHolder(layoutInflater, parent, activity, this);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use which will
     * have the updated adapter position.
     * <p>
     * Override {@link } instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        holder.bind(friendRequests.get(position));
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return friendRequests.size();
    }

    public void removeRequest(int adapterPosition) {
        friendRequests.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);

        if (adapterPosition > 0) {
            notifyItemChanged(adapterPosition - 1);
        }
    }
}
