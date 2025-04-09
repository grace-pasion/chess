package executers;

import model.GameData;
import request.*;
import result.ListGameResult;
import facade.ServerFacade;
import facade.exception.ResponseException;
import websocket.commands.ConnectCommand;
import websocketFacade.NotificationHandler;
import websocketFacade.WebSocketFacade;

import javax.management.Notification;
import java.util.ArrayList;
import java.util.Arrays;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public class PostLogin {
    private final ServerFacade server;
    private String authToken;
    private boolean transferInGame;
    private boolean isWhite;
    private NotificationHandler notificationHandler;
    private final WebSocketFacade ws;
    private int gameID;

    /**
     * This is just a constructor that initializes our
     * server.
     * @param serverUrl a string representing the server url
     */
    public PostLogin(String serverUrl, NotificationHandler notificationHandler, WebSocketFacade ws) throws ResponseException {
        server = new ServerFacade(serverUrl);
        this.ws = ws;
        transferInGame = false;
    }


    /**
     * This takes in the user input and executes the command they
     * typed in by calling different methods on it.
     *
     * @param input the user's input
     * @return the resulting string
     */
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length >0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch(cmd) {
                case "list", "l" -> list(params);
                case "create", "c" -> create(params);
                case "join", "j" -> join(params);
                case "watch", "w" -> watch(params);
                case "logout" -> logout(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }


    /**
     * This checks to make sure that nothing follows list, but if it
     * does then it returns a failure string. It then calls list games
     * from the server. It then takes this array and formats it nicely into
     * a string.
     *
     * @param params the string following "list"
     * @return the result as a string
     * @throws ResponseException if list games fails
     */
    private String list(String... params) throws ResponseException {
        if (params.length != 0) {
            return SET_TEXT_COLOR_RED + "The list function receives no parameters. " +
                    "For list type in: list";
        }

        ListGameRequest listGameRequest = new ListGameRequest(authToken);
        try {
            ListGameResult listGameResult = server.listGame(listGameRequest);
            ArrayList<GameData> games = listGameResult.getGames();
            if (games.isEmpty()) {
                return SET_TEXT_COLOR_BLUE+"\tNo games currently in play";
            }
            StringBuilder result = new StringBuilder
                    (SET_TEXT_COLOR_BLUE + "Available Games:");
            int i = 1;
            for (GameData game : games) {
                result.append("\n\t");
                result.append(i).append(". ID: ").append(game.gameID());
                result.append("  Game Name:").append(game.gameName());
                result.append("  White: ").append(game.whiteUsername());
                result.append("  Black: ").append(game.blackUsername());
            }
            return result.toString();
        } catch (ResponseException e) {
            return SET_TEXT_COLOR_RED + "List Games Failed: " + e.getMessage();
        }
    }


    /**
     * This calls the server to create a new chess game with the specified parameter
     * name.
     *
     * @param params the name of the created game
     * @return a string result
     * @throws ResponseException if create games fail
     */
    private String create(String... params) throws ResponseException {
        try {
            if (params.length != 1) {
                return SET_TEXT_COLOR_RED+
                        "Invalid Parameters. For create games: create <GAME_NAME>";
            }
            CreateGameRequest createGameRequest = new CreateGameRequest(params[0]);
            server.createGame(createGameRequest, authToken);
            return SET_TEXT_COLOR_BLUE+"Successfully created game "+params[0];
        } catch (ResponseException e) {
            return SET_TEXT_COLOR_RED+"Create Games Failed: "+e.getMessage();
        }
    }


    /**
     * This checks to make sure they have the right parameters. Then,
     * they take the user's input and lets them join with the provided
     * gameID and playerColor if it is valid.
     *
     * @param params which should include the gameID and the playerColor
     * @return the resulting string
     * @throws ResponseException if join game fails
     */
    private String join(String... params) throws ResponseException{
        try {
            if (params.length != 2) {
                return SET_TEXT_COLOR_RED+
                        "Invalid parameters. For join games: join <GAME_ID> [WHITE|BLACK]";
            }
            int gameId;

            try {
                gameId = Integer.parseInt(params[0]);
                this.gameID = gameId;
            } catch (NumberFormatException e) {
                return SET_TEXT_COLOR_RED+"The Game ID is supposed to be a number";
            }

            String playerColor = params[1].toUpperCase();
            if (!playerColor.equals("WHITE") && !playerColor.equals("BLACK")) {
                return SET_TEXT_COLOR_RED+"Invalid. Must choose WHITE or BLACK";
            }
            isWhite = playerColor.equals("WHITE");

            ConnectCommand.Side side;
            if (isWhite) {
                side = ConnectCommand.Side.WHITE;
            } else {
                side = ConnectCommand.Side.BLACK;
            }
            ws.connect(authToken, gameId, side);

            JoinGameRequest joinGameRequest = new JoinGameRequest(playerColor, gameId);
            server.joinGame(joinGameRequest, authToken);

            transferInGame = true;
            return SET_TEXT_COLOR_BLUE+
                    "Successfully joined game "+gameId+" as "+playerColor+".";
        } catch (ResponseException e) {
            return SET_TEXT_COLOR_RED+"Join Games Failed: "+e.getMessage();
        }
    }


    /**
     * This goes through all the games to see what matches the provided gameID.
     * Once it finds that game, it will then join as an observer.
     *
     * @param params the gameID
     * @return the resulting string
     * @throws ResponseException if the watch fails
     */
    private String watch(String... params) throws ResponseException{
        isWhite = true;
        if (params.length != 1) {
            return SET_TEXT_COLOR_RED+"Invalid parameters. For watch games: watch <GAME_ID>";
        }
        int gameId;
        try {
            gameId = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            return SET_TEXT_COLOR_RED+"The Game ID must be a number";
        }

        try {
            ListGameRequest listGameRequest = new ListGameRequest(authToken);
            ListGameResult listGameResult = server.listGame(listGameRequest);
            var games = listGameResult.getGames();
            //NEED TO DO SOME MORE LOGIC IN HERE FOR PHASE 6
            for (GameData game : games) {
                if (game.gameID() == gameId) {

                    ConnectCommand.Side side = ConnectCommand.Side.OBSERVER;
                    ws.connect(authToken, gameId, side);

                    transferInGame = true;
                    return SET_TEXT_COLOR_BLUE+
                            "Successfully joined "+game.gameName()+" as a viewer.";
                }
            }
            return SET_TEXT_COLOR_RED+"Game with ID "+gameId+" was not found.";
        } catch (ResponseException e) {
            return SET_TEXT_COLOR_RED+"Failed to retrieve game state: "+e.getMessage();
        }
    }


    /**
     * It calls the server logout method to log the player out.
     *
     * @param params the words following logout
     * @return the resulting string
     * @throws ResponseException if logout goes wrong
     */
    private String logout(String... params) throws ResponseException {
        try {
            LogoutRequest logoutRequest = new LogoutRequest(authToken);
            server.logout(logoutRequest);
            authToken = null;
            return SET_TEXT_COLOR_BLUE+"Successfully logged out.";
        } catch (ResponseException e) {
            return SET_TEXT_COLOR_RED+"Logout Failed: "+e.getMessage();
        }
    }

    /**
     * This is just a helper screen to show the user
     * all the commands they can run.
     *
     * @return a string that has a list of commands
     */
    private String help() {
        return SET_TEXT_COLOR_BLUE+"create <NAME>"+
                SET_TEXT_COLOR_RED+" - a game "+SET_TEXT_COLOR_BLUE+
                "\n\tlist"+ SET_TEXT_COLOR_RED+" - games"+
                SET_TEXT_COLOR_BLUE+"\n\twatch <ID> "+SET_TEXT_COLOR_RED+" - a game"+
                SET_TEXT_COLOR_BLUE+"\n\tjoin <ID> [WHITE|BLACK] "+SET_TEXT_COLOR_RED+" - a game"+
                SET_TEXT_COLOR_BLUE+"\n\tlogout "+SET_TEXT_COLOR_RED+"- when you are done"+
                SET_TEXT_COLOR_BLUE+"\n\tquit"+SET_TEXT_COLOR_RED+" - playing chess"+
                SET_TEXT_COLOR_BLUE+"\n\thelp"+SET_TEXT_COLOR_RED+" - with possible commands";
    }

    /**
     * This just passes in an authToken, to set or
     * reset the current authToken
     *
     * @param authToken the authentication token
     */
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    /**
     * This just returns whether a player can move to the in-game commands.
     * This is only possible if it currently is in post-login, and
     * the user successfully joined as a player or observer.
     *
     * @return true if they can move
     */
    public boolean isTransferInGame() {
        return transferInGame;
    }

    /**
     * This keeps track of the player's color
     * @return true if the player is white
     */
    public boolean isWhiteOrBlack() {
        return isWhite;
    }

    public int getGameID() {
        return gameID;
    }
    /**
     * This updates whether player is still allowed to transfer to in-game
     * (useful for when they go back from in-game, and they
     * need to be blocked for entering in-game until they enter more
     * commands)
     * @param stillCanTransfer whether the player can enter in-games commands
     */
    public void changeTransfer(boolean stillCanTransfer) {
        transferInGame = stillCanTransfer;
    }
}
