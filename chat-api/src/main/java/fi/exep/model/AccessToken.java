package fi.exep.model;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.apache.commons.lang3.RandomStringUtils;

public class AccessToken {
    private String token;
    private Long timeAdded;
    private Long expires;
    private Long userId;
    
    public AccessToken(String token, Long timeAdded, Long expires, Long userId) {
        this.token = token;
        this.timeAdded = timeAdded;
        this.expires = expires;
        this.userId = userId;
    }
    
    public static boolean isAccessTokenValid(AccessToken token) {
        if(token == null)
            return false;
        Long timeNow = System.currentTimeMillis();
        return (timeNow >= token.getTimeAdded() && timeNow < token.getExpires());
    }
            
    public static String generateAccessTokenString() {
        return RandomStringUtils.randomAlphanumeric(20);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(Long timeAdded) {
        this.timeAdded = timeAdded;
    }

    public Long getExpires() {
        return expires;
    }

    public void setExpires(Long expires) {
        this.expires = expires;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
