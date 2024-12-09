package com.example.giftr.presentation.mainFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.android.volley.VolleyError;
import com.example.giftr.R;
import com.example.giftr.business.FriendMessagePair;
import com.example.giftr.business.entities.Message;
import com.example.giftr.business.entities.User;
import com.example.giftr.persistence.MessageDAO;
import com.example.giftr.persistence.UserDAO;
import com.example.giftr.persistence.requester.JsonRequester;
import com.example.giftr.persistence.swaggerApi.SwaggerMessageDAO;
import com.example.giftr.presentation.MainMenuActivity;
import com.example.giftr.presentation.mainFragments.messages.MessageAdapter;
import com.example.giftr.presentation.mainFragments.profile.friendsList.FriendsActivity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class MessagesFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<User> friends;
    private List<FriendMessagePair> friendMessagePairs;
    private ImageButton bCreateChat;
    private User loggedUser;
    public static String FRAGMENT_TAG = "MESSAGES_FRAGMENT";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoggedUser();
        setupMenuArgs();
    }

    private void getLoggedUser() {
        Bundle args = getArguments();
        if (args != null) {
            loggedUser = (User) args.getSerializable(User.LOGGED_USER);
        }
    }

    private void setupMenuArgs() {
        Bundle args;

        if (getArguments() != null) {
            args = getArguments();
        } else {
            args = new Bundle();
        }

        args.putInt(MainMenuActivity.FRAGMENT_ID_TAG, R.id.nav_messages);
        this.setArguments(args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_messages, container, false);

        setupView();
        setupListeners();

        return view;
    }

    private void setupListeners() {
        bCreateChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFriends();
            }
        });
    }

    private void showFriends() {
        Bundle args = new Bundle();
        args.putSerializable(User.FRIEND_NAME_TAG, loggedUser);

        MainMenuActivity mainMenuActivity = (MainMenuActivity) getActivity();
        Objects.requireNonNull(mainMenuActivity).changeActivity(FriendsActivity.class, args);
     }

    private void setupView() {
        recyclerView = view.findViewById(R.id.messages_rvRecentMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        bCreateChat = (ImageButton) view.findViewById(R.id.messages_bCreateChat);
    }

    private void getUsers() {
        if (!isAdded() && !isRemoving()) {
            return; // Fragment is not attached, exit the method
        }

        MessageDAO messageDAO = new SwaggerMessageDAO();
        messageDAO.getUsersMessaging(loggedUser.getAccessToken(), requireContext().getApplicationContext(), new UserDAO.UserListResponseCallback() {
            @Override
            public void onSuccess(List<User> response) {
                friends = response;
                getMessages();
            }

            @Override
            public void onError(VolleyError error) {
                // Handle error
            }
        });
    }

    private void getMessages() {
        friendMessagePairs = new ArrayList<>();

        // For each friend, get their latest message
        if (!friends.isEmpty()) {
            getLatestMessage(friends.get(0));
        }
    }

    private void getLatestMessage(User friend) {
        if (!isAdded() && !isRemoving()) {
            return; // Fragment is not attached, exit the method
        }

        MessageDAO messageDAO = new SwaggerMessageDAO();
        messageDAO.getMessagesWithUser(friend.getId(), loggedUser.getAccessToken(), requireContext().getApplicationContext(), new MessageDAO.MessageResponseCallback() {
            @Override
            public void onSuccess(List<Message> response) {
                // Add the latest message to the list of FriendMessagePair
                if (!response.isEmpty()) {
                    Message lastMessage = response.get(response.size() - 1);
                    friendMessagePairs.add(new FriendMessagePair(friend, lastMessage));
                }
                friends.remove(0);

                // Check if all messages have been retrieved
                if (friends.size() == 0) {
                    // Sort the friendMessagePairs based on the most recent message
                    orderMostRecentMessages(friendMessagePairs);
                    setupRecyclerView();
                }
                else {
                    getLatestMessage(friends.get(0));
                }
            }

            @Override
            public void onError(VolleyError error) {
                // Handle error
            }
        });
    }


    private void setupRecyclerView() {
        messageAdapter = new MessageAdapter(friendMessagePairs, getActivity());
        recyclerView.setAdapter(messageAdapter);
    }

    private void orderMostRecentMessages(List<FriendMessagePair> friendMessagePairs) {
        // Create a custom Comparator based on isOlderThan function
        Comparator<FriendMessagePair> pairComparator = (pair1, pair2) ->
                pair2.getMessage().isOlderThan(pair1.getMessage()) ? -1 :
                        pair1.getMessage().isOlderThan(pair2.getMessage()) ? 1 : 0;

        friendMessagePairs.sort(pairComparator.reversed());
    }

    // Reload the fragment when it's loaded from a back button.
    @Override
    public void onResume() {
        super.onResume();

        // Load the new messages again.
        getUsers();
    }

    @Override
    public void onPause() {
        super.onPause();
        JsonRequester.cancelRequest();
    }

}