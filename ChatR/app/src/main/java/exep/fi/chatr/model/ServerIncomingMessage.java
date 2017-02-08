package exep.fi.chatr.model;

/**
 * Created by pietu on 2/4/17.
 */

public class ServerIncomingMessage {
    private Long chatId;
    private String content;

    public ServerIncomingMessage(){}

    public ServerIncomingMessage(Long chatId, String content) {
        this.chatId = chatId;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

}