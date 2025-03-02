package result;

/**
 * This represents the result when a user registers after
 * adding everything to the database.
 * @param username the registered username
 * @param authToken the authentication token assigned to the user
 */
public record RegisterResult(String username, String authToken) {
}
