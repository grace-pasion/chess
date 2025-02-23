package dataaccess;
import model.UserData;

public interface UserDAO {
    UserData getUserByUsername(String username);

    void createUser(UserData userData);
    void clear();
}
