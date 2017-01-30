/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.exep.db;

import fi.exep.AccessToken;
import fi.exep.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;

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
        User user;
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
}
