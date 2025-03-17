package server.handler;

import com.google.gson.Gson;
import request.CreateGameRequest;
import result.CreateGameResult;
import server.errors.ClassError;
import server.errors.ServerExceptions;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class CreateGameHandler implements Route {
    private final GameService gameService;

    public CreateGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * I turn the request from json to a record class. Then I feed it into my service
     * classes, which returns a result object. This result object will be turned
     * back into JSON.
     * @param req
     * @param res
     * @return
     * @throws ServerExceptions if the authToken is null or the game name is null
     */
    public Object handle(Request req, Response res) throws ServerExceptions {
        try {
            String authToken = req.headers("Authorization");
            if (authToken == null) {
                throw new ServerExceptions(ClassError.AUTHTOKEN_INVALID);
            }
            CreateGameRequest createGameRequest = new Gson().fromJson(req.body(), CreateGameRequest.class);
            if (createGameRequest.gameName() == null) {
                throw new ServerExceptions(ClassError.BAD_REQUEST);
            }
            CreateGameResult createGameResult = gameService.createGame(createGameRequest, authToken);
            res.status(200);
            return new Gson().toJson(createGameResult);
        } catch (ServerExceptions e) {
            res.status(e.getError().getStatusCode());
            return String.format("{\"message\": \"%s\", \"status\": %d}",
                    e.getError().getMessage(), e.getError().getStatusCode());
        }
    }
}
