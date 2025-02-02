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
            return new ArrayList<>();
        }
        Collection<ChessMove> allPosMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> safeMoves = new ArrayList<>();
        for (ChessMove move : allPosMoves) {
            ChessPosition newPos = move.getEndPosition();
            ChessPosition ogPos = move.getStartPosition();
            ChessBoard clonedBoard = board.clone();
            clonedBoard.movePiece(ogPos, newPos);

            if (isInCheckCloned(piece.getTeamColor(), clonedBoard)) {
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
                ChessPiece newPiece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
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
        ChessPosition kingPos = kingLocation(teamColor, board);
        //I am just reusing my code from phase 0
        //Because I realized I already dealt with that
        return riskyMove( kingPos, teamColor, board);
    }

    public boolean isInCheckCloned(TeamColor teamColor, ChessBoard clonedBoard) {
        ChessPosition kingPos = kingLocation(teamColor, clonedBoard);
        //I am just reusing my code from phase 0
        //Because I realized I already dealt with that
        return !riskyMove(kingPos, teamColor, clonedBoard);
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
        for (int row = 1; row<= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> posMoves = piece.pieceMoves(board, pos);
                    for (ChessMove move : posMoves) {
                        ChessBoard clonedBoard = board.clone();
                        clonedBoard.movePiece(move.getStartPosition(), move.getEndPosition());
                        if (isInCheckCloned(teamColor, clonedBoard)){
                            return false;
                        }
                    }
                }
            }
        }
        return true;
        /*ChessPosition kingPos = kingLocation(teamColor);
        ChessPiece kingPiece = board.getPiece(kingPos);
        Collection<ChessMove> kingMoves = kingPiece.pieceMoves(board, kingPos);
        for (ChessMove move : kingMoves) {
            ChessPosition newPos = move.getEndPosition();
            boolean isRisky = riskyMove( newPos, teamColor, board);
            if (!isRisky) {
                return false;
            }
        }
        return true; */
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

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece == null || piece.getTeamColor() != teamColor) {
                    continue;
                }
                Collection<ChessMove> moves = piece.pieceMoves(board, pos);
                for (ChessMove move : moves) {
                    ChessPosition newPos = move.getEndPosition();
                    if (!riskyMove(newPos, teamColor, board)) {
                        return false;
                    }
                }

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
     * This method takes in a position a king can move to.
     * It then decides if that is a risky decision. It will do this by iterating
     * over the whole chessboard. Once it finds a piece, it will see if it is a pawn.
     * If it is a pawn, it will make sure it cannot diagonally get it. If it is not
     * a pawn, it will make sure the collection of moves it can make will not harm
     * it.
     *
     * @param kingPos the position of the piece we are testing
     * @param color the color of the king
     * @return whether it is risky to move
     */
    private boolean riskyMove(ChessPosition kingPos,ChessGame.TeamColor color, ChessBoard board) {
        for (int i = 1; i <= 8; i++) { //row
            for (int j =1; j<=8; j++) { //col
                ChessPosition pos = new ChessPosition(i,j);
                ChessPiece piece = board.getPiece(pos);
                if (piece == null || piece.getTeamColor() == color) {
                    continue;
                } else if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                    int direction;
                    if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        direction = 1;
                    } else {
                        direction = -1;
                    }

                    int pawnRow = pos.getRow();
                    int pawnCol = pos.getColumn();

                    ChessPosition leftDiagonal = new ChessPosition(pawnRow+ direction, pawnCol -1);
                    ChessPosition rightDiagonal = new ChessPosition(pawnRow + direction, pawnCol +1);

                    if (kingPos.equals(leftDiagonal) || kingPos.equals(rightDiagonal)) {
                        return true;
                    }

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


}
