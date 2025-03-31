package websocket.commands;

public class ConnectCommand extends UserGameCommand {
    //more logic needed later
    public enum Side {
        WHITE,
        BLACK,
        OBSERVER
    }
    private final Side side;

    public ConnectCommand(String authToken, Integer gameID, Side side) {
        super(CommandType.CONNECT, authToken, gameID);
        this.side = side;
    }

    public Side getSide() {
        return side;
    }

}
