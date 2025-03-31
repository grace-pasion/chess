package server.websocket;

import com.google.gson.Gson;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
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

import static websocket.commands.ConnectCommand.Side.BLACK;
import static websocket.commands.ConnectCommand.Side.WHITE;

@WebSocket
public class WebSocketHandler {
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private final ConnectionManager connections = new ConnectionManager();

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);

            //validate authTokens by checking it and throwing errors if it is not got
            String username = getUsername(command.getAuthToken());

            saveSession(command.getGameID(), session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, (ConnectCommand) command);
                case MAKE_MOVE -> makeMove(session, username, (MakeMoveCommand) command);
                case LEAVE -> leaveGame(session, username, (LeaveGameCommand) command);
                case RESIGN -> resign(session, username, (ResignCommand) command);

            }
        } catch (Exception ex) {
            //serialize and send the error message
            ex.printStackTrace();
            sendMessage(session.getRemote(), new ErrorMessage("Error: "+ex.getMessage()));
        }
    }

    private void connect(Session session, String username, ConnectCommand command) throws IOException {
        GameData gameData = gameDAO.getGame(command.getGameID());

        if (gameData == null) {
            sendMessage(session.getRemote(), new ErrorMessage("Game is not found"));
            return;
        }

        if ((gameData.whiteUsername() != null && command.getSide() == WHITE) ||
                (gameData.blackUsername() != null && command.getSide() == BLACK)) {
            sendMessage(session.getRemote(), new ErrorMessage("Already taken"));
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
        connections.add(username, session);
        sendMessage(session.getRemote(), new LoadGameMessage(gameData.game()));
        String message = username + " has connected as " + side;
        var notification = new NotificationMessage(message);
        connections.broadcast(username, notification);

        gameDAO.updateGame(gameData.gameID(), gameData);
    }

    private void makeMove(Session session, String username, MakeMoveCommand command) {

    }

    private void leaveGame(Session session, String username, LeaveGameCommand command) {

    }

    private void resign(Session session, String username, ResignCommand command) {

    }

    private void sendMessage(RemoteEndpoint remote, ServerMessage message) {
        try {
            String json = new Gson().toJson(message);
            remote.sendString(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
