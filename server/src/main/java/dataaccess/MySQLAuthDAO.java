package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MySQLAuthDAO implements AuthDAO {
    @Override
    public void createAuth(String authToken, AuthData authData) {

    }

    @Override
    public void clear() {

    }

    @Override
    public HashMap<String, AuthData> getAuthMap() {
        return null;
    }

    @Override
    public AuthData getAuthData(String user) {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) {

    }

    @Override
    public AuthData getDataFromAuthToken(String authToken) {
        return null;
    }
}
