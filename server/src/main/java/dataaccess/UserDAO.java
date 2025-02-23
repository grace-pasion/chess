package dataaccess;
import model.UserData;

import java.util.HashMap;

public interface UserDAO {
    UserData getUserByUsername(String username);

    void createUser(UserData userData);
    void clear();
    HashMap<String, UserData> getUserMap();
}
