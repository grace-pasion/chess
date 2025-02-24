package request;

public record ListGameRequest(String authToken) {
    @Override
    public String authToken() {
        return authToken;
    }
}
