package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator {
    private int row;
    private int col;
    private ChessGame.TeamColor currentColor;

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

    private boolean promoted(ChessPosition position, ChessGame.TeamColor color ) {
        int currentRow = position.getRow();
        if ((color ==ChessGame.TeamColor.WHITE && currentRow == 8)) {
            return true;
        }
        return (color == ChessGame.TeamColor.BLACK && currentRow == 1);
    }


    //helper function for reg move
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

    //helper function for double move
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

    //helper function for diagonal enemy
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
