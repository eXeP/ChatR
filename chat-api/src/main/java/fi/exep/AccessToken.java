package fi.exep;

import java.security.SecureRandom;

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
            
    public static String generateAccessTokenString() {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[20];
        random.nextBytes(bytes);
        String token = bytes.toString();
        return token;
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
