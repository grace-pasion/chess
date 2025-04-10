package executers;

import chess.*;
import facade.exception.ResponseException;
import model.GameData;
import websocket.messages.NotificationMessage;
import websocketFacade.NotificationHandler;
import websocketFacade.WebSocketFacade;
import ui.ChessBoardRender;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;

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
    private WebSocketFacade webSocketFacade;
    private final NotificationHandler notificationHandler;
    private int gameID;
    private String authToken;
    private ChessGame game;
    private ChessBoardRender render;
    private boolean isWhite;
    private boolean adios;
    private boolean isPlayer;
    /**
     * This is just a constructor to make sure we have the
     * right server URL
     * @param serverUrl a string represent the server URL
     */
    public InGame(String serverUrl, NotificationHandler notificationHandler, WebSocketFacade ws) throws ResponseException {
        this.serverUrl = serverUrl;
        //this.gameID = gameID;
        this.notificationHandler = notificationHandler;
        this.webSocketFacade = ws;
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
            case "highlight" -> highlightMoves(params);
            default -> help();
        };
    }

    private String leave() {
        webSocketFacade.leave(authToken, gameID);
        adios = true;
        webSocketFacade = null;
        return SET_TEXT_COLOR_BLUE + "You have left the game.";
    }

    private String resign() {
        webSocketFacade.resign(authToken, gameID);
        return SET_TEXT_COLOR_BLUE + "You have resigned from the game.";
    }

    private String redraw() {
        render.setBoard(game.getBoard(), isWhite);
        render.drawChessBoard(System.out, isWhite);
        return SET_TEXT_COLOR_BLUE + "ChessBoard has been printed.";
    }

    private String move(String... params) {
        if (params.length != 2) {
            return SET_TEXT_COLOR_RED + "You need to input it as: move <start> <end>";
        }

        if (game.isGameOver()) {
            return SET_TEXT_COLOR_RED + "The game is over. You can't move";
        }
        if (!isPlayer) {
            return SET_TEXT_COLOR_RED + "You are an observer, so you can't make moves";
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


        ChessPosition start = new ChessPosition(startRow, startCol);
        ChessPosition end = new ChessPosition(endRow, endCol);

        ChessMove move = new ChessMove(start, end, null);

        ChessPiece piece = game.getBoard().getPiece(start);

        if (piece == null || piece.getTeamColor() != (isWhite ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK)) {
            return SET_TEXT_COLOR_RED + "You can only move your own pieces!";
        }

        if ((isWhite && endRow == 8 && piece.getPieceType() == ChessPiece.PieceType.PAWN) ||
                (!isWhite && endRow == 1 && piece.getPieceType() == ChessPiece.PieceType.PAWN)) {

            // Ask user to select a promotion piece (e.g., Queen, Rook, Bishop, or Knight)
            ChessPiece.PieceType promotionPiece = askPromotionPieceFromUser();
            if (promotionPiece == null) {
                return SET_TEXT_COLOR_RED + "Invalid promotion piece selected.";
            }

            // Create a new move with the promotion piece
            move = new ChessMove(start, end, promotionPiece);
        }

        if (!game.validMoves(start).contains(move)) {
            return SET_TEXT_COLOR_RED +
                    "This is not a legal move for the selected piece!";
        }
        webSocketFacade.makeMove(authToken, gameID,  move);
        game.getBoard().movePiece(start, end);
        render.setBoard(game.getBoard(), isWhite);
        render.drawChessBoard(System.out, isWhite);
        return SET_TEXT_COLOR_BLUE + "Made move.";
    }

    public ChessPiece.PieceType askPromotionPieceFromUser() {
        System.out.println(SET_TEXT_COLOR_BLUE+"Chose" +
                " Pawn Promotion Piece:\n+Q -> Queen\nR -> Rook\nB -> Bishop\nK -> Knight");
        Scanner scanner = new Scanner(System.in);
        String choice = scanner.nextLine();
        return switch (choice) {
            case "Q" -> ChessPiece.PieceType.QUEEN;
            case "R" -> ChessPiece.PieceType.ROOK;
            case "B" -> ChessPiece.PieceType.BISHOP;
            case "K" -> ChessPiece.PieceType.KNIGHT;
            default -> {
                System.out.println(SET_TEXT_COLOR_RED + "Invalid choice. Please choose Q, R, B, or K.");
                yield askPromotionPieceFromUser();
            }
        };
    }

    private String highlightMoves(String... params) {
        if (params.length != 1) {
            return SET_TEXT_COLOR_RED + "You need to input it as: highlight <chessPosition>";
        }
        if (params[0].length() != 2) {
            return SET_TEXT_COLOR_RED + "Position needs to be <row><col>";
        }


        String startPosition = params[0].toLowerCase();
        int startRow;
        int startCol;
        try {
            startRow = Integer.parseInt(String.valueOf(startPosition.charAt(0)));
            startCol = startPosition.charAt(1) - 'a' + 1;
        } catch (NumberFormatException e) {
            return SET_TEXT_COLOR_RED + "Position needs to be <row><col>";
        }
        if (startRow < 1 || startRow > 8 || startCol < 1 || startCol > 8) {
            return SET_TEXT_COLOR_RED + "Rows must be 1-8 and columns a-h.";
        }
        ChessPosition start = new ChessPosition(startRow, startCol);
        ChessPiece piece;
        if (game.getBoard().getPiece(start) == null) {
            return SET_TEXT_COLOR_RED + "This space is empty";
        } else {
            piece = game.getBoard().getPiece(start);
        }
        render.drawBoardWithMoves(System.out, game.getBoard(),
                start, piece, isWhite);

        return SET_TEXT_COLOR_BLUE + "All the valid moves";
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
                SET_TEXT_COLOR_BLUE+"\n\thighlight <chessPosition>"+
                SET_TEXT_COLOR_RED+" - to get all possible moves"+
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

    public void setIsPlayer(boolean isPlayer) {
        this.isPlayer = isPlayer;
    }
}
