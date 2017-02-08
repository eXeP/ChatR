package exep.fi.chatr.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by pietu on 2/3/17.
 */

public class ServerOutgoingMessage {
    private String username;
    private String displayname;
    private String content;
    private Long timestamp;
    private Long chatId;
    private Long messageId;

    public ServerOutgoingMessage(String username, String displayname, String content, Long timestamp, Long chatId, Long messageId) {
        this.username = username;
        this.displayname = displayname;
        this.content = content;
        this.timestamp = timestamp;
        this.chatId = chatId;
        this.messageId = messageId;
    }

    public ServerOutgoingMessage(JSONObject obj) throws JSONException {
        this.username = obj.getString("username");
        this.displayname = obj.getString("displayname");
        this.content = obj.getString("content");
        this.timestamp = obj.getLong("timestamp");
        this.chatId = obj.getLong("chatId");
        this.messageId = obj.getLong("messageId");
    }

    public static ArrayList<ServerOutgoingMessage> fromJSON(JSONArray arr) throws JSONException {
        ArrayList<ServerOutgoingMessage> messages = new ArrayList<ServerOutgoingMessage>();
        for(int i = arr.length()-1; i >= 0; --i) {
            JSONObject obj = arr.getJSONObject(i);
            ServerOutgoingMessage msg = new ServerOutgoingMessage(obj);
            messages.add(msg);
        }
        return messages;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

}
