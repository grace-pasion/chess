package chess;
import java.util.ArrayList;
import java.util.Collection;


public class KingMovesCalculator implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        return new ArrayList<>();
    }
}
