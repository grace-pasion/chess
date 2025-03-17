package replExecuters;
import java.util.Arrays;

import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;
import server.ServerFacade;
import server.exception.ResponseException;

import static ui.EscapeSequences.*;

public class PreLogin {
    private final String serverUrl;
    private final ServerFacade server;
    private String authToken;

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
            RegisterResult result = server.register(request);
            authToken = result.authToken();
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
            LoginResult result = server.login(request);
            authToken = result.authToken();
            return SET_TEXT_COLOR_BLUE+"Successfully logged in.";
        } catch (ResponseException e) {
            return SET_TEXT_COLOR_RED + "Login failed: " + e.getMessage();
        }
    }

    public String help() throws ResponseException {
        return SET_TEXT_COLOR_BLUE+"register <USERNAME> <PASSWORD> <EMAIL>"+
                SET_TEXT_COLOR_RED+" - to create an account"+SET_TEXT_COLOR_BLUE+
                "\n\tlogin <USERNAME> <PASSWORD>"+ SET_TEXT_COLOR_RED+" - to play chess"+
                SET_TEXT_COLOR_BLUE+"\n\tquit"+SET_TEXT_COLOR_RED+" - playing chess"+
                SET_TEXT_COLOR_BLUE+"\n\thelp"+SET_TEXT_COLOR_RED+" - with possible commands";
    }

    public String getAuthToken() {
        return authToken;
    }


    /* //DELETE LATER, JUST FOR DEBUGGING PURPOSES
    public static void main(String[] args) throws ResponseException {
        PreLogin preLogin = new PreLogin("Doesn't matter");
        System.out.println(preLogin.help());
    } */
}
