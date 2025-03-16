import replExecuters.InGame;
import replExecuters.PostLogin;
import replExecuters.PreLogin;

import java.util.Scanner;

import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;

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
        this.printStatement = "[LOGGED_OUT] >>>";
    }

    public void run() {
        System.out.println("\uD83D\uDC51 Welcome to 240 chess. Type Help tp get started. \uD83D\uDC51");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit") && !currentState.equalsIgnoreCase("preLogin") ) {
            if (currentState.equals("preLogin")) {
                System.out.println(printStatement);
                String line = scanner.nextLine();
                try {
                    result = preLogin.eval(line);
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

    /* //fOR TESTING PURPOSES ONLY!!!
    public static void main(String[] args) {
        Repl repl = new Repl("this is not needed rn");
        repl.run();
    } */
}
