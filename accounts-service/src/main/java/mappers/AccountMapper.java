package mappers;

import models.Account;

public interface AccountMapper {
    public int insert(Account account);
    public Account getAccountByToken(String token);
}