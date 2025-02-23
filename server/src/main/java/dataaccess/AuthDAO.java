package dataaccess;

import model.AuthData;

import java.util.HashMap;

public interface AuthDAO {
    //Do stuff with AUTH

    void createAuth(AuthData authData);
    void clear();
    HashMap<String, AuthData> getAuthMap();
    AuthData getAuthData(String user);
}
