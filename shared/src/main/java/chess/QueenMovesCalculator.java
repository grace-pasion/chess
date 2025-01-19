
package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This is a class that's job is to calculate all the possible moves a
 * queen can make
 */
public class QueenMovesCalculator implements PieceMovesCalculator {

    /**
     * Since a queen can do the moves of a rook and bishop,
     * this function calls those two other classes. Then it
     * adds these moves to the queens move. Then it returns the
     * list of all the moves a queen can make.
     *
     * @param board the current chess board
     * @param position the current position of the piece
     * @return a collection of moves a queen can make
     */
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> queenFinal = new ArrayList<ChessMove>();

        RookMovesCalculator rook = new RookMovesCalculator();
        BishopMovesCalculator bishop = new BishopMovesCalculator();


        queenFinal.addAll(rook.pieceMoves(board, position));
        queenFinal.addAll(bishop.pieceMoves(board, position));

        return queenFinal;
    }
}


