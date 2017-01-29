/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.exep.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 *
 * @author root
 */
public class UserRepository {
    
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
    
    public static void insertFeedback(String message, String platform, String user) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = Database.getConnection();
            statement = connection.prepareStatement("INSERT INTO feedback VALUES (id, NOW(), ?, ?, ?)");
            statement.setString(1, message);
            statement.setString(2, platform);
            statement.setString(3, user);
            statement.executeUpdate();
        } finally {
            if(statement != null)
                statement.close();
            if(connection != null)
                connection.close();
        }
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
            resultSet.next();
            password = resultSet.getString(5);
        } finally {
            if(statement != null)
                statement.close();
            if(connection != null)
                connection.close();
        }
        return password;
    }
}
