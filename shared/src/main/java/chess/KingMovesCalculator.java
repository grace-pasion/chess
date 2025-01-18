package chess;
import java.util.ArrayList;
import java.util.Collection;


public class KingMovesCalculator implements PieceMovesCalculator {

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
                if (!riskyMove(board, move,board.getPiece(position).getTeamColor())) {
                    kingFinal.add(move);
                }
            }


        }
        return kingFinal;
    }

    private boolean riskyMove(ChessBoard board, ChessMove move,ChessGame.TeamColor color) {
        return true;
    }
}
