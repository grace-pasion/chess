package ui;

import java.io.PrintStream;

import static ui.EscapeSequences.*;

public class ChessBoardRender {
    private static final int BOARD_SIZE = 8;
    private static final int BORDER_SIZE = 10;
    private static String[][] chessBoard = new String[8][8];
    /*
    Note for later: will need a main that does var out =
    new PrintStream(System.out,..)
     */
    public void drawChessBoard(PrintStream out, boolean isWhite) {
        initializeBoard();
        out.print(ERASE_SCREEN);
        drawBorder(out, isWhite);
        drawBoard(out, isWhite);
        drawBorder(out, isWhite);
    }

    private void initializeBoard() {
        chessBoard[0][0] = SET_TEXT_COLOR_WHITE+WHITE_ROOK;
        chessBoard[0][1] = SET_TEXT_COLOR_WHITE+WHITE_KNIGHT;
        chessBoard[0][2] = SET_TEXT_COLOR_WHITE+WHITE_BISHOP;
        chessBoard[0][3] = SET_TEXT_COLOR_WHITE+WHITE_QUEEN;
        chessBoard[0][4] = SET_TEXT_COLOR_WHITE+WHITE_KING;
        chessBoard[0][5] = SET_TEXT_COLOR_WHITE+WHITE_BISHOP;
        chessBoard[0][6] = SET_TEXT_COLOR_WHITE+WHITE_KNIGHT;
        chessBoard[0][7] = SET_TEXT_COLOR_WHITE+WHITE_ROOK;
        chessBoard[7][0] = SET_TEXT_COLOR_BLACK+BLACK_ROOK;
        chessBoard[7][1] = SET_TEXT_COLOR_BLACK+BLACK_KNIGHT;
        chessBoard[7][2] = SET_TEXT_COLOR_BLACK+BLACK_BISHOP;
        chessBoard[7][3] = SET_TEXT_COLOR_BLACK+BLACK_QUEEN;
        chessBoard[7][4] = SET_TEXT_COLOR_BLACK+BLACK_KING;
        chessBoard[7][5] = SET_TEXT_COLOR_BLACK+BLACK_BISHOP;
        chessBoard[7][6] = SET_TEXT_COLOR_BLACK+BLACK_KNIGHT;
        chessBoard[7][7] = SET_TEXT_COLOR_BLACK+BLACK_ROOK;

        for (int i = 0; i < 8; i++) {
            chessBoard[1][i] = SET_TEXT_COLOR_WHITE+WHITE_PAWN;
        }

        for (int i = 0; i < 8; i++) {
            chessBoard[6][i] = SET_TEXT_COLOR_BLACK+BLACK_PAWN;
        }

        for (int i = 2; i < 6; i++) {
            for (int j = 0; j < 8; j++) {
                chessBoard[i][j] = EMPTY;
            }
        }
    }
    private void drawBoard(PrintStream out, boolean isWhite) {
        for (int row = 0; row < 8; row++) {
            drawChessboardRow(out, row, isWhite);
        }
    }

    private void drawChessboardRow(PrintStream out, int row, boolean isWhite) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_WHITE);
        out.print(" ");
        out.print(8 - row);
        out.print(" ");

        for (int col = 0; col < 8; col++) {
            boolean isBlackSquare = (row + col) % 2 == 1;
            if (isBlackSquare) {
                out.print(SET_BG_COLOR_RED);
                //out.print(EMPTY);
                out.print(chessBoard[row][col]);
            } else {
                out.print(SET_BG_COLOR_MAGENTA);
                //out.print(EMPTY);
                out.print(chessBoard[row][col]);
            }
        }

        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_WHITE);
        out.print(" ");
        out.print(8 - row);
        out.print(" ");
        out.print(RESET_BG_COLOR);

        out.println();
    }

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

    public static void main(String[] args) {
        ChessBoardRender render = new ChessBoardRender();
        var out = new PrintStream(System.out, true);
        render.drawChessBoard(out,false);
    }
}
