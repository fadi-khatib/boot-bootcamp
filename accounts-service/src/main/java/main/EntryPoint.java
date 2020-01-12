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
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;


@Path("/")
public class EntryPoint {
    public static Logger logger = LoggerFactory.getLogger(EntryPoint.class);
    private UserMapper userMapper;

    @Inject
    public EntryPoint(UserMapper userMapper) {
        this.userMapper = userMapper;
        userMapper.createNewTableIfNotExists("Users");
    }

    //for internal server test
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("test")
    public String test() {
        return "logshandler . my server is runing fine";
    }

    // get account data by token
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("account/token")
    public Response getAccountToken(@HeaderParam("X-ACCOUNT-TOKEN") String accountToken) {
        System.out.println(accountToken);
        User user = (User)userMapper.getUserByToken(accountToken);
        System.out.println(user.toString());//*local*
        Gson gson = new Gson();
        String json = gson.toJson(user);
        System.out.println(json);//*local*

        return Response.status(HttpURLConnection.HTTP_OK).entity(json).build();
    }

    // Create new Account
    // return id, name, token, esIndexName
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("create-account")
    public Response createAccount(String jsonString) {
        JsonObject jsonObject = InfraUtil.stringToJson(jsonString);
        String accountName = jsonObject.get("accountName").getAsString();
        String token = RandomStringUtils.random(20, false, true);
        String esIndexName = "logz-" + RandomStringUtils.random(20, false, true);
        int sqlStatus = userMapper.insert(new User(accountName, token, esIndexName));
        System.out.println(token);//*local*
        User user = userMapper.getUserByToken(token);
        System.out.println(user.toString());//*local*
        Gson gson = new Gson();
        String json = gson.toJson(user);
        if (sqlStatus > 0){
//            Gson gson = new Gson();
//            String json = gson.toJson(user);
//            JsonObject JSONObject =  InfraUtil.stringToJson(json);
            return Response.status(HttpURLConnection.HTTP_OK).entity(json).build();
        }
        return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(json).build();



    }

}