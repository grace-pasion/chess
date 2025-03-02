package result;

/**
 *
 * @param username the username of the person who just logged in
 * @param authToken the generated authToken for the person which is
 *                  created when they login
 */
public record LoginResult(String username, String authToken) {
}
