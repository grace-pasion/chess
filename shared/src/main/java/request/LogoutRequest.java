package request;

/**
 *This is the logout request that helps turn the JSON data
 *into some variables, so the program can utilize it.
 *
 * @param authToken the authentication token of the user
 */
public record LogoutRequest(String authToken) {
    @Override
    public String authToken() {
        return authToken;
    }
}
