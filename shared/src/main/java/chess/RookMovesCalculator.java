package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator implements PieceMovesCalculator {
    private static int row;
    private static int col;

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

    //helperFunction for left movements
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

    //helper function for right movement
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

    //helper function for up movement
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

    //helper function down
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
