package replExecuters;
import java.util.Arrays;
import server.exception.ResponseException;

import static ui.EscapeSequences.*;

public class PreLogin {
    private final String serverUrl;
    public PreLogin(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length >0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch(cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

    public String register(String... params) throws ResponseException {
        return "Hellow world";
    }

    public String login(String... params) throws ResponseException {
        return "Hello world";
    }

    public String help() throws ResponseException {
        return SET_TEXT_COLOR_BLUE+"register <USERNAME> <PASSWORD> <EMAIL>"+
                SET_TEXT_COLOR_RED+" - to create an account"+SET_TEXT_COLOR_BLUE+
                "\nlogin <USERNAME> <PASSWORD>"+ SET_TEXT_COLOR_RED+" - to play chess"+
                SET_TEXT_COLOR_BLUE+"\nquit"+SET_TEXT_COLOR_RED+" - playing chess"+
                SET_TEXT_COLOR_BLUE+"\nhelp"+SET_TEXT_COLOR_RED+" - with possible commands";
    }

    /* //DELETE LATER, JUST FOR DEBUGGING PURPOSES
    public static void main(String[] args) throws ResponseException {
        PreLogin preLogin = new PreLogin("Doesn't matter");
        System.out.println(preLogin.help());
    } */
}
