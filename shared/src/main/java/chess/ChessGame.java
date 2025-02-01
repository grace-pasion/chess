package chess;

import java.util.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor teamTurn;
    public ChessGame() {
        this.board = new ChessBoard();
        board.resetBoard();
        //Set to white, since White is what starts it
        this.teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {

        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {

        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }
        Collection<ChessMove> allPosMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> safeMoves = new ArrayList<>();
        for (ChessMove move : allPosMoves) {
            ChessPosition newPos = move.getEndPosition();
            ChessPosition ogPos = move.getStartPosition();
            ChessBoard clonedBoard = board.clone();
            clonedBoard.movePiece(ogPos, newPos);

            if (!isInCheck(piece.getTeamColor())) {
                safeMoves.add(move);
            }
        }
        return safeMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();
        ChessPiece piece = board.getPiece(startPos);
        //just handling my exceptions
        if (piece == null) {
            throw new InvalidMoveException("No piece is found");
        }
        if (piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Not your color's turn");
        }

        Collection<ChessMove> validMoves = validMoves(startPos);
        if (!validMoves.contains(move)) {
            throw new InvalidMoveException("Cannot move like that");
        }

        board.movePiece(startPos, endPos);

        if (isInCheck(teamTurn)) {
            throw new InvalidMoveException("Would cause the king to be in check");
        }

        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            int row;
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                row = 8;
            } else {
                row = 1;
            }
            if (endPos.getRow() == row) {
                ChessPiece newPiece = new ChessPiece(piece.getTeamColor(), ChessPiece.PieceType.QUEEN);
                board.addPiece(endPos, newPiece);
            }
        }

        //updating turn
        if (teamTurn == ChessGame.TeamColor.WHITE) {
            teamTurn = ChessGame.TeamColor.BLACK;
        } else {
            teamTurn = ChessGame.TeamColor.WHITE;
        }

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = kingLocation(teamColor);
        //I am just reusing my code from phase 0
        //Because I realized I already dealt with that
        return new KingMovesCalculator().getRiskyMove(board, kingPos, teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }
        ChessPosition kingPos = kingLocation(teamColor);
        ChessPiece kingPiece = board.getPiece(kingPos);
        Collection<ChessMove> kingMoves = kingPiece.pieceMoves(board, kingPos);
        for (ChessMove move : kingMoves) {
            ChessPosition newPos = move.getEndPosition();
            boolean isRisky = new KingMovesCalculator().getRiskyMove(board, newPos, teamColor);
            if (!isRisky) {
                return false;
            }
        }
        return true;

    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }

        ChessPosition kingPos = kingLocation(teamColor);
        ChessPiece kingPiece = board.getPiece(kingPos);
        Collection<ChessMove> kingMoves = kingPiece.pieceMoves(board, kingPos);
        for (ChessMove move : kingMoves) {
            ChessPosition newPos = move.getEndPosition();
            boolean isRisky = new KingMovesCalculator().getRiskyMove(board, newPos, teamColor);
            if (!isRisky) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {

        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {

        return board;
    }

    private ChessPosition kingLocation(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece.getPieceType().equals(ChessPiece.PieceType.KING)) {
                    return pos;
                }
            }
        }
        throw new IllegalArgumentException("King not found for team "+teamColor);

    }
}
