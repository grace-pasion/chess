package websocket.commands;

public class ResignCommand extends UserGameCommand {
    //reminder to add more logic later

    public ResignCommand(String authToken, Integer gameID) {
        super(CommandType.RESIGN, authToken, gameID);
    }
}
