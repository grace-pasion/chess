package chess;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A class that uses the interface of the PieceMovesCalculator
 * It calculates all the possible moves a king can make
 */
public class KingMovesCalculator implements PieceMovesCalculator {

    /**
     * It iterators through the possible moves can make.
     * It will add it to the list if it is possible for
     *  the king to move there
     *
     * @param board the current chessBoard
     * @param position the current position of the king
     * @return a collection of all the moves a chess board can make
     */
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> kingFinal = new ArrayList<ChessMove>();

        int row = position.getRow();
        int col = position.getColumn();

        int[][] possibleMoves = {
                {-1,1}, {0,1}, {1,1}, {-1,0}, {1,0}, {-1,-1}, {0,-1}, {1,-1}
        };

        for (int[] direction: possibleMoves) {
            int newRow = row + direction[0];
            int newCol = col + direction[1];

            if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8) {
                continue;
            }

            ChessPosition newPos = new ChessPosition(newRow, newCol);
            ChessPiece occupier = board.getPiece(newPos);

            if (occupier == null || occupier.getTeamColor() != board.getPiece(position).getTeamColor()) {
                ChessMove move = new ChessMove(position, newPos, null);
                kingFinal.add(move);
            }


        }
        return kingFinal;
    }
}
