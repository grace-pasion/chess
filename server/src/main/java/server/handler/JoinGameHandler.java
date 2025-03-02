package server.handler;

import com.google.gson.Gson;
import request.JoinGameRequest;
import result.JoinGameResult;
import server.errors.ClassError;
import server.errors.ServerExceptions;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class JoinGameHandler implements Route {
    private final GameService gameService;

    public JoinGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public Object handle(Request req, Response res) throws ServerExceptions {
        try {
            String authToken = req.headers("authorization");
            if (authToken == null) {
                throw new ServerExceptions(ClassError.AUTHTOKEN_INVALID);
            }

            JoinGameRequest joinGameRequest = new Gson().fromJson(req.body(), JoinGameRequest.class);
            JoinGameResult joinGameResult = gameService.joinGame(joinGameRequest, authToken);
            res.status(200);
            return new Gson().toJson(joinGameResult);
        } catch (ServerExceptions e) {
            res.status(e.getError().getStatusCode());
            return String.format("{\"message\": \"%s\", \"status\": %d}",
                    e.getError().getMessage(), e.getError().getStatusCode());
        }
    }
}

