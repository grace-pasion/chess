package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This is a class that calculates all the possible moves a rook can make
 */
public class RookMovesCalculator implements PieceMovesCalculator {

   /**
    * This is the current row a rook is on
    */
    private static int row;

    /**
     * This is the current column a rook is on
     */
    private static int col;

    /**
     * This function calls the helper functions, which is used
     * to see all the possible moves the rook can make.
     *
     * @param board the current chessBoard
     * @param position the current position of the piece
     * @return a collection of moves the rook can make
     */
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        //collecting the moves
        Collection<ChessMove> finalMoves = new ArrayList<ChessMove>();
        row = position.getRow();
        col = position.getColumn();

        //up possibilities
        finalMoves.addAll(calculateUpMoves(board, position));

        //down possibilites
        finalMoves.addAll(calculateDownMoves(board, position));

        //left possibilites
        finalMoves.addAll(calculateLeftMoves(board, position));

        //right possibilites
        finalMoves.addAll(calculateRightMoves(board, position));


        return finalMoves;
    }

    /**
     * This is a helper function for pieceMoves. It calculates all the possible moves
     * the rook can make to it's left.
     *
     * @param board the current chessBoard
     * @param position the current position of the piece
     * @return a collection of moves the rook can make
     */
    private Collection<ChessMove> calculateLeftMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> leftMovesFinal = new ArrayList<ChessMove>();

        //left
        int leftRow = row;
        boolean occupied = false;
        while (!occupied) {
            leftRow -= 1;

            //inBounds?
            if (leftRow < 1) {
                break;
            }

            ChessPosition newPos = new ChessPosition(leftRow, col);
            ChessPiece occupier = board.getPiece(newPos);

            if (occupier == null) {
                leftMovesFinal.add(new ChessMove(position, newPos, null));
            } else {
                if (occupier.getTeamColor() != board.getPiece(position).getTeamColor()) {
                    leftMovesFinal.add(new ChessMove(position, newPos, null));
                }
                occupied = true;
            }

        }

        return leftMovesFinal;
    }

    /**
     * This is a helper function for pieceMoves. It calculates all the possible moves
     * the rook can make to it's right.
     *
     * @param board the current chessBoard
     * @param position the current position of the piece
     * @return a collection of moves the rook can make
     */
    private Collection<ChessMove> calculateRightMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> rightMovesFinal = new ArrayList<ChessMove>();

        //right
        int rightRow = row;
        boolean occupied = false;
        while (!occupied) {
            rightRow += 1;

            //inBounds?
            if (rightRow > 8) {
                break;
            }

            ChessPosition newPos = new ChessPosition(rightRow, col);
            ChessPiece occupier = board.getPiece(newPos);

            if (occupier == null) {
                rightMovesFinal.add(new ChessMove(position, newPos, null));
            } else {
                if (occupier.getTeamColor() != board.getPiece(position).getTeamColor()) {
                    rightMovesFinal.add(new ChessMove(position, newPos, null));
                }
                occupied = true;
            }

        }

        return rightMovesFinal;
    }

    /**
     * This is a helper function for pieceMoves. It calculates all the possible moves
     * the rook can make up.
     *
     * @param board the current chessBoard
     * @param position the current position of the piece
     * @return a collection of moves the rook can make
     */
    private Collection<ChessMove> calculateUpMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> upMovesFinal = new ArrayList<ChessMove>();
        //initializing the space of the og piece

        //up
        int upCol = col;
        boolean occupied = false;
        while (!occupied) {
            upCol += 1;

            //inBounds?
            if (upCol> 8) {
                break;
            }

            ChessPosition newPos = new ChessPosition(row, upCol);
            ChessPiece occupier = board.getPiece(newPos);

            if (occupier == null) {
                upMovesFinal.add(new ChessMove(position, newPos, null));
            } else {
                if (occupier.getTeamColor() != board.getPiece(position).getTeamColor()) {
                    upMovesFinal.add(new ChessMove(position, newPos, null));
                }
                occupied = true;
            }

        }

        return upMovesFinal;
    }

    /**
     * This is a helper function for pieceMoves. It calculates all the possible moves
     * the rook can make down
     *
     * @param board the current chessBoard
     * @param position the current position of the piece
     * @return a collection of moves the rook can make
     */
    private Collection<ChessMove> calculateDownMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> downMovesFinal = new ArrayList<ChessMove>();

        //up
        int downCol = col;
        boolean occupied = false;
        while (!occupied) {
            downCol -= 1;

            //inBounds?
            if (downCol < 1) {
                break;
            }

            ChessPosition newPos = new ChessPosition(row, downCol);
            ChessPiece occupier = board.getPiece(newPos);

            if (occupier == null) {
                downMovesFinal.add(new ChessMove(position, newPos, null));
            } else {
                if (occupier.getTeamColor() != board.getPiece(position).getTeamColor()) {
                    downMovesFinal.add(new ChessMove(position, newPos, null));
                }
                occupied = true;
            }

        }

        return downMovesFinal;
    }
}
