package chess;
import java.util.Collection;

/**
 * this is an interface called pieceMove calculator
 * That will help organize all the possible classes of possible moves
 * from each piece
 */
public interface PieceMovesCalculator {
   public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position);
}
