package com.example.giftr.presentation.mainFragments;

import android.os.Bundle;

import android.widget.ImageButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.example.giftr.R;
import com.example.giftr.business.SharedUser;
import com.example.giftr.business.entities.User;
import com.example.giftr.business.entities.Wishlist;
import com.example.giftr.persistence.WishlistDAO;
import com.example.giftr.persistence.requester.JsonRequester;
import com.example.giftr.persistence.swaggerApi.SwaggerWishlistDAO;
import com.example.giftr.presentation.CreateWishlistActivity;
import com.example.giftr.presentation.MainMenuActivity;
import com.example.giftr.presentation.mainFragments.home.WishlistAdapter;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private List<Wishlist> wishlists = new LinkedList<>();
    private RecyclerView recyclerView;
    private WishlistAdapter wishlistAdapter;
    private ImageButton bCreateWishlist;
    public static String FRAGMENT_TAG = "HOME_FRAGMENT";
    private User loggedUser;
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
        args.putInt(MainMenuActivity.FRAGMENT_ID_TAG, R.id.nav_home);
        this.setArguments(args);
    }

    private void setupAdapter(List<Wishlist> wishlists) {
        wishlistAdapter = new WishlistAdapter(wishlists, getActivity(), loggedUser);
        recyclerView.setAdapter(wishlistAdapter);
    }

    private void getMyWishlists() {
        if (!isAdded() && !isRemoving()) {
            return; // Fragment is not attached, exit the method
        }

        WishlistDAO wishlistDAO = new SwaggerWishlistDAO();

        wishlistDAO.getWishlistsFromUser(MainMenuActivity.loggedUser.getId(), MainMenuActivity.loggedUser.getAccessToken(), getContext(), new WishlistDAO.WishlistListResponseCallback() {
            @Override
            public void onSuccess(List<Wishlist> response) {
                wishlists = response;
                setupAdapter(wishlists);
            }

            @Override
            public void onError(VolleyError error) {
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.home_rvWhishlists);
        bCreateWishlist = (ImageButton) v.findViewById(R.id.home_bCreateWhishlist);

        bCreateWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainMenuActivity mainMenuActivity = (MainMenuActivity) getActivity();
                Objects.requireNonNull(mainMenuActivity).changeActivity(CreateWishlistActivity.class, null);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        getMyWishlists();
    }

    @Override
    public void onPause() {
        super.onPause();
        JsonRequester.cancelRequest();
    }
}