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
                if (!riskyMove(board, newPos ,board.getPiece(position).getTeamColor())) {
                    kingFinal.add(move);
                }
            }


        }
        return kingFinal;
    }

    private boolean riskyMove(ChessBoard board, ChessPosition kingPos,ChessGame.TeamColor color) {
        for (int i = 1; i <= 8; i++) { //row
            for (int j =1; j<=8; j++) { //col
                ChessPosition pos = new ChessPosition(i,j);
                ChessPiece piece = board.getPiece(pos);
                if (piece == null) {
                    continue;
                } else if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                    int direction;
                    if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        direction = 1;
                    } else {
                        direction = -1;
                    }

                    int pawnRow = pos.getRow();
                    int pawnCol = pos.getColumn();

                    ChessPosition leftDiagonal = new ChessPosition(pawnRow+ direction, pawnCol -1);
                    ChessPosition rightDiagonal = new ChessPosition(pawnRow + direction, pawnCol +1);

                    if (kingPos.equals(leftDiagonal) || kingPos.equals(rightDiagonal)) {
                        return true;
                    }

                } else {
                    if (piece.getTeamColor() != color) {
                        Collection<ChessMove> enemyMoves  = piece.pieceMoves(board, pos);

                        for (ChessMove move : enemyMoves) {
                            if (move.getEndPosition().equals(kingPos)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
