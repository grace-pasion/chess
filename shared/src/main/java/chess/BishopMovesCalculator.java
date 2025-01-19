package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * a class that uses the PieceMoveCalculator interface.
 * It calculates all the possible moves a bishop
 * can make.
 */
public class BishopMovesCalculator implements PieceMovesCalculator {


    /**
     * This method loops through the steps a bishop can make.
     * If that move is valid, it will add it to the list.
     *
     * @param board the ChessBoard that we are currently working on
     * @param position the current position of the bishop
     * @return a collection of all the possible moves the bishop can make
     */
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> finalBishop = new ArrayList<ChessMove>();

        int row = position.getRow();
        int col = position.getColumn();

        int[][] possibleMoves = {
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        for (int[] direction : possibleMoves) {
            int moveRow = row;
            int moveCol = col;
            boolean occupied = false;
            while (!occupied) {
                moveRow += direction[0];
                moveCol += direction[1];

                //inBounds?
                if (moveRow < 1 || moveRow > 8 || moveCol < 1 || moveCol > 8) {
                    break;
                }

                ChessPosition newPos = new ChessPosition(moveRow, moveCol);
                ChessPiece occupier = board.getPiece(newPos);

                if (occupier == null) {
                    finalBishop.add(new ChessMove(position, newPos, null));
                } else {
                    if (occupier.getTeamColor() != board.getPiece(position).getTeamColor()) {
                        finalBishop.add(new ChessMove(position, newPos, null));
                    }
                    occupied = true;
                }
            }
        }
        return finalBishop;
    }
}
