package com.example.giftr.presentation.mainFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.giftr.presentation.FullScreenActivity;
import com.example.giftr.R;
import com.example.giftr.business.SharedUser;
import com.example.giftr.business.entities.Gift;
import com.example.giftr.business.entities.User;
import com.example.giftr.business.entities.Wishlist;
import com.example.giftr.persistence.FriendDAO;
import com.example.giftr.persistence.GiftDAO;
import com.example.giftr.persistence.UserDAO;
import com.example.giftr.persistence.WishlistDAO;
import com.example.giftr.persistence.requester.JsonRequester;
import com.example.giftr.persistence.swaggerApi.SwaggerFriendDAO;
import com.example.giftr.persistence.swaggerApi.SwaggerUserDAO;
import com.example.giftr.persistence.swaggerApi.SwaggerWishlistDAO;
import com.example.giftr.presentation.EditProfileActivity;
import com.example.giftr.presentation.LoginActivity;
import com.example.giftr.presentation.MainMenuActivity;
import com.example.giftr.presentation.mainFragments.home.WishlistAdapter;
import com.example.giftr.presentation.mainFragments.profile.friendsList.FriendsActivity;
import com.example.giftr.presentation.mainFragments.profile.giftList.SentGiftAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    private View view;
    private User loggedUser;
    private ImageView ivProfileImage;
    private ImageButton bLogOut;
    private ImageButton bEditProfile;
    private Button bFriendList;
    private TextView tvNumWishlists;
    private TextView tvNumFriends;
    private TextView tvNumReserves;
    private TextView tvUserName;
    private RecyclerView rvWishlists;
    private WishlistAdapter wishlistAdapter;
    private RecyclerView rvSentGifts;
    private SentGiftAdapter giftAdapter;
    public final static int ID_ACTIVITY = 3;
    private SharedUser sharedUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLoggedUser();
        setupMenuArgs();
    }

    private void getLoggedUser() {
        sharedUser = SharedUser.getInstance(requireContext().getApplicationContext());
        loggedUser = sharedUser.getLoggedUser();
    }

    private void setupMenuArgs() {
        Bundle args = new Bundle();
        args.putInt(MainMenuActivity.FRAGMENT_ID_TAG, R.id.nav_profile);
        this.setArguments(args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        setupView();
        setupListeners();

        return view;
    }

    private void loadFriends() {
        if (!isAdded() && !isRemoving()) {
            return; // Fragment is not attached, exit the method
        }

        FriendDAO friendDAO = new SwaggerFriendDAO();
        friendDAO.getAllFriends(loggedUser.getAccessToken(), getContext(), new FriendDAO.FriendResponseCallback() {
            @Override
            public void onSuccess(List<User> response) {
                tvNumFriends.setText(String.valueOf(response.size()));
            }

            @Override
            public void onError(VolleyError error) {
            }
        });
    }

    private void loadWishlists() {
        if (!isAdded() && !isRemoving()) {
            return; // Fragment is not attached, exit the method
        }

        WishlistDAO wishlistDAO = new SwaggerWishlistDAO();
        wishlistDAO.getWishlistsFromUser(loggedUser.getId(), loggedUser.getAccessToken(), getContext(), new WishlistDAO.WishlistListResponseCallback() {
            @Override
            public void onSuccess(List<Wishlist> response) {
                tvNumWishlists.setText(String.valueOf(response.size()));
                wishlistAdapter = new WishlistAdapter(response, getActivity(), loggedUser);
                rvWishlists.setAdapter(wishlistAdapter);
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }

    private void loadReserves() {
        if (!isAdded() && !isRemoving()) {
            return; // Fragment is not attached, exit the method
        }

        UserDAO userDAO = new SwaggerUserDAO();
        userDAO.getUserReservesById(loggedUser.getId(), loggedUser.getAccessToken(), getContext(), new GiftDAO.GiftListResponseCallback() {
            @Override
            public void onSuccess(List<Gift> response) {
                tvNumReserves.setText(String.valueOf(response.size()));
                giftAdapter = new SentGiftAdapter(response, getActivity(), loggedUser);
                rvSentGifts.setAdapter(giftAdapter);
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }

    private void setupView() {
        tvUserName = view.findViewById(R.id.profile_tvUserName);
        tvUserName.setText(loggedUser.getFullName());

        tvNumWishlists = view.findViewById(R.id.profile_tvNumWishlists);
        tvNumFriends = view.findViewById(R.id.profile_tvNumFriends);

        tvNumReserves = view.findViewById(R.id.profile_tvNumReserves);

        ivProfileImage = view.findViewById(R.id.profile_ivProfileImage);
        Picasso.get()
                .load(loggedUser.getImagePath())
                .into(ivProfileImage);

        ivProfileImage.setVisibility(View.VISIBLE);
        bLogOut = view.findViewById(R.id.profile_bLogOut);
        bEditProfile = view.findViewById(R.id.profile_bEditProfile);

        bFriendList = view.findViewById(R.id.profile_bFriends);
        bFriendList.setClickable(true);

        rvWishlists = view.findViewById(R.id.profile_rvWishlists);
        rvWishlists.setLayoutManager(new LinearLayoutManager(getContext()));

        rvSentGifts = view.findViewById(R.id.profile_rvSentGifts);
        rvSentGifts.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setupListeners() {
        bLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMainActivity();
            }
        });

        bEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainMenuActivity activity = (MainMenuActivity) getActivity();

                Objects.requireNonNull(activity).changeActivity(EditProfileActivity.class, null);
            }
        });

        bFriendList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainMenuActivity activity = (MainMenuActivity) getActivity();

                Bundle args = new Bundle();

                if (getArguments() != null) {
                    args = getArguments();
                }

                args.putSerializable(User.FRIEND_NAME_TAG, loggedUser);
                Objects.requireNonNull(activity).changeActivity(FriendsActivity.class, args);
             }
        });

        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageFullscreen();
            }
        });
    }

    private void showImageFullscreen() {
        Bundle args = new Bundle();
        args.putString(User.PROFILE_PICTURE_TAG, sharedUser.getLoggedUser().getImagePath());
        args.putSerializable(User.FRIEND_NAME_TAG, sharedUser.getLoggedUser());

        MainMenuActivity mainMenuActivity = (MainMenuActivity) getActivity();
        Objects.requireNonNull(mainMenuActivity).changeActivity(FullScreenActivity.class, args);
    }

    private void changeMainActivity() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.putExtra(LoginActivity.ACTIVITY_SOURCE, "anotherActivity");
        JsonRequester.cancelRequest();

        startActivityForResult(intent, ID_ACTIVITY);
    }

    @Override
    public void onResume() {
        super.onResume();

        loadFriends();
        loadWishlists();
        loadReserves();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}