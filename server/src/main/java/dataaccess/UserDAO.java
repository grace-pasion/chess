package dataaccess;
import model.UserData;
import model.AuthData;
public interface UserDAO {
    UserData getUserByUsername(String username);

    void createUser(UserData userData);
    void createAuth(AuthData authData);
}
