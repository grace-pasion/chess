import chess.*;

import server.Server;
import facade.ServerFacade;
import facade.exception.ResponseException;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        //cannot call server, start on server main then run this main
        Server server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        ServerFacade serverFacade = new ServerFacade("http://localhost:" + port);
        try {
            serverFacade.clear();
        } catch (ResponseException e) {
            System.out.println("Error in clearing");
        }
        // Pass the correct URL to Repl
        Repl repl = new Repl("http://localhost:" + port);
        repl.run();
        server.stop();
    }
}