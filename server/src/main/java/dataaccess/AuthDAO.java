package dataaccess;

import model.AuthData;

import java.util.HashMap;

public interface AuthDAO {

    void createAuth(String authToken, AuthData authData);
    void clear();
    HashMap<String, AuthData> getAuthMap();
    AuthData getAuthData(String user);
    void deleteAuth(String authToken);
    AuthData getDataFromAuthToken(String authToken);
}
