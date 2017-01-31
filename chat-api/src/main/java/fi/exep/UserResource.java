package fi.exep;

import fi.exep.model.Responses;
import fi.exep.model.AccessToken;
import fi.exep.model.User;
import fi.exep.db.UserRepository;
import java.sql.SQLException;
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
import org.mindrot.jbcrypt.BCrypt;

@Path("user")
public class UserResource {
    
    @Path("register")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrationHandler(@QueryParam("username") String username, @QueryParam("displayname") String displayname, @QueryParam("password") String password) {
        username = username.trim();
        displayname = displayname.trim();
        password = password.trim();
        try {
            if (!UserRepository.isUsernameAvailable(username))
                return Response.status(Status.BAD_REQUEST).entity(Responses.INVALID_PARAMS).build();
        } catch(SQLException e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        try {
            UserRepository.addUser(username, displayname, BCrypt.hashpw(password, BCrypt.gensalt()));
        } catch(SQLException e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.status(Status.OK).build();
    }
    
    @Path("login")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginHandler(@QueryParam("username") String username, @QueryParam("password") String password) {
        username = username.trim();
        password = password.trim();
        User user = null;
        try {
            user = UserRepository.fetchUser(username);
        } catch (SQLException ex) {
            Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Status.UNAUTHORIZED).entity("USERNAME/PASSWORD DOESN'T MATCH").build();
        }
        if (user == null || !BCrypt.checkpw(password, user.getBCryptHash())) {
            return Response.status(Status.UNAUTHORIZED).entity("USERNAME/PASSWORD DOESN'T MATCH").build();
        }
        AccessToken token = null;
        try {
            token = UserRepository.addAccessToken(user);
        } catch (SQLException ex) {
            Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.status(Status.OK).entity(token).build();
    }
}
