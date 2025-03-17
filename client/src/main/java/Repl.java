import replExecuters.InGame;
import replExecuters.PostLogin;
import replExecuters.PreLogin;


import java.util.Scanner;

import server.Server;
import ui.ChessBoardRender;

import static ui.EscapeSequences.*;

public class Repl {
    private final PreLogin preLogin;
    private final PostLogin postLogin;
    private final InGame inGame;
    private String currentState;
    private String printStatement;
    private String authToken;
    private boolean isWhite;
    private String[][] chessBoard = new String[8][8];
    private ChessBoardRender render;

    public Repl(String serverUrl) {
        preLogin = new PreLogin(serverUrl);
        postLogin = new PostLogin(serverUrl);
        inGame = new InGame(serverUrl);
        render = new ChessBoardRender(chessBoard);
        this.currentState = "preLogin";
        this.printStatement = "[LOGGED_OUT] >>> ";
    }

    public void run() {
        printIntro();
        var result = "";
        while (!result.equals("quit") || !currentState.equalsIgnoreCase("preLogin") ) {
            if (currentState.equals("preLogin")) {
                result = handlePreLogin();
            } else if (currentState.equals("postLogin")) {
                result = handlePostLogin();
            } else {
                result = handleInGame();
            }
        }
    }

    private String handlePreLogin() {
        printStatement = "[LOGGED_OUT] >>> ";
        try {
            String line = initialize();
            String result = preLogin.eval(line);
            if (result.contains("Successfully logged in.")
                    || result.contains("Successfully registered.")) {
                authToken = preLogin.getAuthToken();
                currentState = "postLogin";
                postLogin.setAuthToken(authToken);
            }
            System.out.print("\t"+SET_TEXT_COLOR_GREEN+result);
            return result;
        } catch (Throwable e) {
            var msg = e.toString();
            System.out.print(msg);
            return "Error";
        }
    }

    private String handlePostLogin() {
        printStatement = "[LOGGED_IN] >>> ";
        String line = initialize();
        String result = postLogin.eval(line);
        if (postLogin.isTransferInGame()) {
            currentState = "inGame";
            isWhite = postLogin.isWhiteOrBlack();
            render.initializeBoard(isWhite);
        }
        if (result.equalsIgnoreCase("quit")
                || result.contains("Successfully logged out")) {
            currentState = "preLogin";
        }
        System.out.print("\t"+SET_TEXT_COLOR_GREEN+result);
        return result;
    }

    private String handleInGame() {
        printStatement = "[IN_GAME] >>> ";
        //need to change logic for phase 6:
        render.drawChessBoard(System.out, isWhite);
        String line = initialize();
        String result = inGame.eval(line);
        if (result.equalsIgnoreCase("quit")) {
            currentState = "postLogin";
            postLogin.changeTransfer(false);
        }
        System.out.println("\t"+SET_TEXT_COLOR_GREEN+result);
        return result;
    }

    private void printIntro() {
        System.out.println("\uD83D\uDC51 Welcome to 240 chess. Type Help tp get started. \uD83D\uDC51");
        System.out.println("Options:");
        System.out.println("Login as an existing user: \"l\", \"login\" <USERNAME> <PASSWORD>");
        System.out.println("Register a new user: \"r\", \"register\" <USERNAME> <PASSWORD> <EMAIL>");
        System.out.println("Exit the program: \"q\", \"quit\"");
        System.out.println("Print this message: \"h\", \"help\"");
    }

    private String initialize() {
        Scanner scanner = new Scanner(System.in);
        System.out.println();
        System.out.print(RESET_TEXT_COLOR+printStatement);
        return scanner.nextLine();
    }

}
