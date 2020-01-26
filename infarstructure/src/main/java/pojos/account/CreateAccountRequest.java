package pojos.account;

import com.google.gson.Gson;

public class CreateAccountRequest {
    private String accountName;

    public CreateAccountRequest(){

    }
    public CreateAccountRequest(String accountName){
        this.accountName = accountName;
    }

    public String getAccountName(){
        return accountName;
    }

    @Override
    public String toString(){
        Gson gson = new Gson();
        return gson.toJson(this);

    }
}
