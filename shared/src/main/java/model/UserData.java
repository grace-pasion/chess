package model;

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
