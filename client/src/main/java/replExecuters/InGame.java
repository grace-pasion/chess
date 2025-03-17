package replExecuters;

import server.exception.ResponseException;

import java.util.Arrays;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class InGame {
    private final String serverUrl;
    public InGame(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length >0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch(cmd) {
            case "quit" -> "quit";
            default -> help();
        };
    }

    public String help(String... params) {
        return SET_TEXT_COLOR_BLUE+"Currently, our software " +
                "\nengineers are working on implementing in-game " +
                "\nfunctionality. Please be patient. " +
                "\nThe only thing you can do is \"quit\" " +
                "\nto exit back to postLogin functionality";
    }

    //PHASE 6:
    //move

    //resign

    //highlight legal moves

    //leave

    //redraw chess board

    //help
}
