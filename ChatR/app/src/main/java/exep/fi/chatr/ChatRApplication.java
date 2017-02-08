package exep.fi.chatr;

import android.app.Application;
import android.util.Log;

import exep.fi.chatr.util.ChatApiUtil;
import exep.fi.chatr.util.CredentialsUtil;

/**
 * Created by pietu on 2/1/17.
 */

public class ChatRApplication extends Application {

    private CredentialsUtil credentialsUtil;
    private ChatApiUtil chatApiUtil;

    @Override
    public void onCreate() {
        credentialsUtil = new CredentialsUtil(this);
        chatApiUtil = new ChatApiUtil(this);
    }

    public CredentialsUtil getCredentialsUtil() {
        return credentialsUtil;
    }

    public ChatApiUtil getChatApiUtil() {
        return chatApiUtil;
    }
}
