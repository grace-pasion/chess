package websocket.messages;

public class NotificationMessage extends ServerMessage {
    //just a reminder to myself to implement further
    private final String message;

    public NotificationMessage(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
