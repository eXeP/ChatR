/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.exep.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

public class User {
    private String username;
    private String displayname;
    @JsonIgnore
    private String BCryptHash;
    @JsonIgnore
    private Long id;
    private String token;

    public User(Long id, String username, String displayname, String BCryptHash){
        this.id = id;
        this.username = username;
        this.displayname = displayname;
        this.BCryptHash = BCryptHash;
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
    
    @JsonIgnore
    public String getBCryptHash() {
        return BCryptHash;
    }
    @JsonIgnore
    public void setBCryptHash(String BCryptHash) {
        this.BCryptHash = BCryptHash;
    }
    @JsonIgnore
    public Long getId() {
        return id;
    }
    @JsonIgnore
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    
}
