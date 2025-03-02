package request;

/**
 * this is a request where I take the JSON request
 * data and put it into this nice format.
 *
 * @param gameName the name of the game
 */
public record CreateGameRequest(String gameName) {
    @Override
    public String gameName() {
        return gameName;
    }
}
