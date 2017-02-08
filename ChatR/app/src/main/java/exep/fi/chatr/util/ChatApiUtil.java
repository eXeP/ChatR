package exep.fi.chatr.util;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HeaderElement;
import cz.msebera.android.httpclient.ParseException;
import cz.msebera.android.httpclient.entity.StringEntity;
import exep.fi.chatr.ChatRApplication;
import exep.fi.chatr.model.ServerIncomingMessage;

import static exep.fi.chatr.R.id.login;

/**
 * Created by pietu on 2/1/17.
 */

public class ChatApiUtil {

    private ChatRApplication application;
    private CredentialsUtil credentialsUtil;

    private static final String BASE_URL = "http://exep.tech:8080/chat-api/";

    private static final Integer PAGE_LENGTH = 25;

    public ChatApiUtil(ChatRApplication application) {
        this.application = application;
        credentialsUtil = application.getCredentialsUtil();
    }

    public void login(String username, String password, AsyncHttpResponseHandler handler) {
        HashMap<String, String> params = new HashMap<>();
        params.put("username", encodeUrl(username));
        params.put("password", password);
        getAsyncNoToken("user/login", params, handler);
    }

    public void register(String username, String password, AsyncHttpResponseHandler handler) {
        HashMap<String, String> params = new HashMap<>();
        params.put("username", encodeUrl(username));
        params.put("displayname", encodeUrl(username));
        params.put("password", password);
        getAsyncNoToken("user/register", params, handler);
    }

    public void getUserChats(AsyncHttpResponseHandler handler) {
        get("chat/list", new HashMap<String, String>(), handler);
    }

    public void getNewestChatMessages(Long chatId, AsyncHttpResponseHandler handler) {
        HashMap<String, String> params = new HashMap<>();
        params.put("chatId", chatId.toString());
        params.put("count", PAGE_LENGTH.toString());
        get("chat/fetch/newest", params, handler);
    }

    public void getChatMessagesAfter(Long chatId, Long messageId, AsyncHttpResponseHandler handler) {
        HashMap<String, String> params = new HashMap<>();
        params.put("chatId", chatId.toString());
        params.put("messageId", messageId.toString());
        get("chat/fetch/after", params, handler);
    }

    public void getChatMessagesBefore(Long chatId, Long messageId, AsyncHttpResponseHandler handler) {
        HashMap<String, String> params = new HashMap<>();
        params.put("chatId", chatId.toString());
        params.put("messageId", messageId.toString());
        params.put("count", PAGE_LENGTH.toString());
        get("chat/fetch/before", params, handler);
    }

    public void createChat(String name, boolean isPrivate, AsyncHttpResponseHandler handler) {
        HashMap<String, String> params = new HashMap<>();
        params.put("chatName", encodeUrl(name));
        params.put("private", isPrivate == true ? "true" : "false");
        post("chat/create?chatName="+params.get("chatName")+"&"+"private="+params.get("private"), params, handler);
    }

    public void joinChat(Long chatId, AsyncHttpResponseHandler handler) {
        HashMap<String, String> params = new HashMap<>();
        post("chat/join?chatId=" + chatId, params, handler);
    }

    public void createPrivateChat(String otherUser, AsyncHttpResponseHandler handler) {
        HashMap<String, String> params = new HashMap<>();
        params.put("chatName", encodeUrl(otherUser +  " and " + credentialsUtil.getCredentials().getUsername()));
        params.put("private", "true");
        params.put("otherUser", encodeUrl(otherUser));
        post("chat/create?chatName="+params.get("chatName")+"&"+"private=true"+"&"+"otherUser="+params.get("otherUser"), params, handler);
    }

    public void sendMessage(ServerIncomingMessage message, AsyncHttpResponseHandler handler) {
        JSONObject obj = new JSONObject();
        StringEntity entity = null;
        try {
            obj.put("chatId", message.getChatId());
            obj.put("content", message.getContent());
            entity = new StringEntity(obj.toString(), "UTF-8");
        } catch (Exception e) {
            handler.onCancel();
        }
        post("chat/send", entity, handler);
    }

    public void searchChats(String query, AsyncHttpResponseHandler handler) {
        HashMap<String, String> params = new HashMap<>();
        params.put("query", encodeUrl(query));
        get("chat/search", params, handler);
    }

    public void searchUsers(String query, AsyncHttpResponseHandler handler) {
        HashMap<String, String> params = new HashMap<>();
        params.put("query", encodeUrl(query));
        get("user/search", params, handler);
    }

    private void post(final String resource, final StringEntity body, final AsyncHttpResponseHandler handler){
        postAsync(resource, body, new AsyncHttpResponseHandler() {
            boolean hasFailed = false;
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                handler.onSuccess(statusCode, headers, responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 401 && hasFailed == false) {
                    hasFailed = true;
                    credentialsUtil.login(new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            postAsync(resource, body, this);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        }
                    });
                } else {
                    handler.onFailure(statusCode, headers, responseBody, error);
                }
            }
        });
    }


    private void post(final String resource, final HashMap<String, String> queryParams, final AsyncHttpResponseHandler handler){
        Log.d("LOL", "TAALLA " + resource);
        postAsync(resource, queryParams, new AsyncHttpResponseHandler() {
            boolean hasFailed = false;
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                handler.onSuccess(statusCode, headers, responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 401 && hasFailed == false) {
                    hasFailed = true;
                    credentialsUtil.login(new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            postAsync(resource, queryParams, this);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        }
                    });
                } else {
                    handler.onFailure(statusCode, headers, responseBody, error);
                }
            }
        });
    }


    private void get(final String resource, final HashMap<String, String> queryParams, final AsyncHttpResponseHandler handler){
        getAsync(resource, queryParams, new AsyncHttpResponseHandler() {
            boolean hasFailed = false;
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                handler.onSuccess(statusCode, headers, responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 401 && hasFailed == false) {
                    hasFailed = true;
                    credentialsUtil.login(new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            getAsync(resource, queryParams, this);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        }
                    });
                } else {
                    handler.onFailure(statusCode, headers, responseBody, error);
                }
            }
        });
    }

    private void getAsync(String resource, HashMap<String, String> queryParams, AsyncHttpResponseHandler handler){
        AsyncHttpClient client = new AsyncHttpClient();
        String url = BASE_URL + resource;
        client.addHeader("token", credentialsUtil.getCredentials().getToken());
        client.get(url, new RequestParams(queryParams), handler);
    }

    private void postAsync(String resource, HashMap<String, String> queryParams, AsyncHttpResponseHandler handler){
        AsyncHttpClient client = new AsyncHttpClient();
        String url = BASE_URL + resource;
        client.addHeader("token", credentialsUtil.getCredentials().getToken());
        client.post(url, new RequestParams(queryParams), handler);
    }

    private void postAsync(String resource, StringEntity body, AsyncHttpResponseHandler handler){
        AsyncHttpClient client = new AsyncHttpClient();
        String url = BASE_URL + resource;
        client.addHeader("token", credentialsUtil.getCredentials().getToken());
        client.post(application.getApplicationContext(), url, body, "application/json;charset=UTF-8", handler);
    }

    private void getAsyncNoToken(String resource, HashMap<String, String> queryParams, AsyncHttpResponseHandler handler){
        AsyncHttpClient client = new AsyncHttpClient();
        String url = BASE_URL + resource;
        client.get(url, new RequestParams(queryParams), handler);
    }

    private String encodeUrl(String url) {
        try {
            return URLEncoder.encode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }



}
