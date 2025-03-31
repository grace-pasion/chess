package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);

            //validate authTokens by checking it and throwing errors if it is not got
            //String username = getUsername(command.getAuthToken());

            //saveSession(command.getGameID(), session);

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

    private void connect(Session session, String username, ConnectCommand command) {

    }

    private void makeMove(Session session, String username, MakeMoveCommand command) {

    }

    private void leaveGame(Session session, String username, LeaveGameCommand command) {

    }

    private void resign(Session session, String username, ResignCommand command) {

    }
}
