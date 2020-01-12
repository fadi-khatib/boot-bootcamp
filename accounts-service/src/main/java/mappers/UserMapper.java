package mappers;

import models.User;
import java.util.List;

public interface UserMapper {
    List<User> all();
    public int insert(User user);
    public User getUserByToken(String token);
    public void  createNewTableIfNotExists(String s);
}