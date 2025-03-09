package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MySQLUserDAO implements UserDAO {
    @Override
    public UserData getUserByUsername(String username) {
        return null;
    }

    @Override
    public void createUser(UserData userData) {

    }

    @Override
    public void clear() {

    }

    @Override
    public HashMap<String, UserData> getUserMap() {
        return null;
    }
}
