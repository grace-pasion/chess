package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        return new ArrayList<>();
    }

    //most complicated cause have to think about it moving in
    // all different directions and have to consider it's color
}
