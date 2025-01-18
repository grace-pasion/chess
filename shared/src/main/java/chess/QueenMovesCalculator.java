
package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalculator implements PieceMovesCalculator {

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


