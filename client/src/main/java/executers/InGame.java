package executers;

import facade.exception.ResponseException;
import websocket.messages.NotificationMessage;
import websocketFacade.WebSocketFacade;

import java.util.Arrays;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

/**
 * This handles in game user commands while they are in an
 * active chess game. Right now, it only supports basic commands,
 * but I will expand on it in later phases.
 */
public class InGame {
    /**
     * The string representing the server URL
     */
    private final String serverUrl;

    private final String authToken;
    private final WebSocketFacade webSocketFacade;
    private final NotificationMessage notificationMessage;
    /**
     * This is just a constructor to make sure we have the
     * right server URL
     * @param serverUrl a string represent the server URL
     */
    public InGame(String authToken, String serverUrl, int gameID) throws ResponseException {
        this.serverUrl = serverUrl;
        this.authToken = authToken;
        this.gameID = gameID;
        notificationMessage = new NotificationMessage()
        this.webSocketFacade = new WebSocketFacade(serverUrl, notificationMessage);

    }

    /**
     * This goes through the user input
     * and evalautes the command to see how to process it.
     *
     * @param input the user input
     * @return a string result
     */
    public String eval(String input) {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length >0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch(cmd) {
            case "quit" -> "quit";
            default -> help();
        };
    }

    /**
     * When this is called, it just returns a set of commands
     * a user can type in (it's basically just a help menu)
     *
     * @param params these are just extra words the user
     *               typed it that aren't needed
     * @return a message indicating what you can do
     * for the in-game functionaltiy
     */
    public String help(String... params) {
        return SET_TEXT_COLOR_BLUE+"redraw"+
                SET_TEXT_COLOR_RED+" - to redraw the chessboard"+SET_TEXT_COLOR_BLUE+
                "\n\tleave"+ SET_TEXT_COLOR_RED+" - this will remove you from the game"+
                SET_TEXT_COLOR_BLUE+"\n\tmove <start> <end>"+SET_TEXT_COLOR_RED+" - " +
                "will move that piece at that location"+ SET_TEXT_COLOR_BLUE+"\n\tresign"+
                SET_TEXT_COLOR_RED+" - to forfeit and end the game"+
                SET_TEXT_COLOR_BLUE+"\n\thelp"+
                SET_TEXT_COLOR_RED+" - with possible commands";
    }

    //PHASE 6:
    //move

    //resign

    //highlight legal moves

    //leave

    //redraw chess board

    //help
}
