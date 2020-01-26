package main;

import util.InfraUtil;
import mappers.AccountMapper;
import pojos.account.Account;
import org.apache.commons.lang3.RandomStringUtils;
import pojos.account.CreateAccountRequest;

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
import util.GlobalParams;
import static java.util.Objects.requireNonNull;


@Path("/")
public class EntryPoint {
    private final AccountMapper accountMapper;

    @Inject
    public EntryPoint(AccountMapper accountMapper) {
        this.accountMapper = requireNonNull(accountMapper);
    }

    // get account data by token
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("account/token")
    public Response getAccountToken(@HeaderParam(GlobalParams.X_ACCOUNT_TOKEN) String accountToken) {
        Account accountByToken = accountMapper.getAccountByToken(accountToken);
        return Response.status(HttpURLConnection.HTTP_OK).entity(accountByToken).build();
   }

    // Create new Account
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("create-account")
    public Response createAccount(String createAccountString) {
        CreateAccountRequest createAccountRequest = InfraUtil.stringToObject(createAccountString, CreateAccountRequest.class);
        Account newAccount = createAccountWithName( createAccountRequest.getAccountName());
        accountMapper.insert(newAccount);
        Account accountByToken = accountMapper.getAccountByToken(newAccount.getToken());
        return Response.status(HttpURLConnection.HTTP_CREATED).entity(accountByToken).build();//entity(account).build();
    }

    private Account createAccountWithName(String accountName){
        String token = RandomStringUtils.random(20, false, true);
        String esIndexName = "logz-" + RandomStringUtils.random(20, false, true);
        return new Account(accountName, token, esIndexName);
    }


}