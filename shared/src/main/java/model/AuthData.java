package model;

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
