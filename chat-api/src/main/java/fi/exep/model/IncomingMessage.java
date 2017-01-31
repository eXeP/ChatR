/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.exep.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.exep.db.UserRepository;

/**
 *
 * @author root
 */

@JsonIgnoreProperties({"userId"})
public class IncomingMessage {
    private String token;
    private Long chatId;
    private String content;   
    
    private Long userId;
    
    public IncomingMessage(){}
    
    public IncomingMessage(String token, String content) {
        this.token = token;
        this.content = content;
    }
    
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
    
}
