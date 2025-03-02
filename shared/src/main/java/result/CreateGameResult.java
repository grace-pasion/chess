package result;

/**
 *  This represents the createGame result. If it is
 *  returned, the game is successfully created.
 *
 * @param gameID the gameID of the created game
 */
public record CreateGameResult(int gameID) {
    @Override
    public int gameID() {
        return gameID;
    }
}
