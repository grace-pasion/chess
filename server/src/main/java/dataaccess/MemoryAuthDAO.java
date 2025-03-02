package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Objects;

public class MemoryAuthDAO implements AuthDAO {
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
    }

    @Override
    public void deleteAuth(String authToken) {
        authMap.remove(authToken);
    }

    @Override
    public AuthData getDataFromAuthToken(String authToken) {
        return authMap.get(authToken);
    }
}
