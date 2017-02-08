package exep.fi.chatr.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Chat {
    private String starter;
    private Long chatId;
    private String name;
    private Long started;

    private boolean isPrivate;

    public Chat(String starter, Long chatId, String name, Long started, boolean isPrivate) {
        this.starter = starter;
        this.chatId = chatId;
        this.name = name;
        this.started = started;
        this.isPrivate = isPrivate;
    }

    public Chat(JSONObject obj) throws JSONException {
        this.starter = obj.getString("starter");
        this.chatId = obj.getLong("chatId");
        this.name = obj.getString("name");
        this.started = obj.getLong("started");
        this.isPrivate = obj.getBoolean("isPrivate");
    }

    public static ArrayList<Chat> fromJSON(JSONArray arr) throws JSONException {
        ArrayList<Chat> chats = new ArrayList<Chat>();
        for(int i = 0; i < arr.length(); ++i){
            JSONObject obj = arr.getJSONObject(i);
            chats.add(new Chat(obj));
        }
        return chats;
    }


    public Long getStarted() {
        return started;
    }

    public void setStarted(Long started) {
        this.started = started;
    }

    public String getStarter() {
        return starter;
    }

    public void setStarter(String starter) {
        this.starter = starter;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

}
