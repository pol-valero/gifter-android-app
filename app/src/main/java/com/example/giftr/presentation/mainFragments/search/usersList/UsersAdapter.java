package com.example.giftr.presentation.mainFragments.search.usersList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.giftr.R;
import com.example.giftr.business.entities.User;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private List<User> users;
	private User loggedUser;
	private Activity activity;
	private static final int USER_VIEW_TYPE = 1;
	private static final int FRIEND_VIEW_TYPE = 2;
	private boolean friendsView;

	public UsersAdapter(List<User> users, Activity activity, User loggedUser, boolean friendsView) {
		this.users = users;
		this.activity = activity;
		this.loggedUser = loggedUser;
		this.friendsView = friendsView;
	}

	@NonNull
	@NotNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		LayoutInflater layoutInflater = LayoutInflater.from(activity);
		View itemView;

		if (viewType == USER_VIEW_TYPE) {
			itemView = layoutInflater.inflate(R.layout.list_item_users, parent, false);
			return new UsersViewHolder(itemView, activity, loggedUser);
		} else {
			itemView = layoutInflater.inflate(R.layout.list_item_friends, parent, false);
			return new FriendsViewHolder(itemView, activity, loggedUser, this);
		}

	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		User user = users.get(position);
		int viewType = getItemViewType(position);

		if (viewType == USER_VIEW_TYPE) {
			UsersViewHolder viewHolder1 = (UsersViewHolder) holder;
			viewHolder1.bind(user, position == users.size() - 1);
		} else {
			FriendsViewHolder viewHolder2 = (FriendsViewHolder) holder;
			viewHolder2.bind(user, position == users.size() - 1);
		}
	}

	/**
	 * Returns the total number of items in the data set held by the adapter.
	 *
	 * @return The total number of items in this adapter.
	 */
	@Override
	public int getItemCount() {
		return users.size();
	}

	public void setFilter(List<User> filteredUsers) {
		users = filteredUsers;
		notifyDataSetChanged();
	}

	@Override
	public int getItemViewType(int position) {
		// Show friend view or user depending whether those users are friends.
		if (friendsView) {
			return FRIEND_VIEW_TYPE;
		} else {
			return USER_VIEW_TYPE;
		}
	}

	public void removeFriend(int adapterPosition) {
		users.remove(adapterPosition);
		notifyItemRemoved(adapterPosition);

		if (adapterPosition > 0) {
			notifyItemChanged(adapterPosition - 1);
		}
	}
}
