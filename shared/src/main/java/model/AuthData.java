package model;

/**
 * This is all the information surrounding the authentication
 * token such as the authentication token string itself and
 * the user it is attached to
 *
 * @param authToken
 * @param username
 */
public record AuthData(String authToken, String username) {
    //automatically generated getters

    @Override
    public String authToken() {
        return authToken;
    }

    @Override
    public String username() {
        return username;
    }
}
