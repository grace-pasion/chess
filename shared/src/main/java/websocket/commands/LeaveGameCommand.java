package websocket.commands;

public class LeaveGameCommand extends UserGameCommand {
    //just a reminder to add more logic later

    public LeaveGameCommand(String authToken, Integer gameID) {
        super(CommandType.LEAVE, authToken, gameID);
    }

}
