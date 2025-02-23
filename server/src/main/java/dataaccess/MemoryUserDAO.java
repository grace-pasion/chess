package dataaccess;

import model.UserData;

import java.util.HashMap;


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
}
