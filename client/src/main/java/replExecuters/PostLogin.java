package replExecuters;

import model.GameData;
import request.*;
import result.ListGameResult;
import server.ServerFacade;
import server.exception.ResponseException;

import java.util.ArrayList;
import java.util.Arrays;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public class PostLogin {
    private final ServerFacade server;
    private String authToken;
    private boolean transferInGame;
    private boolean isWhite;

    public PostLogin(String serverUrl) {
        server = new ServerFacade(serverUrl);
        transferInGame = false;
    }


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

    private String join(String... params) throws ResponseException{
        try {
            if (params.length != 2) {
                return SET_TEXT_COLOR_RED+
                        "Invalid parameters. For join games: join <GAME_ID> [WHITE|BLACK]";
            }
            int gameId;
            try {
                gameId = Integer.parseInt(params[0]);
            } catch (NumberFormatException e) {
                return SET_TEXT_COLOR_RED+"The Game ID is supposed to be a number";
            }

            String playerColor = params[1].toUpperCase();
            if (!playerColor.equals("WHITE") && !playerColor.equals("BLACK")) {
                return SET_TEXT_COLOR_RED+"Invalid. Must choose WHITE or BLACK";
            }
            isWhite = playerColor.equals("WHITE");

            JoinGameRequest joinGameRequest = new JoinGameRequest(playerColor, gameId);
            server.joinGame(joinGameRequest, authToken);
            transferInGame = true;
            return SET_TEXT_COLOR_BLUE+
                    "Successfully joined game "+gameId+" as "+playerColor+".";
        } catch (ResponseException e) {
            return SET_TEXT_COLOR_RED+"Join Games Failed: "+e.getMessage();
        }
    }

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

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public boolean isTransferInGame() {
        return transferInGame;
    }

    public boolean isWhiteOrBlack() {
        return isWhite;
    }

    public void changeTransfer(boolean stillCanTransfer) {
        transferInGame = stillCanTransfer;
    }

}
