package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    /**
     * The row of a piece
     */
    private final int row;

    /**
     * The column of a piece
     */
    private final int col;

    /**
     * This is just the constructor for the class
     *
     * @param row the current row
     * @param col the current column
     */
    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row  ;
        //throw new RuntimeException("Not implemented");
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col ;
        //throw new RuntimeException("Not implemented");
    }

    /**
     * Sees if two positions are the same
     * @param o an object
     * @return if they are equal
     */
    @Override
    public boolean equals(Object o) {
        //generates -> code -> equals/hash
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) o;
        return row == that.row && col == that.col;
    }

    /**
     *
     * @return the hashCode
     */
    @Override
    public int hashCode() {
        //generates -> code -> equals/hash
        return Objects.hash(row, col);
    }


    /**
     *
     * @return a string representation of the class
     */
    @Override
    public String toString() {
        //generates -> code -> toString
        return "ChessPosition{" +
                "row=" + row +
                ", col=" + col +
                '}';
    }
}
