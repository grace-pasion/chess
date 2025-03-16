package replExecuters;

import server.ServerFacade;
import server.exception.ResponseException;

import java.util.Arrays;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public class PostLogin {
    private final String serverUrl;
    private final ServerFacade server;

    public PostLogin(String serverUrl) {
        this.serverUrl = serverUrl;
        server = new ServerFacade(serverUrl);
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
        return "Hello World";
    }

    private String create(String... params) throws ResponseException {
        return "Hello World";
    }

    private String join(String... params) throws ResponseException{
        return "Hello World";
    }

    private String watch(String... params) throws ResponseException{
        return "hellow world";
    }

    private String logout(String... params) throws ResponseException {
        return "log out";
    }

    private String help() {
        return SET_TEXT_COLOR_BLUE+"create <NAME>"+
                SET_TEXT_COLOR_RED+" - a game "+SET_TEXT_COLOR_BLUE+
                "\nlist"+ SET_TEXT_COLOR_RED+" - games"+
                SET_TEXT_COLOR_BLUE+"\nobserve <ID> [WHITE|BLACK]"+SET_TEXT_COLOR_RED+" - a game"+
                SET_TEXT_COLOR_BLUE+"\nquit"+SET_TEXT_COLOR_RED+" - playing chess"+
                SET_TEXT_COLOR_BLUE+"\nhelp"+SET_TEXT_COLOR_RED+" - with possible commands";
    }
}
