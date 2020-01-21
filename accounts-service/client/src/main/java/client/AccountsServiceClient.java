package client;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import pojos.account.Account;
import pojos.account.CreateAccountRequest;

public class AccountsServiceClient {
    private final String X_ACCOUNT_TOKEN = "X-ACCOUNT-TOKEN";
    private WebTarget target;


    public AccountsServiceClient(String accountsServiceHost, int accountsServicePort) {
        String accountServiceUri = "http://" + accountsServiceHost + ":" + accountsServicePort + "/";
        target = ClientBuilder.newClient().target(accountServiceUri);
    }

    public Account getAccountByToken(String accountToken) {
        Response response = target.path("account/token")
                .request(MediaType.APPLICATION_JSON)
                .header(X_ACCOUNT_TOKEN, accountToken)
                .get();
        return response.readEntity(Account.class);
    }

    public Account createAccount(CreateAccountRequest accountRequest) {
        Response response = target.path("create-account")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(accountRequest));
        return response.readEntity(Account.class);
    }


}
