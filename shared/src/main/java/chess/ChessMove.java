package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    /**
     * The initial position of the piece
     */
    private final ChessPosition startPosition;

    /**
     * The final position of the piece after the piece
     */
    private final ChessPosition endPosition;

    /**
     * The piece that it transforms to after the move
     * (Really only applicable to pawns after reaching the
     * end of the board).
     */
    private final ChessPiece.PieceType promotionPiece;

    /**
     * This is simply the constructor for the class
     *
     * @param startPosition the initial position of the piece
     * @param endPosition the ending position of the piece
     * @param promotionPiece the piece it turns into after the turn
     */
    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
       return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

    /**
     * Just shows the chessMove has taken place
     * By printing out a statement
     *
     * @return the string ChessMove{}
     */
    @Override
    public String toString() {
        return "ChessMove{}";
    }

    /**
     * Checks to see if an object
     * is equal to the current chessMove
     *
     * @param o another object
     * @return whether the two moves are equal
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(startPosition, chessMove.startPosition)
                && Objects.equals(endPosition, chessMove.endPosition)
                && promotionPiece == chessMove.promotionPiece;
    }

    /**
     * Overrides the hashCode
     *
     * @return the hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(startPosition, endPosition, promotionPiece);
    }
}
