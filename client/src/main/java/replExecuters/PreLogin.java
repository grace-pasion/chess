package replExecuters;
import java.util.Arrays;

import request.LoginRequest;
import request.RegisterRequest;
import server.ServerFacade;
import server.exception.ResponseException;

import static ui.EscapeSequences.*;

public class PreLogin {
    private final String serverUrl;
    private final ServerFacade server;

    public PreLogin(String serverUrl) {
        this.serverUrl = serverUrl;
        server = new ServerFacade(serverUrl);

    }

    public String eval(String input) {
        try {
            //server.clear(); //GET RID OF - JUST FOR TESTING
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length >0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch(cmd) {
                case "register", "r" -> register(params);
                case "login", "l" -> login(params);
                case "quit", "q" -> "quit";
                default -> help();
            };
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

    public String register(String... params) throws ResponseException {
        if (params.length != 3) {
            return SET_TEXT_COLOR_RED + "Invalid parameters. " +
                    "For register: register <USERNAME> <PASSWORD> <EMAIL>";
        }

        RegisterRequest request = new RegisterRequest(params[0], params[1], params[2]);
        try {
            server.register(request);
            return SET_TEXT_COLOR_BLUE+"Successfully registered.";
        } catch (ResponseException e) {
            return SET_TEXT_COLOR_RED + "Registration failed: " + e.getMessage();
        }

    }

    public String login(String... params) throws ResponseException {
        if (params.length != 2) {
            return SET_TEXT_COLOR_RED + "Invalid parameters. " +
                    "For login: login <USERNAME> <PASSWORD> ";
        }

        LoginRequest request = new LoginRequest(params[0], params[1]);
        try {
            server.login(request);
            return SET_TEXT_COLOR_BLUE+"Successfully logged in.";
        } catch (ResponseException e) {
            return SET_TEXT_COLOR_RED + "Login failed: " + e.getMessage();
        }
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
