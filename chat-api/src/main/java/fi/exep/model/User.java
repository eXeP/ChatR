/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.exep.model;

public class User {
    private String username;
    private String displayname;
    private String BCryptHash;
    private Long id;
    
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

    public String getBCryptHash() {
        return BCryptHash;
    }

    public void setBCryptHash(String BCryptHash) {
        this.BCryptHash = BCryptHash;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
}
