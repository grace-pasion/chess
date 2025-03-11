package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Objects;


public class MemoryUserDAO implements UserDAO {
    private final HashMap<String, UserData> users = new HashMap<>();


    @Override
    public UserData getUserByUsername(String username) {
        return users.get(username);
    }

    @Override
    public void createUser(UserData userData)  {
        users.put(userData.username(), userData);
    }

    @Override
    public void clear() {
        users.clear();
    }

    @Override
    public HashMap<String, UserData> getUserMap() {
        return users;
    }

    @Override
    public boolean verifyUser(String username, String clearPassword) {
        UserData userData = users.get(username);
        if (userData == null) {
            return false;
        }
        return Objects.equals(userData.password(), clearPassword);
    }
}
