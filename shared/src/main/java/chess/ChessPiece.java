package chess;

import java.util.Collection;
import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    public ChessGame.TeamColor pieceColor;
    public PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        if (pieceColor == null || type == null) {
            throw new IllegalArgumentException("The chess pieces and color can't be null");
        }
        this.pieceColor = pieceColor;
        this.type = type;

    }


    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
        //throw new RuntimeException("Not implemented");
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
        //throw new RuntimeException("Not implemented");
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        //make sure to implement this
         if (type == PieceType.KNIGHT) {
            return new KnightMovesCalculator().pieceMoves(board, myPosition);
         } else if (type == PieceType.ROOK) {
             return new RookMovesCalculator().pieceMoves(board, myPosition);
         } else if (type == PieceType.BISHOP) {
             return new BishopMovesCalculator().pieceMoves(board, myPosition);
         }
        return new ArrayList<>();
        //throw new RuntimeException("Not implemented");
    }


    @Override
    public boolean equals(Object o) {
        //made by code -> generate -> equals/hashCode
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        //made by code -> generate -> equals/hashCode
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        //code -> generate -> toString
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }

}
