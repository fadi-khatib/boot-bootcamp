package main;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import util.InfraUtil;
import mappers.UserMapper;
import models.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;

import static java.util.Objects.requireNonNull;


@Path("/")
public class EntryPoint {
    public static Logger logger = LoggerFactory.getLogger(EntryPoint.class);
    private final UserMapper userMapper;

    @Inject
    public EntryPoint(UserMapper userMapper) {
        this.userMapper = requireNonNull(userMapper);
        userMapper.createNewTableIfNotExists("Users");
    }

    // get account data by token
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("account/token")
    public Response getAccountToken(@HeaderParam("X-ACCOUNT-TOKEN") String accountToken) {
        User userByToken = (User)userMapper.getUserByToken(accountToken);
        if ( userByToken != null) {
            Gson gson = new Gson();
            String json = gson.toJson(userByToken);
            return Response.status(HttpURLConnection.HTTP_OK).entity(json).build();
        }
        return Response.status(HttpURLConnection.HTTP_NOT_FOUND).entity("Account-token not found").build();
    }

    // Create new Account
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("create-account")
    public Response createAccount(String accountStringJson) {
        JsonObject accountJson = InfraUtil.stringToJson(accountStringJson);
        User newUser = createUserWithName( accountJson.get("accountName").getAsString());
        userMapper.insert(newUser);
        User userByToken = userMapper.getUserByToken(newUser.getToken());
        if ( userByToken != null){
            Gson gson = new Gson();
            String userJson = gson.toJson(userByToken);
            return Response.status(HttpURLConnection.HTTP_CREATED).entity(userJson).build();
        }
        return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity("Could not create new account").build();
    }

    private User createUserWithName(String accountName){
        String token = RandomStringUtils.random(20, false, true);
        String esIndexName = "logz-" + RandomStringUtils.random(20, false, true);
        return new User(accountName, token, esIndexName);
    }


}