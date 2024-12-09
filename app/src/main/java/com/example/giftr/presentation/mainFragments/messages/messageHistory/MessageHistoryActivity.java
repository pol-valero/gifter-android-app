package com.example.giftr.presentation.mainFragments.messages.messageHistory;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.content.Context;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.giftr.R;
import com.example.giftr.business.ImageViewLoader;
import com.example.giftr.business.SentenceSplitter;
import com.example.giftr.business.entities.Message;
import com.example.giftr.business.entities.User;
import com.example.giftr.persistence.ApiResponseCallback;
import com.example.giftr.persistence.MessageDAO;
import com.example.giftr.persistence.swaggerApi.SwaggerMessageDAO;
import com.example.giftr.presentation.MainMenuActivity;
import com.example.giftr.presentation.mainFragments.messages.messageHistory.messageList.MsgHistoryAdapter;
import com.example.giftr.presentation.mainFragments.profile.FriendProfileActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MsgHistoryAdapter msgHistoryAdapter;
    private List<Message> messagesHistory;
    private User loggedUser;
    private User friend;
    private TextView tvUserName;
    private ImageView ivProfilePicture;
    private EditText etWrittenMessage;
    private ImageButton bSendMessage;
    private List<String> messageToSend;
    private final static int MAX_MESSAGES_WITH_SCROLL = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_history);

        getLoggedUser();
        getFriend();
        setupView();
        loadMessages();
        setupListeners();
    }

    private void setupView() {
        tvUserName = findViewById(R.id.msgHistory_tvUserName);
        tvUserName.setText(friend.getFullName());

        ivProfilePicture = findViewById(R.id.msgHistory_ivProfilePicture);
        ImageViewLoader imageViewLoader = new ImageViewLoader(ivProfilePicture);
        imageViewLoader.loadImage(friend.getImagePath());

        etWrittenMessage = findViewById(R.id.msgHistory_etWriteMessage);
        bSendMessage = findViewById(R.id.msgHistory_bSendMessage);

        recyclerView = findViewById(R.id.msgHistory_rvMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        etWrittenMessage.clearFocus();
    }

    private void setupListeners() {
        // Obtain the root view of the layout
        View rootView = findViewById(android.R.id.content);

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                rootView.getWindowVisibleDisplayFrame(rect);

                int screenHeight = rootView.getRootView().getHeight();
                int keypadHeight = screenHeight - rect.bottom;

                if (keypadHeight > screenHeight * 0.15) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scrollRecyclerView();
                        }
                    }, 100); // Delay the scroll operation for 200 milliseconds
                }
            }
        });

        // Request focus when it's clicked (necessary to avoid it being focused when launching the app).
        etWrittenMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etWrittenMessage.requestFocus();
            }
        });

        // Remove the focus when pressing ENTER key.
        etWrittenMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int act, KeyEvent event) {
                if (act == EditorInfo.IME_ACTION_DONE) {
                    etWrittenMessage.clearFocus();
                    hideKeyboard();
                }
                return false;
            }
        });

        ivProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args;

                if (getIntent().getExtras() != null) {
                    args = getIntent().getExtras();
                }
                else {
                    args = new Bundle();
                }
                args.putSerializable(User.FRIEND_NAME_TAG, friend);

                changeActivity(FriendProfileActivity.class, args);
            }
        });

        bSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupMessage();
            }
        });
    }

    public void changeActivity(Class<? extends AppCompatActivity> targetActivity, Bundle extraArgs) {
        Intent intent = new Intent(MessageHistoryActivity.this, targetActivity);
        intent.putExtra(User.LOGGED_USER, loggedUser);

        if (extraArgs != null) {
            intent.putExtras(extraArgs);
        }

        startActivityForResult(intent, 1);
    }

    private void setupMessage() {
        String content = etWrittenMessage.getText().toString();
        int senderID = loggedUser.getId();
        int receiverID = friend.getId();

        String[] splitSentences = SentenceSplitter.splitSentence(content, 45);
        messageToSend = new ArrayList<>(Arrays.asList(splitSentences));
        sendMessage(messageToSend.get(0), senderID, receiverID);
    }

    private void sendMessage(String content, int senderID, int receiverID) {
        Message message = new Message(content, senderID, receiverID);
        MessageDAO messageDAO = new SwaggerMessageDAO();

        messageDAO.sendMessage(message, loggedUser.getAccessToken(), this, new ApiResponseCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                // Remove the first message sent and check if there are more to be sent.
                messageToSend.remove(0);
                if (messageToSend.size() != 0) {
                    sendMessage(messageToSend.get(0), senderID, receiverID);
                } else {
                    refreshHistory();
                }
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }

    private void refreshHistory() {
        etWrittenMessage.setText("");
        etWrittenMessage.clearFocus();

        hideKeyboard();

        MessageDAO messageDAO = new SwaggerMessageDAO();
        messageDAO.getMessagesWithUser(friend.getId(), loggedUser.getAccessToken(), this, new MessageDAO.MessageResponseCallback() {
            @Override
            public void onSuccess(List<Message> response) {
                msgHistoryAdapter.updateMessages(response);
                scrollLastMessage();
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etWrittenMessage.getWindowToken(), 0); // Hide the keyboard
    }

    private void getFriend() {
        Bundle args = getIntent().getExtras();
        if (args != null) {
            friend = (User) args.getSerializable(User.FRIEND_NAME_TAG);
        }
    }

    private void getLoggedUser() {
        Bundle args = getIntent().getExtras();
        if (args != null) {
            loggedUser = (User) args.getSerializable(User.LOGGED_USER);
        }
    }

    private void loadMessages() {
        MessageDAO messageDAO = new SwaggerMessageDAO();
        messageDAO.getMessagesWithUser(friend.getId(), loggedUser.getAccessToken(), this, new MessageDAO.MessageResponseCallback() {
            @Override
            public void onSuccess(List<Message> response) {
                messagesHistory = response;
                setupRecyclerView();
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }

    private void setupRecyclerView() {
        msgHistoryAdapter = new MsgHistoryAdapter(messagesHistory, this);
        recyclerView.setAdapter(msgHistoryAdapter);

        scrollLastMessage();
    }

    private void scrollLastMessage() {
        recyclerView.setVisibility(View.GONE);

        // Check when all the messages have been visible so that the view can scroll to the last one.
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                scrollRecyclerView();

                // Unregister the listener to avoid multiple calls
                recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void scrollRecyclerView() {
        // Scroll the RecyclerView to the last item
        int itemCount = recyclerView.getAdapter().getItemCount();

        // Check if there are any current messages, so that the recycler view can scroll.
        if (itemCount > 0) {
            if (itemCount > MAX_MESSAGES_WITH_SCROLL) {
                recyclerView.scrollToPosition(itemCount - 1);
            }
            else {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.smoothScrollToPosition(itemCount - 1);
            }

            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
