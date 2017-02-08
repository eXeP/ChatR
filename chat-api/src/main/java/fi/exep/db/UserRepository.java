/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.exep.db;

import static fi.exep.db.ChatRepository.parseChats;
import fi.exep.model.AccessToken;
import fi.exep.model.Chat;
import fi.exep.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 *
 * @author root
 */
public class UserRepository {
    public static Long TIME_30_MINUTES = 30*60*1000L;
    
    public static void addUser(String username, String displayname, String password) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = Database.getConnection();
            statement = connection.prepareStatement("INSERT INTO users VALUES (id, NOW(), ?, ?, ?)");
            statement.setString(1, username);
            statement.setString(2, displayname);
            statement.setString(3, password);
            statement.executeUpdate();   
        } finally {
            if(statement != null)
                statement.close();
            if(connection != null)
                connection.close();
        }
    }
    
    public static boolean isUsernameAvailable(String username) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        boolean available = false;
        try {
            connection = Database.getConnection();
            statement = connection.prepareStatement("SELECT COUNT(*) FROM users WHERE username COLLATE UTF8_GENERAL_CI LIKE ?");
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            if (resultSet.getInt(1) == 0) {
                available = true;
            }
        } finally {
            if(statement != null)
                statement.close();
            if(connection != null)
                connection.close();
        }
        return available;
    }
    
    public static String fetchPasswordHash(String username) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        String password = null;
        try {
            connection = Database.getConnection();
            statement = connection.prepareStatement("SELECT * FROM users WHERE username COLLATE UTF8_GENERAL_CI LIKE ?");
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                throw new SQLException();
            }
            password = resultSet.getString(5);
        } finally {
            if (statement != null)
                statement.close();
            if (connection != null)
                connection.close();
        }
        return password;
    }
    
    public static User fetchUser(String username) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        User user = null;
        try {
            connection = Database.getConnection();
            statement = connection.prepareStatement("SELECT * FROM users WHERE username COLLATE UTF8_GENERAL_CI LIKE ?");
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                throw new SQLException();
            }
            user = new User(resultSet.getLong(1), resultSet.getString(3), resultSet.getString(4), resultSet.getString(5));
        } finally {
            if (statement != null)
                statement.close();
            if (connection != null)
                connection.close();
        }
        return user;
    }
    
    public static AccessToken addAccessToken(User user) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        String token = AccessToken.generateAccessTokenString();
        Long timeNow = System.currentTimeMillis();
        Long expires = timeNow + TIME_30_MINUTES;
        try {
            connection = Database.getConnection();
            statement = connection.prepareStatement("INSERT INTO access_tokens VALUES (id, ?, ?, ?, ?)");
            statement.setTimestamp(1, new Timestamp(timeNow));
            statement.setTimestamp(2, new Timestamp(expires));
            statement.setLong(3, user.getId());
            statement.setString(4, token);
            statement.executeUpdate();   
        } finally {
            if(statement != null)
                statement.close();
            if(connection != null)
                connection.close();
        }
        return new AccessToken(token, timeNow, expires, user.getId());
    }
    
    public static AccessToken fetchAccessToken(String tokenString) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        AccessToken token = null;
        try {
            connection = Database.getConnection();
            statement = connection.prepareStatement("SELECT * FROM access_tokens WHERE token LIKE ?");
            statement.setString(1, tokenString);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                throw new SQLException();
            }
            token = new AccessToken(tokenString, resultSet.getTimestamp(2).getTime(), resultSet.getTimestamp(3).getTime(), resultSet.getLong(4));
        } finally {
            if (statement != null)
                statement.close();
            if (connection != null)
                connection.close();
        }
        return token;
    }
    
    public static ArrayList<String> searchUsers(String query) throws SQLException {
        ArrayList<String> users = null;
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = Database.getConnection();
            statement = connection.prepareStatement(
                    "SELECT u.username " +
                    "FROM users u " +
                    "WHERE u.username COLLATE UTF8_GENERAL_CI LIKE ?");
            statement.setString(1, "%" + query + "%");
            ResultSet resultSet = statement.executeQuery();
            users = parseUsers(resultSet);
        } finally {
            if(statement != null)
                statement.close();
            if(connection != null)
                connection.close();
        }
        return users;
    }
    
    public static ArrayList<String> parseUsers(ResultSet rs) throws SQLException {
        ArrayList<String> users = new ArrayList<>();
        while(rs.next()){
            users.add(rs.getString(1));
        }
        return users;
    }
}
