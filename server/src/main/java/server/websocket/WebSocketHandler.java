package server.websocket;

import chess.*;
import com.google.gson.Gson;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import dataaccess.AuthDAO;

import java.io.IOException;
import java.rmi.ServerError;
import java.rmi.ServerException;
import java.util.Collection;
import java.util.Objects;

import static websocket.commands.ConnectCommand.Side.BLACK;
import static websocket.commands.ConnectCommand.Side.WHITE;

@WebSocket
public class WebSocketHandler {
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private final ConnectionManager connections = new ConnectionManager();
    public WebSocketHandler() {
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);

            //validate authTokens by checking it and throwing errors if it is not got
            String username = getUsername(command.getAuthToken());

            saveSession(command.getGameID(), session);

            switch (command.getCommandType()) {
                case CONNECT -> {
                    ConnectCommand connectCommand = new Gson().fromJson(message, ConnectCommand.class);
                    connect(session, username, connectCommand);
                }
                case MAKE_MOVE -> {
                    MakeMoveCommand moveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
                    makeMove(session, username, moveCommand);
                }
                case LEAVE -> {
                    LeaveGameCommand leaveCommand = new Gson().fromJson(message, LeaveGameCommand.class);
                    leaveGame(session, username, leaveCommand);
                }
                case RESIGN -> {
                    ResignCommand resignCommand = new Gson().fromJson(message, ResignCommand.class);
                    resign(session, username, resignCommand);
                }
            }
        } catch (Exception ex) {
            //serialize and send the error message
            ex.printStackTrace();
            //sendMessage(session.getRemote(), new ErrorMessage("Error: "+ex.getMessage()));
        }
    }

    private void connect(Session session, String username, ConnectCommand command) throws IOException {
        GameData gameData = gameDAO.getGame(command.getGameID());
        connections.add(username, session);

        if (gameData == null) {
            String ErrorMessageJson = new Gson().toJson(new ErrorMessage("Game is not found"));
            connections.connections.get(username).send(ErrorMessageJson);
            return;
        }

        if (!Objects.equals(authDAO.getAuthData(username).authToken(), command.getAuthToken())) {
            String ErrorMessageJson = new Gson().toJson(new ErrorMessage("Invalid AuthToken"));
            connections.connections.get(username).send(ErrorMessageJson);
            return;
        }
        //find authToken correctly

        if ((gameData.whiteUsername() != null && command.getSide() == WHITE) ||
                (gameData.blackUsername() != null && command.getSide() == BLACK)) {
            String ErrorMessageJson = new Gson().toJson(new ErrorMessage("Already Taken"));
            connections.connections.get(username).send(ErrorMessageJson);
            return;
        }

        String side = "";
        if (command.getSide() == ConnectCommand.Side.WHITE) {
            gameData = new GameData(gameData.gameID(),
                    username, gameData.blackUsername(),gameData.gameName(), gameData.game());
            side = "white";
        } else if (command.getSide() == ConnectCommand.Side.BLACK) {
            gameData = new GameData(gameData.gameID(),
                    gameData.whiteUsername(), username,gameData.gameName(), gameData.game());
            side = "black";
        } else {
            side = "observer";
        }

        String loadGameMessageJson = new Gson().toJson(new LoadGameMessage(gameData.game()));
        connections.connections.get(username).send(loadGameMessageJson);
        String message = username + " has connected as " + side;
        var notification = new NotificationMessage(message);
        connections.broadcast(username, notification);

        gameDAO.updateGame(gameData.gameID(), gameData);
    }


    private void makeMove(Session session, String username, MakeMoveCommand command) throws IOException {
        //steps need to do according to gameplay.md
        //1. server verifies the validity of the move
        GameData gameData = gameDAO.getGame(command.getGameID());
        if ((gameData.whiteUsername().equals(username)
                && gameData.game().getTeamTurn() != ChessGame.TeamColor.WHITE) ||
                (gameData.blackUsername().equals(username)
                && gameData.game().getTeamTurn() != ChessGame.TeamColor.BLACK)) {
            String ErrorMessageJson = new Gson().toJson(new ErrorMessage("It's not your turn"));
            connections.connections.get(username).send(ErrorMessageJson);
        }
        boolean isValidMove = validateMove(gameData, command);
        if (!isValidMove) {
            String ErrorMessageJson = new Gson().toJson(new ErrorMessage("Invalid Move!"));
            connections.connections.get(username).send(ErrorMessageJson);
        }

        //2. game is updated to represent the move. game is updated in the database
        ChessMove move = command.getMove();
        try {
            gameData.game().makeMove(move);
        } catch (InvalidMoveException e) {
            String ErrorMessageJson = new Gson().toJson(new ErrorMessage("Made move failed"));
            connections.connections.get(username).send(ErrorMessageJson);
            return;
        }
        gameDAO.updateGame(gameData.gameID(), gameData);

        //3. send a load game message to all the clients (including the root client)
        // with an updated game
        //sendMessage(session.getRemote(), new LoadGameMessage(gameData.game()));
        String loadGameMessageJson = new Gson().toJson(new LoadGameMessage(gameData.game()));
        connections.connections.get(username).send(loadGameMessageJson);
        LoadGameMessage loadGameMessage = new LoadGameMessage(gameData.game());
        connections.broadcast(username, loadGameMessage);

        //4. sends a notification message to all other clients in the game
        // informing them what move was made
        String moveDescription = username + " made a move: " +
                move.getStartPosition() + " to " + move.getEndPosition();
        var notification = new NotificationMessage(moveDescription);
        connections.broadcast(username, notification);

        //5. if the move results in check, checkmate, or stalement the server
        //sends a notification message to all clients
        resultsInBadNews(username, gameData, session);

        //do i need to update the game turn?
        ChessGame.TeamColor nextTurn = (gameData.game().getTeamTurn() == ChessGame.TeamColor.WHITE)
                ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
        gameData.game().setTeamTurn(nextTurn);
        gameDAO.updateGame(gameData.gameID(), gameData);
    }

    private void leaveGame(Session session, String username, LeaveGameCommand command) throws IOException {
        //1. If a player is leaving, update game to remove root client (game is updated
        //in database)
        GameData  gameData = gameDAO.getGame(command.getGameID());
        if (gameData == null) {
            //sendMessage(session.getRemote(), new ErrorMessage("The Game is not found"));
            return;
        }

        //checking because it might be an observer
        boolean isPlayer= false;
        if (username.equals(gameData.whiteUsername())) {
            gameData = new GameData(gameData.gameID(), null,
                    gameData.blackUsername(), gameData.gameName(), gameData.game());
            isPlayer = true;
        } else if (username.equals(gameData.blackUsername())) {
            gameData = new GameData(gameData.gameID(), gameData.whiteUsername(),
                    null, gameData.gameName(), gameData.game());
            isPlayer = true;
        }

        if (isPlayer) {
            gameDAO.updateGame(gameData.gameID(), gameData);
        }

        connections.remove(username);
        //2. Server sends a notification message to all other clients informing them
        //that the root client left. This applies to both players and observers
        String message = username + " has left the game.";
        var notification = new NotificationMessage(message);
        connections.broadcast(username, notification);

    }

    private void resign(Session session, String username, ResignCommand command) throws IOException {
        //1. server marks the game as over (no more moves can be made). Game is updated in the database
        GameData gameData = gameDAO.getGame(command.getGameID());
        if (gameData == null) {
            //sendMessage(session.getRemote(), new ErrorMessage("Game not found"));
            return;
        }

        //just so observers can't resign
        if (!username.equals(gameData.whiteUsername())
                && !username.equals(gameData.blackUsername())) {
            //sendMessage(session.getRemote(), new ErrorMessage("Only players can resign"));
            return;
        }

        gameData.game().setGameOver(true);
        gameDAO.updateGame(gameData.gameID(), gameData);

        //2. Server sends a notification message to all clients in that game informing
        //them that the root client resigned. This applies to both player and observers
        String message = username + " has resigned. The game is over.";
        var notification = new NotificationMessage(message);
        connections.broadcast(username, notification);
        //sendMessage(session.getRemote(), notification);

    }


    private void saveSession(Integer gameID, Session session) {
        ConnectionManager.saveSession(gameID, session);
    }

    private String getUsername(String authToken) throws Exception {
        AuthData authData = authDAO.getDataFromAuthToken(authToken);
        if (authData == null) {
            throw new Exception("Invalid auth token");
        }
        return authData.username();
    }

    private boolean validateMove(GameData gameData, MakeMoveCommand command) {
        ChessGame chessGame = gameData.game();

        ChessPosition startPosition = command.getMove().getStartPosition();
        ChessPosition endPosition = command.getMove().getEndPosition();

        //checking if the piece exists and right color
        ChessPiece piece = chessGame.getBoard().getPiece(startPosition);
        if ((chessGame.getBoard().getPiece(startPosition) == null) ||
                (piece.getTeamColor() != chessGame.getTeamTurn())) {
            return false;
        }

        Collection<ChessMove> validMoves = chessGame.validMoves(startPosition);
        boolean isValidMove = false;
        for (ChessMove move : validMoves) {
            if (move.getEndPosition().equals(endPosition)) {
                isValidMove = true;
                break;
            }
        }
        return isValidMove;
        //maybe need to deal with stalement, checkmate, and check after?


    }

    private void resultsInBadNews(String username, GameData gameData, Session session) throws IOException {
        if (gameData.game().isInCheck(gameData.game().getTeamTurn())) {
            var checkNotification = new NotificationMessage("Check: " + gameData.game().getTeamTurn() + " is in check.");
            connections.broadcast(username, checkNotification);
            //sendMessage(session.getRemote(), checkNotification);
        } else if (gameData.game().isInCheckmate(gameData.game().getTeamTurn())) {
            var checkmateNotification = new NotificationMessage("Checkmate: " + gameData.game().getTeamTurn() + " is in checkmate.");
            connections.broadcast(username, checkmateNotification);
            //sendMessage(session.getRemote(), checkmateNotification);
        } else if (gameData.game().isInStalemate(gameData.game().getTeamTurn())) {
            var stalemateNotification = new NotificationMessage("Stalemate: The game is in stalemate.");
            connections.broadcast(username, stalemateNotification);
            //sendMessage(session.getRemote(), stalemateNotification);
        }
    }

    public void setAuthDAO(AuthDAO authDao) {
        this.authDAO = authDao;
    }

    public void setGameDAO(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

}
