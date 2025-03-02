package model;

/**
 *  This holds all my data that I possibly can have for
 *  a user including their username, password,
 *  and email
 *
 * @param username the username of the player
 * @param password the password of the player
 * @param email the email of the player
 */
public record UserData(String username, String password, String email) {
    //automatically generated getters
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
