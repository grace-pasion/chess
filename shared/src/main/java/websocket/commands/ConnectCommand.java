package websocket.commands;

public class ConnectCommand extends UserGameCommand {
    //more logic needed later

    public ConnectCommand(String authToken, Integer gameID) {
        super(CommandType.CONNECT, authToken, gameID);
    }

}
