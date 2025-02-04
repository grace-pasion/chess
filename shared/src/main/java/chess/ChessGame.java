package chess;

import java.util.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    /**
     * This is the current chessBoard the game
     * is being played on
     */
    private ChessBoard board;

    /**
     * This is the current color whose turn it is
     */
    private TeamColor teamTurn;

    /**
     * This is just the constructor that gets a new clean reset board
     * ,and makes the initial starting color white.
     */
    public ChessGame() {
        this.board = new ChessBoard();
        board.resetBoard();
        this.teamTurn = TeamColor.WHITE;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
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
     * Gets a valid moves for a piece at the given location. It loops through all the possible
     * piece moves from the pieceMoves function. It then checks to see if that move
     * is possible without causing the player to be in check
     * by simulating the move on a clonedBoard.
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return new ArrayList<>();
        }
        Collection<ChessMove> allPosMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> safeMoves = new ArrayList<>();
        for (ChessMove move : allPosMoves) {
            ChessPosition newPos = move.getEndPosition();
            ChessPosition ogPos = move.getStartPosition();
            ChessBoard clonedBoard = board.clone();
            clonedBoard.movePiece(ogPos, newPos);

            if (!isInCheck(piece.getTeamColor(), clonedBoard)) {
                safeMoves.add(move);
            }
        }
        return safeMoves;
    }



    /**
     * Makes a move in a chess game. It grabs the starting and ending position of that move, and the
     * current piece making that move. If you can move like that,
     * it updates the board by moving the piece. It also handles pawn upgrades and updating
     * the turn.
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid (No piece found, not current color's turn
     * ,it is not a valid move, or would cause a check)
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

        updatePawn(piece, move, endPos);

        //updating turn
        if (teamTurn == ChessGame.TeamColor.WHITE) {
            teamTurn = ChessGame.TeamColor.BLACK;
        } else {
            teamTurn = ChessGame.TeamColor.WHITE;
        }

    }


    /**
     * Determines if the given team is in check. It does this by calling kingLocation to
     * locate the king. Then it determines if it is in check by calling risky moves.
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = kingLocation(teamColor, board);
        return riskyMove( kingPos, teamColor, board);
    }

    /**
     * This is a function overload, so it is the same as the other isInCheck where
     * it just determines if the player is in check. Expect this one takes in the clonedBoard.
     * It locates the king by using the king location function and checks if it is a risky move
     * by calling riskyMove.
     *
     * @param teamColor the current team's color
     * @param clonedBoard the copy of the current board
     * @return True if it is in check
     */
    public boolean isInCheck(TeamColor teamColor, ChessBoard clonedBoard) {
        ChessPosition kingPos = kingLocation(teamColor, clonedBoard);
        return riskyMove(kingPos, teamColor, clonedBoard);
    }

    /**
     * Determines if the given team is in checkmate. It does this by making sure it
     * is in check. Then it calls checkALlPieces to see if there are any moves they
     * could make that would stop the king from being in CheckMate.
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }
        return checkAllPieces(teamColor);
    }


    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves. It checks if it is not in check. Then it checks all the pieces to see if
     * there is a possible move to not put it in stalemate.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        return checkAllPieces(teamColor);
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

    /**
     * This function loops through the whole board. It then checks if the square is not
     * empty. If it is not empty, it checks if that piece is the current teamColor and
     * a king. If it is so, it returns that position.
     *
     * @param teamColor the current team's color
     * @param board the current chessBoard
     * @return the position of the current King
     */
    private ChessPosition kingLocation(TeamColor teamColor, ChessBoard board) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece == null) {
                    continue;
                }
                if (piece.getPieceType() == (ChessPiece.PieceType.KING)
                        && piece.getTeamColor() == teamColor) {
                    return pos;
                }
            }
        }
        throw new IllegalArgumentException("King not found for team "+teamColor);

    }

    /**
     *This function loops through the board. It takes in the king Position, the current color,
     * and the chess board. If the position is not empty and the piece is the enemy's piece,
     * then it grabs all the enemy's move. If that enemy piece can move to the king position and
     * capture the king, then it is a risky move.
     *
     * @param kingPos the position of the current king
     * @param color the color of the current player
     * @param board the chessBoard we are looking at
     * @return true if it is a risky move
     */
    private boolean riskyMove(ChessPosition kingPos,ChessGame.TeamColor color, ChessBoard board) {
        for (int i = 1; i <= 8; i++) { //row
            for (int j =1; j<=8; j++) { //col
                ChessPosition pos = new ChessPosition(i,j);
                ChessPiece piece = board.getPiece(pos);
                if (piece == null || piece.getTeamColor() == color) {
                    continue;
                } else {
                    Collection<ChessMove> enemyMoves  = piece.pieceMoves(board, pos);

                    for (ChessMove move : enemyMoves) {
                        if (move.getEndPosition().equals(kingPos)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * THis function takes in the current team color. It loops through
     * the whole board. It grabs that position. If that position is
     * not empty, and it's the color of the current team's turn, then it
     * checks all its move. It simulates the move. If the move
     * is able to stop it from being in check then it is safe.
     *
     * @param teamColor the current team's color
     * @return True if it is still in check
     */
    private boolean checkAllPieces(TeamColor teamColor) {
        for (int row = 1; row<= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> posMoves = piece.pieceMoves(board, pos);
                    for (ChessMove move : posMoves) {
                        ChessBoard clonedBoard = board.clone();
                        clonedBoard.movePiece(move.getStartPosition(), move.getEndPosition());
                        if (!isInCheck(teamColor, clonedBoard)){
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * The function sees if a piece is a pawn. If so, it checks whether that piece is
     * at the opposite end of the board. If so, it can get promoted, so it promotes the piece.
     *
     * @param piece the current chessPiece we want to see if it is a pawn
     * @param move the move of that piece
     * @param end the ending position of that piece
     */
    private void updatePawn(ChessPiece piece, ChessMove move, ChessPosition end) {
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            int row;
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                row = 8;
            } else {
                row = 1;
            }
            if (end.getRow() == row) {
                ChessPiece newPiece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
                board.addPiece(end, newPiece);
            }
        }
    }

}
