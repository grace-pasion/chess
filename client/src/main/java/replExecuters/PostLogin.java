package replExecuters;

import model.GameData;
import request.ListGameRequest;
import request.RegisterRequest;
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
                    (SET_TEXT_COLOR_BLUE + "\tAvailable Games:\n");
            int i = 1;
            for (GameData game : games) {
                result.append("\n");
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

        return "Hello World";
    }

    private String join(String... params) throws ResponseException{
        transferInGame = true;
        return "Hello World";
    }

    private String watch(String... params) throws ResponseException{
        transferInGame = true;
        return "hellow world";
    }

    private String logout(String... params) throws ResponseException {
        return "log out";
    }

    private String help() {
        return SET_TEXT_COLOR_BLUE+"\tcreate <NAME>"+
                SET_TEXT_COLOR_RED+" - a game "+SET_TEXT_COLOR_BLUE+
                "\n\tlist"+ SET_TEXT_COLOR_RED+" - games"+
                SET_TEXT_COLOR_BLUE+"\n\tobserve <ID> [WHITE|BLACK]"+SET_TEXT_COLOR_RED+" - a game"+
                SET_TEXT_COLOR_BLUE+"\n\tquit"+SET_TEXT_COLOR_RED+" - playing chess"+
                SET_TEXT_COLOR_BLUE+"\n\thelp"+SET_TEXT_COLOR_RED+" - with possible commands";
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public boolean isTransferInGame() {
        return transferInGame;
    }

}
