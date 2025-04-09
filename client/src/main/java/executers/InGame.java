package executers;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import facade.exception.ResponseException;
import model.GameData;
import websocket.messages.NotificationMessage;
import websocketFacade.NotificationHandler;
import websocketFacade.WebSocketFacade;
import ui.ChessBoardRender;

import java.io.PrintStream;
import java.util.Arrays;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

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
    private final WebSocketFacade webSocketFacade;
    private final NotificationHandler notificationHandler;
    private int gameID;
    private String authToken;
    private boolean outOfGame;
    private ChessGame game;
    private ChessBoardRender render;
    private boolean isWhite;
    private boolean adios;
    /**
     * This is just a constructor to make sure we have the
     * right server URL
     * @param serverUrl a string represent the server URL
     */
    public InGame(String serverUrl, NotificationHandler notificationHandler) throws ResponseException {
        this.serverUrl = serverUrl;
        //this.gameID = gameID;
        this.notificationHandler = notificationHandler;
        this.webSocketFacade = new WebSocketFacade(serverUrl, notificationHandler);
        adios = false;
        render = new ChessBoardRender(new String[8][8]);

    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
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
            case "quit", "leave" -> leave();
            case "resign" -> resign();
            case "move" -> move(params);
            case "redraw" -> redraw();
            case "highlight moves" -> highlightMoves();
            default -> help();
        };
    }

    private String leave() {
        webSocketFacade.leave(authToken, gameID);
        adios = true;
        return SET_TEXT_COLOR_BLUE + "You have left the game.";
    }

    private String resign() {
        webSocketFacade.resign(authToken, gameID);
        return SET_TEXT_COLOR_BLUE + "You have resigned from the game.";
    }

    private String redraw() {
        render.setBoard(game.getBoard());
        render.drawChessBoard(System.out, isWhite);
        return SET_TEXT_COLOR_BLUE + "ChessBoard has been printed.";
    }

    private String move(String... params) {
        if (params.length != 2) {
            return SET_TEXT_COLOR_RED + "You need to input it as: move <start> <end>";
        }

        if (params[0].length() != 2 || params[1].length() != 2) {
            return SET_TEXT_COLOR_RED + "Position needs to be <row><col>";
        }
        String startPosition = params[0].toLowerCase();
        String endPosition = params[1].toLowerCase();
        int startRow;
        int startCol;
        int endRow;
        int endCol;
        try {
            startRow = Integer.parseInt(String.valueOf(startPosition.charAt(0)));
            endRow = Integer.parseInt(String.valueOf(endPosition.charAt(0)));
            endCol = endPosition.charAt(1) - 'a' + 1;
            startCol = startPosition.charAt(1) - 'a' + 1;
        } catch (NumberFormatException e) {
            return SET_TEXT_COLOR_RED + "Position needs to be <row><col>";
        }
        if (startRow < 1 || startRow > 8 || startCol < 1 || startCol > 8
                || endRow < 1 || endRow > 8 || endCol < 1 || endCol > 8) {
            return SET_TEXT_COLOR_RED + "Rows must be 1-8 and columns a-h.";
        }

        if ((isWhite && game.getTeamTurn() != ChessGame.TeamColor.WHITE) ||
                (!isWhite && game.getTeamTurn() != ChessGame.TeamColor.BLACK)) {
            return SET_TEXT_COLOR_RED + "It's not your turn to move!";
        }

        //SOMEHOW DEAL WITH PAWN PROMOTION
        //if pawn promotion is true
        //go into a new function
        //this new function will let the user pick the piece they want for pawn promotion
        //then set that current piece to that piece they want
        ChessPosition start = new ChessPosition(startRow, startCol);
        ChessPosition end = new ChessPosition(endRow, endCol);


        ChessMove move = new ChessMove(start, end, null);
        if (!game.validMoves(start).contains(move)) {
            return SET_TEXT_COLOR_RED +
                    "This is not a legal move for the selected piece!";
        }
        webSocketFacade.makeMove(authToken, gameID,  move);
        game.getBoard().movePiece(start, end);
        render.setBoard(game.getBoard());
        render.drawChessBoard(System.out, isWhite);
        return SET_TEXT_COLOR_BLUE + "Made move.";
    }


    private String highlightMoves() {
        return " ";
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
        return SET_TEXT_COLOR_BLUE+"redraw"+
                SET_TEXT_COLOR_RED+" - to redraw the chessboard"+SET_TEXT_COLOR_BLUE+
                "\n\tleave"+ SET_TEXT_COLOR_RED+" - this will remove you from the game"+
                SET_TEXT_COLOR_BLUE+"\n\tmove <start> <end>"+SET_TEXT_COLOR_RED+" - " +
                "will move that piece at that location"+ SET_TEXT_COLOR_BLUE+"\n\tresign"+
                SET_TEXT_COLOR_RED+" - to forfeit and end the game"+
                SET_TEXT_COLOR_BLUE+"\n\thelp"+
                SET_TEXT_COLOR_RED+" - with possible commands";
    }


    public void setGame(ChessGame game) {
        this.game= game;
    }

    public void setIsWhite(boolean isWhite) {
        this.isWhite = isWhite;
    }

    public boolean getAdios() {
        return adios;
    }
}
