package com.example.giftr.presentation.mainFragments.profile.friendsList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.VolleyError;
import com.example.giftr.R;
import com.example.giftr.business.SharedUser;
import com.example.giftr.business.entities.User;
import com.example.giftr.persistence.UserDAO;
import com.example.giftr.persistence.swaggerApi.SwaggerUserDAO;
import com.example.giftr.presentation.mainFragments.search.usersList.UsersAdapter;

import java.util.List;

public class FriendsActivity extends AppCompatActivity {

    private User friend;
    private RecyclerView recyclerView;
    private UsersAdapter usersAdapter;
    private List<User> friends;
    private SharedUser sharedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        getUser();
        getFriend();
        setupView();
        loadFriends();
    }

    private void getFriend() {
        Bundle args = getIntent().getExtras();
        if (args != null) {
            friend = (User) args.getSerializable(User.FRIEND_NAME_TAG);
        }
    }

    private void loadFriends() {
        UserDAO userDAO = new SwaggerUserDAO();
        userDAO.getUserFriendsById(friend.getId(), sharedUser.getUserAccessToken(), getApplicationContext(), new UserDAO.UserListResponseCallback() {
            @Override
            public void onSuccess(List<User> response) {
                friends = response;
                setupAdapter();
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }

    private void setupAdapter() {
        usersAdapter = new UsersAdapter(friends, this, sharedUser.getLoggedUser(), sharedUser.getLoggedUser().sameUser(friend));
        recyclerView.setAdapter(usersAdapter);
    }

    private void setupView() {
        recyclerView = findViewById(R.id.friends_rvFriendsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getUser() {
        this.sharedUser = SharedUser.getInstance(getApplicationContext());
    }

    public void changeActivity(Class<? extends AppCompatActivity> newActivity, Bundle args) {
        Intent intent = new Intent(FriendsActivity.this, newActivity);
        intent.putExtra(User.LOGGED_USER, sharedUser.getLoggedUser());

        if (args != null) {
            intent.putExtras(args);
        }

        startActivityForResult(intent, 2);
    }
}