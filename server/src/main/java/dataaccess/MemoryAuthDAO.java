package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Objects;

public class MemoryAuthDAO implements AuthDAO {
    //change so key is authToken instead of username
    private final HashMap<String, AuthData> authMap = new HashMap<>();

    @Override
    public void createAuth(String authToken, AuthData authData) {
        authMap.put(authToken, authData);
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
        String keyAuthToken = null;
        for (String token : authMap.keySet()) {
            String username = authMap.get(token).username();
            if (Objects.equals(username, user)) {
                keyAuthToken = token;
                break;
            }
        }
        return authMap.get(keyAuthToken);

        //return authMap.get(user);
    }

    @Override
    public void deleteAuth(String authToken) {
        authMap.remove(authToken);
       /* String keyRemove = null;
        for (String username : authMap.keySet()) {
            AuthData authData = authMap.get(username);
            if (authData.authToken().equals(authToken)) {
                keyRemove = username;
                break;
            }
        }

        if (keyRemove != null) {
            authMap.remove(keyRemove);
        } */
    }

    @Override
    public AuthData getDataFromAuthToken(String authToken) {
        /*
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
        return authMap.get(usernameCorrect); */
        return authMap.get(authToken);
    }
}
