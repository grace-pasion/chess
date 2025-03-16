package replExecuters;
import java.util.Arrays;
import server.exception.ResponseException;

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
        return "Hello world";
    }
}
