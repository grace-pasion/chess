package websocketFacade;

import com.google.gson.Gson;
import facade.exception.ResponseException;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint { //extend endPoint
    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);

                    switch (serverMessage.getServerMessageType()) {
                        case ERROR:
                            notificationHandler.errorMessage(new Gson().fromJson(message, ErrorMessage.class));
                            break;
                        case LOAD_GAME:
                            notificationHandler.loadGame(new Gson().fromJson(message, LoadGameMessage.class));
                            break;
                        case NOTIFICATION:
                            notificationHandler.notification(new Gson().fromJson(message, NotificationMessage.class));
                            break;
                        default:
                            notificationHandler.notify(serverMessage);
                            break;
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

}
