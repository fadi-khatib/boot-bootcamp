package mappers;

import models.User;

public interface UserMapper {
    public int insert(User user);
    public User getUserByToken(String token);
}