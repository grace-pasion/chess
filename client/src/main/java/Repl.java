import replExecuters.InGame;
import replExecuters.PostLogin;
import replExecuters.PreLogin;


import java.util.Scanner;

import server.Server;
import static ui.EscapeSequences.*;

public class Repl {
    private final PreLogin preLogin;
    private final PostLogin postLogin;
    private final InGame inGame;
    private String currentState;
    private String printStatement;

    public Repl(String serverUrl) {
        preLogin = new PreLogin(serverUrl);
        postLogin = new PostLogin(serverUrl);
        inGame = new InGame(serverUrl);
        this.currentState = "preLogin";
        this.printStatement = "[LOGGED_OUT] >>> ";
    }

    public void run() {
        printIntro();
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit") || !currentState.equalsIgnoreCase("preLogin") ) {
            if (currentState.equals("preLogin")) {
                System.out.println();
                System.out.print(RESET_TEXT_COLOR+printStatement);
                String line = scanner.nextLine();
                try {
                    result = preLogin.eval(line);
                    if (result.equalsIgnoreCase("Successfully logged in.")
                            || result.equalsIgnoreCase("Successfully registered.")) {
                        currentState = "postLogin";
                    }
                    System.out.print(SET_TEXT_COLOR_GREEN+result);
                } catch (Throwable e) {
                    var msg = e.toString();
                    System.out.print(msg);
                }
            } else if (currentState.equals("postLogin")) {
                //something like if result is quit then currentState = preLogin
            } else {

            }
        }
    }

    public void printIntro() {
        System.out.println("\uD83D\uDC51 Welcome to 240 chess. Type Help tp get started. \uD83D\uDC51");
        System.out.println("Options:");
        System.out.println("Login as an existing user: \"l\", \"login\" <USERNAME> <PASSWORD>");
        System.out.println("Register a new user: \"r\", \"register\" <USERNAME> <PASSWORD> <EMAIL>");
        System.out.println("Exit the program: \"q\", \"quit\"");
        System.out.println("Print this message: \"h\", \"help\"");
    }

    //fOR TESTING PURPOSES ONLY!!!
    public static void main(String[] args) {
        Server server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        // Pass the correct URL to Repl
        Repl repl = new Repl("http://localhost:" + port);
        repl.run();
    }
}
