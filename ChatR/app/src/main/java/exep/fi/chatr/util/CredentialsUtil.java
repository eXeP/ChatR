package exep.fi.chatr.util;

import android.app.Application;
import android.content.SharedPreferences;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import exep.fi.chatr.ChatRApplication;
import exep.fi.chatr.model.Credentials;

/**
 * Created by pietu on 2/1/17.
 */

public class CredentialsUtil {

    private Credentials credentials;

    private static final String PREFS_NAME = "CREDENTIALS_PREFS";
    private static final String DOES_NOT_EXIST = "DOESNTEXIST@)(#&()@&#@)(#&()@#&()#&@()(#@&#((@#(";

    private ChatRApplication application;

    private boolean credentialsExist = false;

    public CredentialsUtil(ChatRApplication application) {
        this.application = application;
        loadCredentials();
    }

    public void login(final AsyncHttpResponseHandler handler) {
        loadCredentials();
        if(!credentialsExist)
            handler.onCancel();
        application.getChatApiUtil().login(credentials.getUsername(), credentials.getPassword(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject resp = new JSONObject(new String(responseBody));
                    credentials.setToken(resp.getString("token"));
                    credentials.setDisplayname(resp.getString("displayname"));
                    credentials.setUsername(resp.getString("username"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                handler.onSuccess(statusCode, headers, responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                handler.onFailure(statusCode, headers, responseBody, error);
            }
        });
    }

    public void register(final AsyncHttpResponseHandler handler) {
        loadCredentials();
        if(!credentialsExist)
            handler.onCancel();
        application.getChatApiUtil().register(credentials.getUsername(), credentials.getPassword(), handler);
    }

    public void setCredentials(String username, String password) {
        SharedPreferences settings = application.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.commit();
    }

    public void eraseCredentials() {
        SharedPreferences settings = application.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("username", DOES_NOT_EXIST);
        editor.putString("password", DOES_NOT_EXIST);
        editor.commit();
    }

    private void loadCredentials() {
        SharedPreferences settings = application.getSharedPreferences(PREFS_NAME, 0);
        String username = settings.getString("username", DOES_NOT_EXIST);
        String password = settings.getString("password", DOES_NOT_EXIST);
        credentials = new Credentials(username, password);
        credentialsExist = !username.equals(DOES_NOT_EXIST);
    }

    public boolean hasCredentials() {
        return credentialsExist;
    }

    public Credentials getCredentials() {
        return credentials;
    }
}
