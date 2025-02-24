package server.handler;

import com.google.gson.Gson;
import request.CreateGameRequest;
import request.LoginRequest;
import result.CreateGameResult;
import server.Errors.ClassError;
import server.Errors.ServerExceptions;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class CreateGameHandler implements Route {
    private final GameService gameService;

    public CreateGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
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
            res.type("application/json");
            res.status(200);
            return new Gson().toJson(createGameResult);
        } catch (ServerExceptions e) {
            res.status(e.getError().getStatusCode());
            return String.format("{\"message\": \"%s\", \"status\": %d}",
                    e.getError().getMessage(), e.getError().getStatusCode());
        }
    }
}
