package websocketfacade;

import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public interface NotificationHandler {
    void notify(ServerMessage message);
    void errorMessage(ErrorMessage errorMessage);
    void loadGame(LoadGameMessage loadGameMessage);
    void notification(NotificationMessage notificationMessage);
}
