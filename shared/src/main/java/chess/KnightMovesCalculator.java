package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> finalMoves = new ArrayList<ChessMove>();

        ChessPiece currentPiece = board.getPiece(position);

        int[][] waysToMoves = {
                {1,2}, {1,-2}, {-1, 2}, {-1,-2},
                {2,1}, {2,-1}, {-2, 1}, {-2,-1}
        };

        for (int[] possibleMove: waysToMoves) {
            //iterating through the possible combos
            int newRow = position.getRow() + possibleMove[0];
            int newCol = position.getColumn() + possibleMove[1];

            if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                //checking to see if in bounds
                ChessPosition newPos = new ChessPosition(newRow, newCol);
                ChessPiece occupier = board.getPiece(newPos);

                if (occupier == null || occupier.getTeamColor() != currentPiece.getTeamColor()) {
                    ChessMove move = new ChessMove(position, newPos, currentPiece.getPieceType());
                    finalMoves.add(move);
                }
            }
        }

        return finalMoves;
    }
}
