package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class calculates the knights possible moves
 */
public class KnightMovesCalculator implements PieceMovesCalculator {

    /**
     * It will iterator through the possible moves a knight can make.
     * If the move is possible (ie: the spot is not off the board,
     * empty, or an enemy is there) it will add it to a list of
     * movements it can make
     *
     * @param board the current chessBoard
     * @param position the current position
     * @return a collection of chessMoves the knight can make
     */
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> finalMoves = new ArrayList<ChessMove>();

        ChessPiece currentPiece = board.getPiece(position);

        int[][] waysToMoves = {
                {2,1}, {1,2}, {-1, 2}, {-2,1},
                {-2,-1}, {-1,-2}, {1, -2}, {2,-1}
        };

        //iterating through the possible combos
        for (int[] possibleMove: waysToMoves) {
            int newRow = position.getRow() + possibleMove[0];
            int newCol = position.getColumn() + possibleMove[1];

            //checking to see if in bounds
            if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {

                ChessPosition newPos = new ChessPosition(newRow, newCol);
                ChessPiece occupier = board.getPiece(newPos);

                if (occupier == null || occupier.getTeamColor() != currentPiece.getTeamColor()) {
                    ChessMove move = new ChessMove(position, newPos, null);
                    finalMoves.add(move);
                }
            }
        }

        return finalMoves;
    }
}
