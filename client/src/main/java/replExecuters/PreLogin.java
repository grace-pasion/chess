package replExecuters;
import java.util.Arrays;

import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;
import server.ServerFacade;
import server.exception.ResponseException;

import static ui.EscapeSequences.*;

/**
 * This class handles authentication/pre-login commands
 * like register, login, etc
 */
public class PreLogin {
    private final String serverUrl;
    private final ServerFacade server;
    private String authToken;

    /**
     *This is just the constructor that constructs a server
     * with the same URL
     *
     * @param serverUrl a string representing the server URL
     */
    public PreLogin(String serverUrl) {
        this.serverUrl = serverUrl;
        server = new ServerFacade(serverUrl);
    }

    /**
     * This evaluates the given user input and then executes that
     * command
     * @param input the user's input
     * @return the resulting string
     */
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

    /**
     * This checks that it has the correct input for username, password, and email.
     * Then it calls the server and gets the register result.
     *
     * @param params the username, password, and email in forms of Strings
     * @return the resulting string
     * @throws ResponseException if register fails
     */
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


    /**
     * This checks to make sure the user correctly
     * passed in the username and password. Then it
     * calls the server and grabs the login result from it.
     *
     * @param params the useranme and the password
     * @return the resulting string
     * @throws ResponseException if login fails
     */
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

    /**
     * Shows the user all the commands they can make
     *
     * @return a string of the possible commands
     * @throws ResponseException if for some reason this function fails
     */
    public String help() throws ResponseException {
        return SET_TEXT_COLOR_BLUE+"register <USERNAME> <PASSWORD> <EMAIL>"+
                SET_TEXT_COLOR_RED+" - to create an account"+SET_TEXT_COLOR_BLUE+
                "\n\tlogin <USERNAME> <PASSWORD>"+ SET_TEXT_COLOR_RED+" - to play chess"+
                SET_TEXT_COLOR_BLUE+"\n\tquit"+SET_TEXT_COLOR_RED+" - playing chess"+
                SET_TEXT_COLOR_BLUE+"\n\thelp"+SET_TEXT_COLOR_RED+" - with possible commands";
    }

    /**
     *
     * @return the authentication token
     */
    public String getAuthToken() {
        return authToken;
    }
}
