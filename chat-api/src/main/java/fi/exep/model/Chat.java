/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.exep.model;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 *
 * @author root
 */

public class Chat {
    private String starter;
    private Long chatId;
    private String name;
    private Long started;
    
    private Boolean isPrivate;
    
    @JsonIgnore
    private ArrayList<Participant> participants;

    public Chat(String starter, Long chatId, String name, Long started, boolean isPrivate) {
        this.starter = starter;
        this.chatId = chatId;
        this.name = name;
        this.started = started;
        this.isPrivate = isPrivate;
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
    
    public Boolean getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Boolean isPrivate) {
        this.isPrivate = isPrivate;
    }
    @JsonIgnore
    public ArrayList<Participant> getParticipants() {
        return participants;
    }
    @JsonIgnore
    public void setParticipants(ArrayList<Participant> participants) {
        this.participants = participants;
    }
    
    
}
