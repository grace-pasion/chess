package executers;

import java.util.Arrays;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

/**
 * This handles in game user commands while they are in an
 * active chess game. Right now, it only supports basic commands,
 * but I will expand on it in later phases.
 */
public class InGame {
    /**
     * The string representing the server URL
     */
    private final String serverUrl;

    /**
     * This is just a constructor to make sure we have the
     * right server URL
     * @param serverUrl a string represent the server URL
     */
    public InGame(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    /**
     * This goes through the user input
     * and evalautes the command to see how to process it.
     *
     * @param input the user input
     * @return a string result
     */
    public String eval(String input) {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length >0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch(cmd) {
            case "quit" -> "quit";
            default -> help();
        };
    }

    /**
     * When this is called, it just returns a set of commands
     * a user can type in (it's basically just a help menu)
     *
     * @param params these are just extra words the user
     *               typed it that aren't needed
     * @return a message indicating what you can do
     * for the in-game functionaltiy
     */
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
