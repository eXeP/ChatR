package fi.exep.model;

public class OutgoingMessage {
    private String username;
    private String displayname;
    private String content;
    private Long timestamp;
    private Long chatId;
    private Long messageId;
    
    public OutgoingMessage(String username, String displayname, String content, Long timestamp, Long chatId, Long messageId) {
        this.username = username;
        this.displayname = displayname;
        this.content = content;
        this.timestamp = timestamp;
        this.chatId = chatId;
        this.messageId = messageId;
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
