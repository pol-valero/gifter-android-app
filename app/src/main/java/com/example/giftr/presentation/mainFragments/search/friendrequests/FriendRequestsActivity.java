package com.example.giftr.presentation.mainFragments.search.friendrequests;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.android.volley.VolleyError;
import com.example.giftr.R;
import com.example.giftr.business.entities.User;
import com.example.giftr.persistence.FriendDAO;
import com.example.giftr.persistence.swaggerApi.SwaggerFriendDAO;
import com.example.giftr.presentation.mainFragments.search.friendrequests.recyclerview.RequestAdapter;

import java.util.List;

public class FriendRequestsActivity extends AppCompatActivity {

    private List<User> friendRequests;
    private RecyclerView requestsRecyclerView;
    private RequestAdapter requestsAdapter;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);

        Bundle args = getIntent().getExtras();
        if (args != null) {
            user = (User) args.getSerializable(User.LOGGED_USER);
        }

        requestsRecyclerView = findViewById(R.id.friendrequests_rvFriendRequests);
        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        getFriendsRequests();
    }

    private void getFriendsRequests() {
        FriendDAO friendDAO = new SwaggerFriendDAO();
        friendDAO.getAllRequests(user.getAccessToken(), FriendRequestsActivity.this, new FriendDAO.FriendResponseCallback() {
            @Override
            public void onSuccess(List<User> response) {
                friendRequests = response;

                requestsAdapter = new RequestAdapter(friendRequests, FriendRequestsActivity.this, user, getApplicationContext());
                requestsRecyclerView.setAdapter(requestsAdapter);
                updateUI();
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateUI() {
        requestsAdapter.notifyDataSetChanged();
    }
}