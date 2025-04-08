package chess;

import java.util.Arrays;
import java.util.Objects;


/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Cloneable {

    /**
     * This is the chessboard represented as a 2d array
     */
    private ChessPiece[][] squares = new ChessPiece[8][8];

    /**
     * The constructor for the chessBoard class
     */
    public ChessBoard() {
    }

    public ChessPiece[][] getBoard() {
        return squares;
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                squares[row][col] = null;
            }
        }
        resetPawns();
        resetRooks();
        resetKnight();
        resetBishops();
        resetQueen();
        resetKing();
    }

    /**
     * This resets the pawns in the correct position
     * It is simply a helper function for restBoard()
     */
    public void resetPawns() {
        for (int col = 1; col <= 8; col++) {
            //white
            addPiece(new ChessPosition(2, col),
                    new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));

            //black
            addPiece(new ChessPosition(7 ,col),
                    new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }
    }

    /**
     * This resets the rooks in the correct position
     * It is simply a helper function for restBoard()
     */
    public void resetRooks() {
        //white
        addPiece(new ChessPosition(1, 1),
                new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));

        addPiece(new ChessPosition(1, 8),
                new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));

        //black
        addPiece(new ChessPosition(8, 1),
                new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));

        addPiece(new ChessPosition(8, 8),
                new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));

    }

    /**
     * This resets the knights in the correct position
     * It is simply a helper function for restBoard()
     */
    public void resetKnight() {
        //white
        addPiece(new ChessPosition(1, 2),
                new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));

        addPiece(new ChessPosition(1, 7),
                new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));

        //black
        addPiece(new ChessPosition(8, 2),
                new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));

        addPiece(new ChessPosition(8, 7),
                new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
    }

    /**
     * This resets the bishops in the correct position
     * It is simply a helper function for restBoard()
     */
    public void resetBishops() {
        //white
        addPiece(new ChessPosition(1, 3),
                new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));

        addPiece(new ChessPosition(1, 6),
                new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));

        //black
        addPiece(new ChessPosition(8, 3),
                new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 6),
                new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
    }

    /**
     * This resets the queen in the correct position
     * It is simply a helper function for restBoard()
     */
    public void resetQueen() {
        //white
        addPiece(new ChessPosition(1, 4),
                new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));

        //black
        addPiece(new ChessPosition(8, 4),
                new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
    }

    /**
     * This resets the king in the correct position
     * It is simply a helper function for restBoard()
     */
    public void resetKing() {
        //white
        addPiece(new ChessPosition(1, 5),
                new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));

        //black
        addPiece(new ChessPosition(8, 5),
                new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
    }


    /**
     * This function takes in the starting position and ending position.
     * Then it moves that piece by setting the ending position equal to the piece
     * and the starting position to null.
     *
     * @param startPos the starting position of a piece
     * @param endPos the ending position after a move
     */
    public void movePiece(ChessPosition startPos, ChessPosition endPos) {
        ChessPiece piece = getPiece(startPos);
        if (piece == null) {
            return;
        }
        addPiece(startPos, null);
        addPiece(endPos, piece);
    }

    /**
     * Checks whether two chessboards are the same
     *
     * @param o another object
     * @return true if the chessboards are equal to each other
     */
    @Override
    public boolean equals(Object o) {
        //generated by code -> generate -> equals and hashCode
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }


    /**
     * overrides the hashCode methods for
     * this specific class
     *
     * @return an integer hashCode
     */
    @Override
    public int hashCode() {
        //generated by code -> generate -> equals and hashCode
        return Arrays.deepHashCode(squares);
    }

    /**
     * Turns the squares in the chessboard and prints
     * it out.
     *
     * @return the string of the chessBoard
     */
    @Override
    public String toString() {
        //generated by code -> generate -> toString
        return "ChessBoard{" +
                "squares=" + Arrays.toString(squares) +
                '}';
    }


    /**
     * This overrides the clone method. It makes a deep copy of the
     * current chessBoard
     *
     * @return a deep copy of the current ChessBoard
     */
    @Override
    public ChessBoard clone() {
        try {
            ChessBoard clonedBoard = (ChessBoard)super.clone();
            clonedBoard.squares = new ChessPiece[8][8];
            for (int row =0; row <8 ; row++) {
                for (int col = 0; col < 8; col++) {
                    ChessPiece piece = this.squares[row][col];
                    if (piece != null) {
                        clonedBoard.squares[row][col] = piece;
                    }
                }
            }
            return clonedBoard;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
