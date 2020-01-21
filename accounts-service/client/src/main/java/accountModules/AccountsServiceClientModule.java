package accountModules;

import client.AccountsServiceClient;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class AccountsServiceClientModule extends AbstractModule{

    private final int accountsServicePort;
    private final String accountsServiceHost;
    public AccountsServiceClientModule(String accountsServiceHost, int accountsServicePort){
        this.accountsServiceHost = accountsServiceHost;
        this.accountsServicePort = accountsServicePort;

    }

    @Override
    protected void configure() {
        binder().requireExplicitBindings();
    }
    @Provides
    public AccountsServiceClient providesAccountsServiceClient(){
        return new AccountsServiceClient(accountsServiceHost, accountsServicePort);
    }
}
