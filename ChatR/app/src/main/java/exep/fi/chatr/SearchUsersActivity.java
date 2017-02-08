package exep.fi.chatr;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import exep.fi.chatr.model.Chat;
import exep.fi.chatr.ui.ChatAdapter;
import exep.fi.chatr.ui.UserListAdapter;

public class SearchUsersActivity extends AppCompatActivity {

    SearchView searchView;
    ListView searchList;

    ChatRApplication application;

    UserListAdapter userListAdapter;

    private static final String TAG = "SearchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Search chats");

        application = (ChatRApplication) getApplication();

        searchView = (SearchView) findViewById(R.id.chatSearchView);
        searchList = (ListView) findViewById(R.id.searchList);

        userListAdapter = new UserListAdapter(application);
        searchList.setAdapter(userListAdapter);

        setListeners();
    }

    private void setListeners() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.trim();
                if (newText.length() <= 2) {
                    return false;
                }
                application.getChatApiUtil().searchUsers(newText, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            JSONArray arr = new JSONArray(new String(responseBody));
                            ArrayList<String> users = new ArrayList<String>();
                            for (int i = 0; i < arr.length(); ++i) {
                                String user = arr.getString(i);
                                if (application.getCredentialsUtil().getCredentials().getUsername().toLowerCase().compareTo(user.toLowerCase()) != 0) {
                                    users.add(user);
                                }
                            }
                            userListAdapter.setMessages(users);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                });
                return true;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                userListAdapter.clear();
                return false;
            }
        });

        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String user = userListAdapter.getItem(position);
                application.getChatApiUtil().createPrivateChat(user, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Toast.makeText(application.getApplicationContext(), "Private chat started succesfully", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.d(TAG, statusCode + " start fail " + new String(responseBody));
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
