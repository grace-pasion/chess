package ui;

import java.io.PrintStream;
import java.util.Objects;

import static ui.EscapeSequences.*;

public class ChessBoardRender {

    private String[][] chessBoard = new String[8][8];

    /**
     * This is just the constructor that initializes the board.
     * @param chessBoard a 2d array representing the chess board
     */
    public ChessBoardRender(String[][] chessBoard) {
        this.chessBoard = chessBoard;
    }

    /**
     * This first clears the screen for printing. Then
     * it calls other methods to print out the whole chessboard
     *
     * @param out the printStream
     * @param isWhite true if the player is white
     */
    public void drawChessBoard(PrintStream out, boolean isWhite) {
        out.print(ERASE_SCREEN);
        out.println();
        drawBorder(out, isWhite);
        drawBoard(out, isWhite);
        drawBorder(out, isWhite);
    }

    /**
     * This puts the pieces in the correct spot within the chessboard
     * (where the current player is at the bottom)
     *
     * @param isWhite true if the player is white
     */
    public void initializeBoard(boolean isWhite) {
        if (isWhite) {
            initializeWhite();
        } else {
            initializeBlack();
        }
        for (int i = 2; i < 6; i++) {
            for (int j = 0; j < 8; j++) {
                chessBoard[i][j] = EMPTY;
            }
        }
    }

    /**
     * This loops through each row and draws each row
     * one at a time
     * @param out the PrintStream
     * @param isWhite if the player is white
     */
    private void drawBoard(PrintStream out, boolean isWhite) {
        for (int row = 0; row < 8; row++) {
            drawChessboardRow(out, row, isWhite);
        }
    }

    /**
     * It prints out the left and right boarder.
     * Then it prints out the individual squares in that
     * row (and the characters if there is a
     * piece on a square on that row)
     *
     * @param out the PrintStream
     * @param row the current row we are on
     * @param isWhite true if the player is white
     */
    private void drawChessboardRow(PrintStream out, int row, boolean isWhite) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_WHITE);
        out.print(" ");
        out.print(isWhite ? (8 - row) : (row + 1));
        out.print(" ");

        for (int col = 0; col < 8; col++) {
            boolean isBlackSquare = (row + col) % 2 == 1;
            if (isBlackSquare) {
                out.print(SET_BG_COLOR_RED);
                if (Objects.equals(chessBoard[row][col], EMPTY)) {
                    out.print(SET_TEXT_COLOR_RED+BLACK_PAWN);
                } else {
                    out.print(chessBoard[row][col]);
                }
            } else {
                out.print(SET_BG_COLOR_MAGENTA);
                if (Objects.equals(chessBoard[row][col], EMPTY)) {
                    out.print(SET_TEXT_COLOR_MAGENTA+BLACK_PAWN);
                } else {
                    out.print(chessBoard[row][col]);
                }
            }
        }

        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_WHITE);
        out.print(" ");
        out.print(isWhite ? (8 - row) : (row + 1));
        out.print(" ");
        out.print(RESET_BG_COLOR);

        out.println();
    }

    /**
     * This draws the top and bottom boarder of the chess board
     *  (the a,b,c,etc)
     * @param out the PrintStream
     * @param isWhite true if that player is whtie
     */
    private void drawBorder(PrintStream out, boolean isWhite) {
        char[] labels = {'a','b','c','d','e','f','g','h'};
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_WHITE);
        out.print("  ");

        if (!isWhite) {
            for (int i = labels.length - 1; i >= 0; i--) {
                out.print(" "+labels[i] + "  " );
            }
        } else {
            for (char label : labels) {
                out.print(" "+label + "  ");
            }
        }
        out.print(" ");
        out.print(RESET_BG_COLOR);
        out.println();
    }

    /**
     * Sets the correct initial pieces on the board for a
     * white player
     */
    private void initializeWhite() {
        chessBoard[7][0] = SET_TEXT_COLOR_WHITE + WHITE_ROOK;
        chessBoard[7][1] = SET_TEXT_COLOR_WHITE + WHITE_KNIGHT;
        chessBoard[7][2] = SET_TEXT_COLOR_WHITE + WHITE_BISHOP;
        chessBoard[7][3] = SET_TEXT_COLOR_WHITE + WHITE_QUEEN;
        chessBoard[7][4] = SET_TEXT_COLOR_WHITE + WHITE_KING;
        chessBoard[7][5] = SET_TEXT_COLOR_WHITE + WHITE_BISHOP;
        chessBoard[7][6] = SET_TEXT_COLOR_WHITE + WHITE_KNIGHT;
        chessBoard[7][7] = SET_TEXT_COLOR_WHITE + WHITE_ROOK;

        chessBoard[0][0] = SET_TEXT_COLOR_BLACK + BLACK_ROOK;
        chessBoard[0][1] = SET_TEXT_COLOR_BLACK + BLACK_KNIGHT;
        chessBoard[0][2] = SET_TEXT_COLOR_BLACK + BLACK_BISHOP;
        chessBoard[0][3] = SET_TEXT_COLOR_BLACK + BLACK_QUEEN;
        chessBoard[0][4] = SET_TEXT_COLOR_BLACK + BLACK_KING;
        chessBoard[0][5] = SET_TEXT_COLOR_BLACK + BLACK_BISHOP;
        chessBoard[0][6] = SET_TEXT_COLOR_BLACK + BLACK_KNIGHT;
        chessBoard[0][7] = SET_TEXT_COLOR_BLACK + BLACK_ROOK;

        for (int i = 0; i < 8; i++) {
            chessBoard[6][i] = SET_TEXT_COLOR_WHITE + WHITE_PAWN;
        }

        for (int i = 0; i < 8; i++) {
            chessBoard[1][i] = SET_TEXT_COLOR_BLACK + BLACK_PAWN;
        }
    }

    /**
     * Sets the correct initial pieces on the board for a
     * black player
     */
    private void initializeBlack() {
        chessBoard[7][0] = SET_TEXT_COLOR_BLACK + BLACK_ROOK;
        chessBoard[7][1] = SET_TEXT_COLOR_BLACK + BLACK_KNIGHT;
        chessBoard[7][2] = SET_TEXT_COLOR_BLACK + BLACK_BISHOP;
        chessBoard[7][3] = SET_TEXT_COLOR_BLACK + BLACK_QUEEN;
        chessBoard[7][4] = SET_TEXT_COLOR_BLACK + BLACK_KING;
        chessBoard[7][5] = SET_TEXT_COLOR_BLACK + BLACK_BISHOP;
        chessBoard[7][6] = SET_TEXT_COLOR_BLACK + BLACK_KNIGHT;
        chessBoard[7][7] = SET_TEXT_COLOR_BLACK + BLACK_ROOK;

        chessBoard[0][0] = SET_TEXT_COLOR_WHITE + WHITE_ROOK;
        chessBoard[0][1] = SET_TEXT_COLOR_WHITE + WHITE_KNIGHT;
        chessBoard[0][2] = SET_TEXT_COLOR_WHITE + WHITE_BISHOP;
        chessBoard[0][3] = SET_TEXT_COLOR_WHITE + WHITE_QUEEN;
        chessBoard[0][4] = SET_TEXT_COLOR_WHITE + WHITE_KING;
        chessBoard[0][5] = SET_TEXT_COLOR_WHITE + WHITE_BISHOP;
        chessBoard[0][6] = SET_TEXT_COLOR_WHITE + WHITE_KNIGHT;
        chessBoard[0][7] = SET_TEXT_COLOR_WHITE + WHITE_ROOK;

        for (int i = 0; i < 8; i++) {
            chessBoard[6][i] = SET_TEXT_COLOR_BLACK + BLACK_PAWN;
        }

        for (int i = 0; i < 8; i++) {
            chessBoard[1][i] = SET_TEXT_COLOR_WHITE + WHITE_PAWN;
        }
    }
}
