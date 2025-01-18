package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator implements PieceMovesCalculator {
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
