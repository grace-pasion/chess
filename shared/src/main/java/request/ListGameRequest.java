package request;

/**
 * This is the list games request that helps turn the JSON data
 * into some variables, so the program can utilize it.
 * @param authToken the authentication token of the person trying
 *                  to get the information
 */
public record ListGameRequest(String authToken) {
    @Override
    public String authToken() {
        return authToken;
    }
}
