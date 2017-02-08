package exep.fi.chatr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;
import exep.fi.chatr.model.Chat;
import exep.fi.chatr.model.ServerIncomingMessage;
import exep.fi.chatr.model.ServerOutgoingMessage;
import exep.fi.chatr.ui.MessageAdapter;
import exep.fi.chatr.util.ChatApiUtil;
import exep.fi.chatr.util.CredentialsUtil;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private static final int CHAT_GROUP_ID = 42;
    private static final int PRIVATE_GROUP_ID = 1337;

    NavigationView navigationView;
    SubMenu roomMenu;
    SubMenu privateMenu;

    ChatRApplication application;

    ChatApiUtil chatApiUtil;
    CredentialsUtil credentialsUtil;
    ListView messageList;
    ImageButton sendButton;
    EditText messageText;
    TextView navUserText;

    MessageAdapter messageAdapter;

    private Long currentChatId = -1L;

    private ArrayList<ServerOutgoingMessage> currentMessages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("ChatR");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        messageList = (ListView) findViewById(R.id.message_list);
        sendButton = (ImageButton) findViewById(R.id.sendButton);
        messageText = (EditText) findViewById(R.id.messageEdit);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navUserText = (TextView) findViewById(R.id.navUserText);

        navigationView.setNavigationItemSelectedListener(this);
        privateMenu = navigationView.getMenu().addSubMenu("People");
        roomMenu = navigationView.getMenu().addSubMenu("Rooms");

        application = (ChatRApplication) getApplication();
        credentialsUtil = application.getCredentialsUtil();
        chatApiUtil = application.getChatApiUtil();

        messageAdapter = new MessageAdapter(application);
        messageList.setAdapter(messageAdapter);
        //navUserText.setText(credentialsUtil.getCredentials().getUsername());

        setChatUpdaters();
        setupButtonListeners();
    }

    private void setupButtonListeners() {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String message = messageText.getText().toString().trim();
                if (message.length() > 0) {
                    //messageList.getAdapter().
                    chatApiUtil.sendMessage(new ServerIncomingMessage(currentChatId, message), new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                            scrollToMessageListBottom();
                            Log.d(TAG, "RESP " + new String(responseBody));
                            messageText.setText("");
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Snackbar.make(messageList, "Sending message failed.", Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();
                        }
                    });
                }

            }
        });
    }

    public void setNavigationMenuChats(ArrayList<Chat> chats) {
        roomMenu.clear();
        privateMenu.clear();
        for(Chat chat: chats) {
            if (currentChatId == -1L) {
                changeMainChat(chat.getChatId());
            }
            if(chat.isPrivate())
                privateMenu.add(PRIVATE_GROUP_ID, chat.getChatId().intValue(), 1, chat.getName());
            else
                roomMenu.add(CHAT_GROUP_ID, chat.getChatId().intValue(), 1, chat.getName());
        }
    }



    private void changeMainChat(Long chatId) {
        messageAdapter.setMessages(new ArrayList<ServerOutgoingMessage>());
        currentChatId = chatId;
        final Long loadingChatId = currentChatId;
        chatApiUtil.getNewestChatMessages(currentChatId, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    if (loadingChatId != currentChatId) {
                        return;
                    }
                    ArrayList<ServerOutgoingMessage> messages =  ServerOutgoingMessage.fromJSON(new JSONArray(new String(responseBody)));
                    currentMessages = messages;
                    messageAdapter.setMessages(messages);
                    scrollToMessageListBottom();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(TAG, "MAIN CHAT fail " + new String(responseBody));
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        updateChatList();
    }


    private void scrollToMessageListBottom() {
        messageList.setSelection(messageAdapter.getCount());
    }


    private void setChatUpdaters() {
        setChatListUpdater();
        setMainChatUpdater();

        messageList.setOnScrollListener(new AbsListView.OnScrollListener() {
            Long lastLoad = 0L;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                final Long loadingChatId = currentChatId;
                if (System.currentTimeMillis() - lastLoad > 1500 && firstVisibleItem <= 5) {
                    lastLoad = System.currentTimeMillis();
                    Long messageId = messageAdapter.getCount() == 0 ? 0 : messageAdapter.getItem(0).getMessageId();
                    chatApiUtil.getChatMessagesBefore(currentChatId, messageId, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if (currentChatId == loadingChatId) {
                                try {
                                    ArrayList<ServerOutgoingMessage> newMessages = ServerOutgoingMessage.fromJSON(new JSONArray(new String(responseBody)));
                                    final int positionToSave = messageList.getFirstVisiblePosition() + newMessages.size();
                                    messageAdapter.addToTop(newMessages);
                                    messageList.post(new Runnable() {

                                        @Override
                                        public void run() {
                                            messageList.setSelection(positionToSave);
                                        }
                                    });
                                    messageList.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                                        @Override
                                        public boolean onPreDraw() {
                                            if(messageList.getFirstVisiblePosition() == positionToSave) {
                                                messageList.getViewTreeObserver().removeOnPreDrawListener(this);
                                                return true;
                                            }
                                            else {
                                                return false;
                                            }
                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        }
                    });
                }
            }
        });
    }

    private void setChatListUpdater() {

        final Handler handler = new Handler();
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        updateChatList();
                    }
                });
            }
        };
        timer.schedule(task, 0, 20*1000);
    }

    private void updateChatList() {
        try {
            chatApiUtil.getUserChats(new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        ArrayList<Chat> chats = Chat.fromJSON(new JSONArray(new String(responseBody)));
                        setNavigationMenuChats(chats);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.d(TAG, statusCode + " " + new String(responseBody));
                }
            });
        } catch (Exception e) {
            // error, do something
        }
    }

    private void setMainChatUpdater() {

        final Handler handler = new Handler();
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        final Long loadingChatId = currentChatId;
                        Log.d(TAG, "MAIN CHAT UPDATE START");
                        if (currentChatId != -1) {

                            try {
                                Long lastMessageId = currentMessages == null ? 0 : currentMessages.size() == 0 ? 0 : currentMessages.get(currentMessages.size() - 1).getMessageId();
                                chatApiUtil.getChatMessagesAfter(currentChatId, lastMessageId, new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                        Log.d(TAG, "MAIN CHAT UPDATE SUCCESS " + new String(responseBody));
                                        try {
                                            if (loadingChatId != currentChatId) {
                                                return;
                                            }
                                            ArrayList<ServerOutgoingMessage> messages =  ServerOutgoingMessage.fromJSON(new JSONArray(new String(responseBody)));
                                            if (messages.size() > 0) {
                                                boolean scrollToBottom = false;
                                                if (messageList.getCount() > 0 && messageList.getLastVisiblePosition() == messageAdapter.getCount() - 1 && messageList.getChildAt(messageList.getChildCount() -1 ).getBottom() <= messageList.getHeight()) {
                                                    scrollToBottom = true;
                                                }
                                                currentMessages.addAll(messages);
                                                messageAdapter.addToBottom(messages);
                                                if (scrollToBottom) {
                                                    scrollToMessageListBottom();
                                                }
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                        //Log.d(TAG, "MAIN CHAT FAIL " + statusCode + " " + new String(responseBody));
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d(TAG, "Error on main chat update");
                            }
                        }
                    }
                });
            }
        };
        timer.schedule(task, 0, 1*1000);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createChatDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Create chat");
        alert.setMessage("Enter chat name");

        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String chatName = input.getEditableText().toString();
                chatApiUtil.createChat(chatName, false, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Toast.makeText(application.getApplicationContext(), "Chat created successfully", Toast.LENGTH_SHORT).show();
                        updateChatList();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(application.getApplicationContext(), "Failed to create chat", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, statusCode + " " );
                    }
                });
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.nav_logout) {
            credentialsUtil.eraseCredentials();
            goToLoginActivity();
        } else if (id == R.id.nav_search) {
            goToSearchChatsActivity();
        } else if(id == R.id.nav_search_users) {
            goToSearchUsersActivity();
        } else if(id == R.id.nav_create_chat) {
            createChatDialog();
        } else if(item.getGroupId() == CHAT_GROUP_ID) {
            changeMainChat(new Long(id));
        } else if(item.getGroupId() == PRIVATE_GROUP_ID) {
            changeMainChat(new Long(id));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void goToSearchUsersActivity() {
        Intent intent = new Intent(this, SearchUsersActivity.class);
        startActivity(intent);
    }

    private void goToSearchChatsActivity() {
        Intent intent = new Intent(this, SearchChatsActivity.class);
        startActivity(intent);
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
