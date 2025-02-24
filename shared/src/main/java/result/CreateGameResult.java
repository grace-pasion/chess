package result;

public record CreateGameResult(int gameID) {
    @Override
    public int gameID() {
        return gameID;
    }
}
