package request;

public record CreateGameRequest(String gameName) {
    @Override
    public String gameName() {
        return gameName;
    }
}
