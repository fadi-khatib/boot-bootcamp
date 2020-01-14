package main;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import util.InfraUtil;
import mappers.AccountMapper;
import models.Account;
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
    private final AccountMapper accountMapper;

    @Inject
    public EntryPoint(AccountMapper accountMapper) {
        this.accountMapper = requireNonNull(accountMapper);
    }

    // get account data by token
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("account/token")
    public Response getAccountToken(@HeaderParam("X-ACCOUNT-TOKEN") String accountToken) {
        Account accountByToken = (Account) accountMapper.getAccountByToken(accountToken);
        if ( accountByToken != null) {
            Gson gson = new Gson();
            String json = gson.toJson(accountByToken);
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
        Account newAccount = createUserWithName( accountJson.get("accountName").getAsString());
        accountMapper.insert(newAccount);
        Account accountByToken = accountMapper.getAccountByToken(newAccount.getToken());
        if ( accountByToken != null){
            Gson gson = new Gson();
            String accountJsonString = gson.toJson(accountByToken);
            return Response.status(HttpURLConnection.HTTP_CREATED).entity(accountJsonString).build();
        }
        return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity("Could not create new account").build();
    }

    private Account createUserWithName(String accountName){
        String token = RandomStringUtils.random(20, false, true);
        String esIndexName = "logz-" + RandomStringUtils.random(20, false, true);
        return new Account(accountName, token, esIndexName);
    }


}