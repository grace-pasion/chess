package request;

/**
 * This is the register request that helps turn the JSON data
 * into some variables, so the program can utilize it.
 *
 * @param username the username of the person registering
 * @param password the password chosen by the person registering
 * @param email the email of the person registering
 */
public record RegisterRequest(String username, String password, String email) {
    @Override
    public String username() {
        return username;
    }

    @Override
    public String password() {
        return password;
    }

    @Override
    public String email() {
        return email;
    }
}
