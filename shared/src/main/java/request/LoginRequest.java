package request;

/**
 * This is the login request that helps turn the JSON data
 * into some variables, so the program can utilize it.
 *
 * @param username the username of the person logging in
 * @param password the password of the person logging in
 */
public record LoginRequest(String username, String password) {
    @Override
    public String username() {
        return username;
    }

    @Override
    public String password() {
        return password;
    }
}
