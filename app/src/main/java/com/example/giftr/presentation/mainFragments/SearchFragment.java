package com.example.giftr.presentation.mainFragments;

import android.os.Bundle;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.VolleyError;
import com.example.giftr.R;
import com.example.giftr.business.entities.User;
import com.example.giftr.persistence.UserDAO;
import com.example.giftr.persistence.requester.JsonRequester;
import com.example.giftr.persistence.swaggerApi.SwaggerUserDAO;
import com.example.giftr.presentation.MainMenuActivity;
import com.example.giftr.presentation.mainFragments.search.friendrequests.FriendRequestsActivity;
import com.example.giftr.presentation.mainFragments.search.usersList.UsersAdapter;

import java.util.List;
import java.util.Objects;

public class SearchFragment extends Fragment {

	private View view;
	private User user;
	private ImageButton bFriendRequests;
	private EditText etSearchFilter;
	private RecyclerView recyclerView;
	private UsersAdapter usersAdapter;
	private List<User> users;
	public static String FRAGMENT_TAG = "SEARCH_FRAGMENT";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		user = (User) getArguments().getSerializable(User.LOGGED_USER);
		setupMenuArgs();
	}

	private void setupMenuArgs() {
		Bundle args = new Bundle();
		args.putInt(MainMenuActivity.FRAGMENT_ID_TAG, R.id.nav_search);
		this.setArguments(args);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		view = inflater.inflate(R.layout.fragment_search, container, false);

		setupView();
		setupListeners();
		updateUI();

		return view;
	}

	private void setupView() {
		bFriendRequests = view.findViewById(R.id.search_bFriendRequests);
		etSearchFilter = view.findViewById(R.id.search_etSearchFilter);
		etSearchFilter.setAnimation(null);
		etSearchFilter.setInputType(InputType.TYPE_CLASS_TEXT);
		etSearchFilter.setImeOptions(EditorInfo.IME_ACTION_NONE);
		recyclerView = view.findViewById(R.id.search_rvUsers);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
	}



	private void setupListeners() {
		bFriendRequests.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//                changeFragment();
				// Get a reference to the MainMenuActivity containing this fragment
				MainMenuActivity mainMenuActivity = (MainMenuActivity) getActivity();

				// Call the replaceFragment method on the MainMenuActivity
				Objects.requireNonNull(mainMenuActivity).changeActivity(FriendRequestsActivity.class, null);
			}
		});

		etSearchFilter.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				Log.i("asd", "onTextChanged:" + s.toString());
				applyFilter(s.toString());
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	public void applyFilter(String filter) {
		if (!isAdded() && !isRemoving()) {
			return; // Fragment is not attached, exit the method
		}

		UserDAO userDAO = new SwaggerUserDAO();
		userDAO.searchUser(filter, user.getAccessToken(), requireContext().getApplicationContext(), new UserDAO.UserListResponseCallback() {
			@Override
			public void onSuccess(List<User> response) {
				usersAdapter.setFilter(response);
			}

			@Override
			public void onError(VolleyError error) {

			}
		});
	}

	private void updateUI() {
		getAllUsers();
	}

	private void getAllUsers() {
		if (!isAdded() && !isRemoving()) {
			return; // Fragment is not attached, exit the method
		}

		UserDAO userDAO = new SwaggerUserDAO();
		userDAO.getAllUsers(user.getAccessToken(), requireContext().getApplicationContext(), new UserDAO.UserListResponseCallback() {
			@Override
			public void onSuccess(List<User> response) {
				users = response;
				setupAdapter(response);
			}

			@Override
			public void onError(VolleyError error) {

			}
		});
	}

	private void setupAdapter(List<User> users) {
		usersAdapter = new UsersAdapter(users, getActivity(), user, false);
		recyclerView.setAdapter(usersAdapter);
	}

	@Override
	public void onPause() {
		super.onPause();
		JsonRequester.cancelRequest();
	}
}