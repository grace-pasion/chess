package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This is class that calculates all the possible
 * movements a pawn can make
 */
public class PawnMovesCalculator implements PieceMovesCalculator {
    /**
     * the row a pawn is on
     */
    private int row;

    /**
     * the column a pawn is on
     */
    private int col;

    /**
     * the current color of the pawn
     */
    private ChessGame.TeamColor currentColor;

    /**
     * This method checks whether a single forward move,
     * a double forward move, or a diagonal capture is valid.
     * If so, it will add it to the list.
     *
     * @param board the current chessboard
     * @param position the current position
     * @return a collection of all the possible moves a pawn can make
     */
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {

        row = position.getRow();
        col = position.getColumn();

        currentColor = board.getPiece(position).getTeamColor();

        //regular one space move
        Collection<ChessMove> pawnFinal = new ArrayList<ChessMove>(oneSpace(board, position));

        //double one from starting position
        if ((currentColor == ChessGame.TeamColor.WHITE && row ==2) ||
                (currentColor == ChessGame.TeamColor.BLACK && row == 7)) {
            pawnFinal.addAll(doubleSpace(board, position));
        }


        //capture diagonally to the right
        pawnFinal.addAll(RightDiagonal(board, position));

        //capture diagonally to the left
        pawnFinal.addAll(leftDiagonal(board, position));

        return pawnFinal;
    }

    /**
     * This method checks to see if the pawn is at the end of the board.
     * If so, it can change into a queen, bishop, rook, or knight
     *
     * @param position the current position of the piece
     * @param color the color of the piece
     * @return whether a pawn can be promoted
     */
    private boolean promoted(ChessPosition position, ChessGame.TeamColor color ) {
        int currentRow = position.getRow();
        if ((color ==ChessGame.TeamColor.WHITE && currentRow == 8)) {
            return true;
        }
        return (color == ChessGame.TeamColor.BLACK && currentRow == 1);
    }


    /**
     * This is a helper function for the pieceMoves function. It checks to see
     * if a one space move is possible for the current pawn. If so, it returns
     * a list which has the one move inside of it
     *
     * @param board the current chessboard
     * @param position the current position of the piece
     * @return a collection containing the one space move
     */
    private Collection<ChessMove> oneSpace(ChessBoard board, ChessPosition position ) {
        Collection<ChessMove> pawnOne = new ArrayList<ChessMove>();
        int regMove = row;
        if (currentColor == ChessGame.TeamColor.WHITE) {
            regMove++;
        } else {
            regMove--;
        }
        if (regMove >= 1 && regMove <= 8) {
            ChessPosition newPos = new ChessPosition(regMove, col);
            ChessPiece piece = board.getPiece(newPos);
            if (piece == null) {
                if (promoted(newPos, board.getPiece(position).getTeamColor())) {
                    pawnOne.add(new ChessMove(position, newPos, ChessPiece.PieceType.QUEEN));
                    pawnOne.add(new ChessMove(position, newPos, ChessPiece.PieceType.BISHOP));
                    pawnOne.add(new ChessMove(position, newPos, ChessPiece.PieceType.ROOK));
                    pawnOne.add(new ChessMove(position, newPos, ChessPiece.PieceType.KNIGHT));
                } else {
                    pawnOne.add(new ChessMove(position, newPos, null));
                }

            }
        }
        return pawnOne;
    }

    /**
     * This is a helper function for the pieceMoves function. It checks to see
     * if a double space move is possible for the current pawn. A double
     * space move is only possible if there is nothing in those two spots, and it
     * is the first move. If so, it returns a list which has the double move inside of it/
     *
     * @param board the current board
     * @param position the current position of the piece
     * @return a collection containing the double space move
     */
    private Collection<ChessMove> doubleSpace(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> doubleOne = new ArrayList<ChessMove>();
        int doubleMove = row;
        int singleMove = row;
        if (currentColor == ChessGame.TeamColor.WHITE) {
            doubleMove+=2;
            singleMove++;
        } else {
            doubleMove-=2;
            singleMove--;
        }

        ChessPosition doublePos = new ChessPosition(doubleMove, col);
        //check to see if noting is blocking it
        ChessPosition singlePos = new ChessPosition(singleMove, col);
        ChessPiece doublePiece = board.getPiece(doublePos);
        ChessPiece singlePiece = board.getPiece(singlePos);
        if (doublePiece == null && singlePiece == null) {
            doubleOne.add(new ChessMove(position, doublePos, null));
        }
        return doubleOne;
    }

    /**
     * It checks to see if an enemy is on the diagonal of the pawn. If so,
     * it can capture it diagonally. It will then add that to a list, which
     * it returns.
     *
     * @param board the current chessBoard
     * @param position the current position of the piece
     * @return a collection containing the diagonal move
     */
    private Collection<ChessMove> leftDiagonal(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> leftFinal = new ArrayList<ChessMove>();

        //find if white or black move
        int leftCol = col-1;
        int leftRow = row;
        if (currentColor == ChessGame.TeamColor.WHITE) {
            leftRow++;
        } else {
            leftRow--;
        }

        if (leftCol >= 1 && leftCol <= 8 && leftRow >= 1 && leftRow <= 8) {
            ChessPosition leftPos = new ChessPosition(leftRow, leftCol);
            ChessPiece leftPiece = board.getPiece(leftPos);
            if (leftPiece != null && leftPiece.getTeamColor() != currentColor) {
                if (promoted(leftPos, board.getPiece(position).getTeamColor())) {
                    leftFinal.add(new ChessMove(position, leftPos, ChessPiece.PieceType.QUEEN));
                    leftFinal.add(new ChessMove(position, leftPos, ChessPiece.PieceType.BISHOP));
                    leftFinal.add(new ChessMove(position, leftPos, ChessPiece.PieceType.ROOK));
                    leftFinal.add(new ChessMove(position, leftPos, ChessPiece.PieceType.KNIGHT));
                } else {
                    leftFinal.add(new ChessMove(position, leftPos, null));
                }
            }
        }

        return leftFinal;
    }

    /**
     * Very similar to the left diagonal method, instead it goes right.
     * It checks if there is an enemy to the upper right. If so,
     * it can move to capture it. It will then add this move to a list,
     * which it will return.
     *
     * @param board the current chessBoard
     * @param position the current position of hte piece
     * @return a collection containing the right diagonal move
     */
    private Collection<ChessMove> RightDiagonal(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> rightFinal = new ArrayList<ChessMove>();

        //find if white or black move
        int rightCol = col+1;
        int rightRow = row;
        if (currentColor == ChessGame.TeamColor.WHITE) {
            rightRow++;
        } else {
            rightRow--;
        }

        if (rightCol >= 1 && rightCol <= 8 && rightRow >= 1 && rightRow <= 8) {
            ChessPosition rightPos = new ChessPosition(rightRow, rightCol);
            ChessPiece rightPiece = board.getPiece(rightPos);
            if (rightPiece != null && rightPiece.getTeamColor() != currentColor) {
                if (promoted(rightPos, board.getPiece(position).getTeamColor())) {
                    rightFinal.add(new ChessMove(position, rightPos, ChessPiece.PieceType.QUEEN));
                    rightFinal.add(new ChessMove(position, rightPos, ChessPiece.PieceType.BISHOP));
                    rightFinal.add(new ChessMove(position, rightPos, ChessPiece.PieceType.ROOK));
                    rightFinal.add(new ChessMove(position, rightPos, ChessPiece.PieceType.KNIGHT));

                } else {
                    rightFinal.add(new ChessMove(position, rightPos, null));
                }
            }
        }

        return rightFinal;
    }
}
