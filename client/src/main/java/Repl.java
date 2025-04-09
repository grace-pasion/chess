import chess.ChessBoard;
import chess.ChessPiece;
import executers.InGame;
import executers.PostLogin;
import executers.PreLogin;


import java.util.Scanner;

import facade.exception.ResponseException;
import ui.ChessBoardRender;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import websocketFacade.NotificationHandler;
import chess.ChessGame;
import websocketFacade.WebSocketFacade;

import static ui.EscapeSequences.*;
import static websocket.messages.ServerMessage.ServerMessageType.ERROR;
import static websocket.messages.ServerMessage.ServerMessageType.NOTIFICATION;

/**
 * This is my read eval print loop class. It handles three phases, which
 * includes prelogin, postlogin, and ingame functionality.
 */
public class Repl implements NotificationHandler {
    private final PreLogin preLogin;
    private final PostLogin postLogin;
    private final InGame inGame;
    private String currentState;
    private String printStatement;
    private String authToken;
    private boolean isWhite;
    private String[][] chessBoard = new String[8][8];
    private ChessBoardRender render;
    private ChessGame game;
    private WebSocketFacade ws;

    //DEAL WITH PAWN PROMOTION

    /**
     * Thid id just my constructor which initializes my REPL with
     * the given server URL, and it sets up prelogin, postlogin,
     * and ingame.
     * @param serverUrl the server url to connect to
     */
    public Repl(String serverUrl) throws ResponseException {
        preLogin = new PreLogin(serverUrl);
        ws = new WebSocketFacade(serverUrl, this);
        postLogin = new PostLogin(serverUrl, this, ws);
        inGame = new InGame(serverUrl, this, ws);
        render = new ChessBoardRender(chessBoard);

        this.currentState = "preLogin";
        this.printStatement = "[LOGGED_OUT] >>> ";
    }

    /**
     * This runs the REPL transitioning between prelogin,
     * postlogin, and ingame. It stops if the user quits in prelogin.
     */
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

    /**
     * This handles the prelogin state. It
     * reads in the line the user typed, and then does some work
     * behind the scenes. It then gives the result of this behind the scenes
     * work. If the player is trying to transition to quit or postLogin,
     * it updates it here.
     * @return a string of the result
     */
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

    /**
     *  This handles the postLogin things (after the user logins/registers, but
     *  before they enter a game). It reads in the line the user typed, and does
     *  some work behind the scenes. It handles the transition from postlogin to
     *  prelogin or ingame. It also returns the results as string.
     *
     * @return a string of the result
     */
    private String handlePostLogin() {
        printStatement = "[LOGGED_IN] >>> ";
        String line = initialize();
        String result = postLogin.eval(line);
        if (postLogin.isTransferInGame()) {
            currentState = "inGame";
            isWhite = postLogin.isWhiteOrBlack();
            inGame.setAuthToken(authToken);
            int gameID = postLogin.getGameID();
            inGame.setGameID(gameID);
            //render.initializeBoard(isWhite);
        }
        if (result.equalsIgnoreCase("quit")
                || result.contains("Successfully logged out")) {
            currentState = "preLogin";
            if (result.equalsIgnoreCase("quit")) {
                result = "back to logged-out";
            }
        }
        System.out.print("\t"+SET_TEXT_COLOR_GREEN+result);
        return result;
    }

    /**
     * This handles the inGame things. It reads in the line the user typed,
     * and does work to it. It handles if they want to exit the game, and also returns
     * the results as a string
     * @return a string of the result
     */
    private String handleInGame() {
        printStatement = "[IN_GAME] >>> ";
        //need to change logic for phase 6:
        //render.drawChessBoard(System.out, isWhite);
        //inGame.setGame(game);
        inGame.setIsWhite(isWhite);
        String line = initialize();
        String result = inGame.eval(line);
        boolean adios = inGame.getAdios();
        if (adios) {
            currentState = "postLogin";
            postLogin.changeTransfer(false);
        }
        System.out.println("\t"+SET_TEXT_COLOR_GREEN+result);
        return result;
    }


    /**
     * This is just a bunch of print statements that will appear to the user
     * when they initially start the server
     */
    private void printIntro() {
        System.out.println("\uD83D\uDC51 Welcome to 240 chess. Type Help tp get started. \uD83D\uDC51");
        System.out.println("Options:");
        System.out.println("Login as an existing user: \"l\", \"login\" <USERNAME> <PASSWORD>");
        System.out.println("Register a new user: \"r\", \"register\" <USERNAME> <PASSWORD> <EMAIL>");
        System.out.println("Exit the program: \"q\", \"quit\"");
        System.out.println("Print this message: \"h\", \"help\"");
    }

    /**
     * This is what is printed out for each iteration of the
     * while loop. It reads in the user's input and prints out the
     * prompt thing that appears to the left when the user types
     * @return a string of the user inputs
     */
    private String initialize() {
        Scanner scanner = new Scanner(System.in);
        System.out.println();
        System.out.print(RESET_TEXT_COLOR+printStatement);
        return scanner.nextLine();
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case ERROR -> errorMessage((ErrorMessage) message);
            case NOTIFICATION -> notification((NotificationMessage) message);
            case LOAD_GAME -> loadGame((LoadGameMessage) message);
        }
    }

    @Override
    public void loadGame(LoadGameMessage loadGameMessage) {
        this.game = loadGameMessage.getGame();
        inGame.setGame(game);
        //issue is that newBoard sends with white always ontop. Need
        // to flip it if it is white
        this.isWhite = postLogin.isWhiteOrBlack();
        ChessBoard newBoard = loadGameMessage.getGame().getBoard();
        render.setBoard(newBoard, isWhite);

        render.drawChessBoard(System.out, isWhite);
    }

    @Override
    public void notification(NotificationMessage notificationMessage) {
        System.out.println(SET_TEXT_COLOR_BLUE + notificationMessage.getMessage());
    }

    @Override
    public void errorMessage(ErrorMessage errorMessage) {
        System.out.println(SET_TEXT_COLOR_RED + errorMessage.getErrorMessage());
    }





}
