/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.exep;

/**
 *
 * @author root
 */
public class FeedbackMessage {

    public String message;
    public String platform;
    public String user;

    public FeedbackMessage() {

    }

    public FeedbackMessage(String message, String platform, String user) {
        this.message = message;
        this.platform = platform;
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

}
