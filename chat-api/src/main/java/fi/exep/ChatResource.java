package fi.exep;

import fi.exep.model.Responses;
import fi.exep.model.AccessToken;
import fi.exep.db.ChatRepository;
import fi.exep.db.UserRepository;
import fi.exep.model.IncomingMessage;
import fi.exep.model.OutgoingMessage;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import jdk.nashorn.internal.ir.ObjectNode;
import org.mindrot.jbcrypt.BCrypt;

@Path("chat")
public class ChatResource {
    
    @Path("create")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response chatCreationHandler(@QueryParam("token") String token, @QueryParam("chatName") String chatName, @QueryParam("private") boolean isPrivate) { 
        AccessToken accessToken = null;
        try {
            accessToken = UserRepository.fetchAccessToken(token);
        } catch(SQLException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        if (!AccessToken.isAccessTokenValid(accessToken)) {
            return Response.status(Status.UNAUTHORIZED).build();
        }
        chatName = chatName.trim();
        Long newChatId = null;
        try {
            newChatId = ChatRepository.createNewChat(chatName, accessToken.getUserId(), isPrivate);
            ChatRepository.addParticipant(accessToken.getUserId(), newChatId);
        } catch(SQLException e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.status(Status.OK).entity(new HashMap<String,Long>().put("chatId", newChatId)).build();
    }
    
    @Path("send")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendMessageHandler(IncomingMessage message){
        AccessToken accessToken = null;
        try {
            accessToken = UserRepository.fetchAccessToken(message.getToken());
        } catch(SQLException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        if (!AccessToken.isAccessTokenValid(accessToken)) {
            return Response.status(Status.UNAUTHORIZED).build();
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
    
    @Path("fetch/after")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetchMessagesHandler(@QueryParam("token") String token, @QueryParam("chatId") long chatId, @QueryParam("messageId") long after) {
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
}
