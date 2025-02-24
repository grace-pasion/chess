package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    private final HashMap<String, AuthData> authMap = new HashMap<>();

    @Override
    public void createAuth(AuthData authData) {
        authMap.put(authData.username(), authData);
    }

    @Override
    public void clear() {
        authMap.clear();
    }

    @Override
    public HashMap<String, AuthData> getAuthMap() {
        return authMap;
    }

    @Override
    public AuthData getAuthData(String user) {
        return authMap.get(user);
    }

    @Override
    public void deleteAuth(String authToken) {
        String keyRemove = null;
        for (String username : authMap.keySet()) {
            AuthData authData = authMap.get(username);
            if (authData.authToken().equals(authToken)) {
                keyRemove = username;
                break;
            }
        }

        if (keyRemove != null) {
            authMap.remove(keyRemove);
        }
    }

    @Override
    public AuthData getDataFromAuthToken(String authToken) {
        String usernameCorrect = null;
        for (String username : authMap.keySet()) {
            AuthData authData = authMap.get(username);
            if (authData.authToken().equals(authToken)) {
                usernameCorrect = username;
                break;
            }
        }

        if (usernameCorrect == null) {
            return null;
        }
        return authMap.get(usernameCorrect);
    }
}
