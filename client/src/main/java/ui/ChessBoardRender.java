package ui;

import java.io.PrintStream;

import static ui.EscapeSequences.*;

public class ChessBoardRender {
    private static final int BOARD_SIZE = 8;
    private static final int BORDER_SIZE = 10;;
    /*
    Note for later: will need a main that does var out =
    new PrintStream(System.out,..)
     */
    public void drawChessBoard(PrintStream out, boolean isWhite) {
        out.print(ERASE_SCREEN);
        drawBorder(out, isWhite);
        drawBoard(out, isWhite);
        drawBorder(out, isWhite);
    }

    private void drawBoard(PrintStream out, boolean isWhite) {
        for (int row = 0; row < 8; row++) {
            drawChessboardRow(out, row, isWhite);
        }
    }

    private void drawChessboardRow(PrintStream out, int row, boolean isWhite) {
        out.print(SET_BG_COLOR_DARK_GREEN);
        out.print(SET_TEXT_COLOR_WHITE);
        out.print(" ");
        out.print(8 - row);
        out.print(" ");

        for (int col = 0; col < 8; col++) {
            boolean isBlackSquare = (row + col) % 2 == 1;
            if (isBlackSquare) {
                out.print(SET_BG_COLOR_DARK_GREY);
                out.print(SET_TEXT_COLOR_WHITE);
                out.print(EMPTY);
            } else {
                out.print(SET_BG_COLOR_WHITE);
                out.print(SET_TEXT_COLOR_BLACK);
                out.print(EMPTY);
            }
        }

        out.print(SET_BG_COLOR_DARK_GREEN);
        out.print(SET_TEXT_COLOR_WHITE);
        out.print(" ");
        out.print(8 - row);
        out.print(" ");
        out.print(RESET_BG_COLOR);

        out.println();
    }

    private void drawBorder(PrintStream out, boolean isWhite) {
        char[] labels = {'a','b','c','d','e','f','g','h'};
        out.print(SET_BG_COLOR_DARK_GREEN);
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
