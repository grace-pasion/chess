package request;

/**
 * This is the join game request that helps turn the JSON data
 *  into some variables, so the program can utilize it.
 *
 * @param playerColor the color that the requesting player wants
 * @param gameID the current gameID they want to get into
 */
public record JoinGameRequest(String playerColor, int gameID) {
}
