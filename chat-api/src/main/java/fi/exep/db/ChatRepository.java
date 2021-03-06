/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.exep.db;

import com.mysql.cj.api.jdbc.Statement;
import fi.exep.model.AccessToken;
import fi.exep.model.Chat;
import fi.exep.model.OutgoingMessage;
import fi.exep.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ChatRepository {
    
    public static Long createNewChat(String name, Long creator, boolean isPrivate) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        Long newChatId = null;
        try {
            connection = Database.getConnection();
            statement = connection.prepareStatement("INSERT INTO chats VALUES (id, NOW(), ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, name);
            statement.setLong(2, creator);
            statement.setBoolean(3, isPrivate);
            statement.executeUpdate();
            ResultSet keys = statement.getGeneratedKeys();
            keys.next();
            newChatId = keys.getLong(1);
        } finally {
            if(statement != null)
                statement.close();
            if(connection != null)
                connection.close();
        }
        return newChatId;
    }
    
    public static void addParticipant(Long userId, Long chatId) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = Database.getConnection();
            statement = connection.prepareStatement("INSERT INTO participation VALUES (id, NOW(), ?, ?)");
            statement.setLong(1, userId);
            statement.setLong(2, chatId);
            statement.executeUpdate();   
        } finally {
            if(statement != null)
                statement.close();
            if(connection != null)
                connection.close();
        }
    }
    
    public static boolean isChatNameAvailable(String name) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        boolean available = false;
        try {
            connection = Database.getConnection();
            statement = connection.prepareStatement("SELECT COUNT(*) FROM chats WHERE name COLLATE UTF8_GENERAL_CI LIKE ?");
            statement.setString(1, name);
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
    
    public static Long sendMessage(Long userId, Long chatId, String message) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        Long newMessageId = null;
        try {
            connection = Database.getConnection();
            statement = connection.prepareStatement("INSERT INTO messages VALUES (id, NOW(), ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, userId);
            statement.setLong(2, chatId);
            statement.setString(3, message);
            statement.executeUpdate();
            ResultSet keys = statement.getGeneratedKeys();
            keys.next();
            newMessageId = keys.getLong(1);
        } finally {
            if(statement != null)
                statement.close();
            if(connection != null)
                connection.close();
        }
        return newMessageId;
    }
    
    public static boolean isUserParticipant(Long userId, Long chatId) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        boolean isParticipant = false;
        try {
            connection = Database.getConnection();
            statement = connection.prepareStatement("SELECT COUNT(*) FROM participation WHERE chat_id=? AND user_id=?");
            statement.setLong(1, chatId);
            statement.setLong(2, userId);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            if (resultSet.getInt(1) >= 1) {
                isParticipant = true;
            }
        } finally {
            if(statement != null)
                statement.close();
            if(connection != null)
                connection.close();
        }
        return isParticipant;
    }
    
    public static Chat fetchChat(Long chatId) throws SQLException {
        Chat chat = null;
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = Database.getConnection();
            statement = connection.prepareStatement(
                    "SELECT c.id, c.name, c.added, u.username, c.private " +
                    "FROM chats c " +
                    "INNER JOIN users u ON c.starter_user_id = u.id " +
                    "WHERE c.id = ?");
            statement.setLong(1, chatId);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                chat = new Chat(resultSet.getString(4), resultSet.getLong(1), resultSet.getString(2), resultSet.getTimestamp(3).getTime(), resultSet.getBoolean(5));
            }
        } finally {
            if(statement != null)
                statement.close();
            if(connection != null)
                connection.close();
        }
        return chat;
    }
    
    public static ArrayList<Chat> searchChats(String query) throws SQLException {
        ArrayList<Chat> chats = null;
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = Database.getConnection();
            statement = connection.prepareStatement(
                    "SELECT c.id, c.name, c.added, u.username, c.private " +
                    "FROM chats c " +
                    "INNER JOIN users u ON c.starter_user_id = u.id " +
                    "WHERE c.name COLLATE UTF8_GENERAL_CI LIKE ? AND c.private = false");
            statement.setString(1, "%" + query + "%");
            ResultSet resultSet = statement.executeQuery();
            chats = parseChats(resultSet);
        } finally {
            if(statement != null)
                statement.close();
            if(connection != null)
                connection.close();
        }
        return chats;
    }
    
    public static ArrayList<Chat> fetchUserChats(Long userId) throws SQLException {
        ArrayList<Chat> chats = null;
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = Database.getConnection();
            statement = connection.prepareStatement(
                    "SELECT DISTINCT p.chat_id, c.name, c.added, u.username, c.private " +
                    "FROM participation p " +
                    "INNER JOIN chats c ON p.chat_id = c.id " +
                    "INNER JOIN users u ON c.starter_user_id = u.id " +
                    "WHERE p.user_id = ?");
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            chats = parseChats(resultSet);
        } finally {
            if(statement != null)
                statement.close();
            if(connection != null)
                connection.close();
        }
        return chats;
    }
    
    public static ArrayList<Chat> parseChats(ResultSet rs) throws SQLException {
        ArrayList<Chat> chats = new ArrayList<>();
        rs.beforeFirst();
        while (rs.next()) {
            Chat chat = new Chat(rs.getString(4), rs.getLong(1), rs.getString(2), rs.getTimestamp(3).getTime(), rs.getBoolean(5));
            chats.add(chat);
        }
        return chats;
    }
    
    public static ArrayList<OutgoingMessage> fetchNewestMessages(Long chatId, Long count) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ArrayList<OutgoingMessage> messages = null;
        try {
            connection = Database.getConnection();
            statement = connection.prepareStatement(
                    "SELECT u.username, u.displayname, m.message, m.sent, m.chat_id, m.id " +
                    "FROM messages m " + 
                    "INNER JOIN users u ON m.user_id=u.id " +
                    "WHERE m.chat_id=? " +
                    "ORDER BY m.id DESC LIMIT ?");
            statement.setLong(1, chatId);
            statement.setLong(2, count);
            ResultSet resultSet = statement.executeQuery();
            messages = parseOutgoingMessages(resultSet);
        } finally {
            if(statement != null)
                statement.close();
            if(connection != null)
                connection.close();
        }
        return messages;
    }
    
    public static ArrayList<OutgoingMessage> fetchMessagesAfter(Long chatId, Long messageId) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ArrayList<OutgoingMessage> messages = null;
        try {
            connection = Database.getConnection();
            statement = connection.prepareStatement(
                    "SELECT u.username, u.displayname, m.message, m.sent, m.chat_id, m.id " +
                    "FROM messages m " + 
                    "INNER JOIN users u ON m.user_id=u.id " +
                    "WHERE m.chat_id=? AND m.id>?");
            statement.setLong(1, chatId);
            statement.setLong(2, messageId);
            ResultSet resultSet = statement.executeQuery();
            messages = parseOutgoingMessages(resultSet);
        } finally {
            if(statement != null)
                statement.close();
            if(connection != null)
                connection.close();
        }
        return messages;
    }
    
    public static ArrayList<OutgoingMessage> fetchMessagesBefore(Long chatId, Long messageId, Long count) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ArrayList<OutgoingMessage> messages = null;
        try {
            connection = Database.getConnection();
            statement = connection.prepareStatement(
                    "SELECT u.username, u.displayname, m.message, m.sent, m.chat_id, m.id " +
                    "FROM messages m " + 
                    "INNER JOIN users u ON m.user_id=u.id " +
                    "WHERE m.chat_id=? AND m.id<? ORDER BY m.id DESC LIMIT ?");
            statement.setLong(1, chatId);
            statement.setLong(2, messageId);
            statement.setLong(3, count);
            ResultSet resultSet = statement.executeQuery();
            messages = parseOutgoingMessages(resultSet);
        } finally {
            if(statement != null)
                statement.close();
            if(connection != null)
                connection.close();
        }
        return messages;
    }
    
    public static ArrayList<OutgoingMessage> parseOutgoingMessages(ResultSet rs) throws SQLException {
        ArrayList<OutgoingMessage> messages = new ArrayList<>();
        rs.beforeFirst();
        while(rs.next()){
            OutgoingMessage message = new OutgoingMessage(rs.getString(1), rs.getString(2), rs.getString(3), rs.getTimestamp(4).getTime(), rs.getLong(5), rs.getLong(6));
            messages.add(message);
        }
        return messages;
    }
}
