package mappers;

import pojos.account.Account;

public interface AccountMapper {
    public int insert(Account account);
    public Account getAccountByToken(String token);
}