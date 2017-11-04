package fi.exep;

import fi.exep.model.Responses;
import fi.exep.model.AccessToken;
import fi.exep.db.ChatRepository;
import fi.exep.db.UserRepository;
import fi.exep.model.Chat;
import fi.exep.model.IncomingMessage;
import fi.exep.model.OutgoingMessage;
import fi.exep.model.User;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import jdk.nashorn.internal.ir.ObjectNode;
import org.jvnet.hk2.annotations.Optional;
import org.mindrot.jbcrypt.BCrypt;

@Path("chat")
public class ChatResource {
    
    @Path("create")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response chatCreationHandler(@HeaderParam("token") @NotNull String token, @QueryParam("chatName") @NotNull String chatName, @QueryParam("private") @NotNull boolean isPrivate, @QueryParam("otherUser") @Optional String username) { 
        AccessToken accessToken = null;
        try {
            accessToken = UserRepository.fetchAccessToken(token);
            if (!AccessToken.isAccessTokenValid(accessToken)) {
                return Response.status(Status.UNAUTHORIZED).build();
            }
        } catch(SQLException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        
        chatName = chatName.trim();
        Long newChatId = null;
        try {
            if(isPrivate) {
                User other = UserRepository.fetchUser(username);
                newChatId = ChatRepository.createNewChat(chatName, accessToken.getUserId(), isPrivate);
                ChatRepository.addParticipant(accessToken.getUserId(), newChatId);
                ChatRepository.addParticipant(other.getId(), newChatId);
            } else {
                newChatId = ChatRepository.createNewChat(chatName, accessToken.getUserId(), isPrivate);
                ChatRepository.addParticipant(accessToken.getUserId(), newChatId);
            }
        } catch(SQLException e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.status(Status.OK).build();
    }
    
    @Path("join")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addParticipant(@HeaderParam("token") @NotNull String token, @QueryParam("chatId") @NotNull Long chatId) {
        AccessToken accessToken = null;
        Chat chat = null;
        try {
            accessToken = UserRepository.fetchAccessToken(token);
            if (!AccessToken.isAccessTokenValid(accessToken)) {
                return Response.status(Status.UNAUTHORIZED).build();
            }
            chat = ChatRepository.fetchChat(chatId);
            if (chat == null) {
                return Response.status(Status.BAD_REQUEST).build();
            }
            if (chat.getIsPrivate()) {
                return Response.status(Status.FORBIDDEN).build();
            }
            ChatRepository.addParticipant(accessToken.getUserId(), chatId);
        } catch(SQLException e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.status(Status.OK).build();
    }
    
    
    
    @Path("send")
    @POST
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response sendMessageHandler(@HeaderParam("token") @NotNull String token, IncomingMessage message){
        AccessToken accessToken = null;
        try {
            accessToken = UserRepository.fetchAccessToken(token);
            if (!AccessToken.isAccessTokenValid(accessToken)) {
                return Response.status(Status.UNAUTHORIZED).build();
            }
        } catch(SQLException e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        Long newMessageId = null;     
        try {
            if (!ChatRepository.isUserParticipant(accessToken.getUserId(), message.getChatId())) {
                return Response.status(Status.UNAUTHORIZED).build();
            }
            newMessageId = ChatRepository.sendMessage(accessToken.getUserId(), message.getChatId(), message.getContent());
        } catch (SQLException ex) {
            Logger.getLogger(ChatResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        
        return Response.status(Status.OK).entity(new HashMap<String,Long>().put("chatId", newMessageId)).build();
    }
    
    @Path("fetch/before")
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response fetchMessagesHandler(@HeaderParam("token") @NotNull String token, @QueryParam("chatId") @NotNull @Min(1) Long chatId, @QueryParam("messageId") @NotNull long before, @QueryParam("count") @Min(1) @NotNull Long count) {
        AccessToken accessToken = null;
        ArrayList<OutgoingMessage> messages = null;
        try {
            accessToken = UserRepository.fetchAccessToken(token);
            if (!AccessToken.isAccessTokenValid(accessToken) || ! ChatRepository.isUserParticipant(accessToken.getUserId(), chatId)) {
                return Response.status(Status.UNAUTHORIZED).build();
            }
            messages = ChatRepository.fetchMessagesBefore(chatId, before, count);
        } catch(SQLException e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.status(Status.OK).entity(messages).build();
    }
    
    @Path("fetch/after")
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response fetchMessagesHandler(@HeaderParam("token") @NotNull String token, @QueryParam("chatId") @NotNull @Min(1) Long chatId, @QueryParam("messageId") @NotNull long after) {
        AccessToken accessToken = null;
        ArrayList<OutgoingMessage> messages = null;
        try {
            accessToken = UserRepository.fetchAccessToken(token);
            if (!AccessToken.isAccessTokenValid(accessToken) || ! ChatRepository.isUserParticipant(accessToken.getUserId(), chatId)) {
                return Response.status(Status.UNAUTHORIZED).build();
            }
            messages = ChatRepository.fetchMessagesAfter(chatId, after);
        } catch(SQLException e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.status(Status.OK).entity(messages).build();
    }
    
    @Path("fetch/newest")
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response fetchMessagesHandler(@HeaderParam("token") @NotNull String token, @QueryParam("chatId") @NotNull @Min(1) Long chatId, @QueryParam("count") @NotNull Long count) {
        AccessToken accessToken = null;
        ArrayList<OutgoingMessage> messages = null;
        try {
            accessToken = UserRepository.fetchAccessToken(token);
            if (!AccessToken.isAccessTokenValid(accessToken) || ! ChatRepository.isUserParticipant(accessToken.getUserId(), chatId)) {
                return Response.status(Status.UNAUTHORIZED).build();
            }
            messages = ChatRepository.fetchNewestMessages(chatId, count);
        } catch(SQLException e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.status(Status.OK).entity(messages).build();
    }
    
    @Path("list")
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response chatListHandler(@HeaderParam("token") @NotNull String token) {
        AccessToken accessToken = null;
        ArrayList<Chat> chats = null;
        try {
            accessToken = UserRepository.fetchAccessToken(token);
            if (!AccessToken.isAccessTokenValid(accessToken)) {
                return Response.status(Status.UNAUTHORIZED).build();
            }
            chats = ChatRepository.fetchUserChats(accessToken.getUserId());
        } catch(SQLException e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.status(Status.OK).entity(chats).build();
    }
    
    @Path("search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response chatSearchHandler(@HeaderParam("token") @NotNull String token, @QueryParam("query") @NotNull @Size(min = 2) String query) {
        AccessToken accessToken = null;
        ArrayList<Chat> chats = null;
        try {
            accessToken = UserRepository.fetchAccessToken(token);
            if (!AccessToken.isAccessTokenValid(accessToken)) {
                return Response.status(Status.UNAUTHORIZED).build();
            }
            chats = ChatRepository.searchChats(query);
        } catch(SQLException e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.status(Status.OK).entity(chats).build();
    }
}
