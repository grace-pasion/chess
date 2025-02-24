package server.handler;

import com.google.gson.Gson;
import model.GameData;
import result.ListGameResult;
import request.ListGameRequest;
import server.Errors.ClassError;
import server.Errors.ServerExceptions;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.ArrayList;

public class ListGameHandler implements Route {
    private final GameService gameService;

    public ListGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Object handle(Request req, Response res) throws ServerExceptions {
        try {
            String authToken = req.headers("Authorization");
            String json = String.format("{\"authToken\": \"%s\"}", authToken);
            if (authToken == null || authToken.isEmpty()) {
                throw new ServerExceptions(ClassError.AUTHTOKEN_INVALID);  // Explicitly throw an exception
            }
            ListGameRequest listGamesRequest = new Gson().fromJson(json, ListGameRequest.class);
            ListGameResult result = gameService.getAllGames(listGamesRequest);
            res.status(200);
            res.type("application/json");
            return new Gson().toJson(result);
        } catch (ServerExceptions e) {
            res.status(e.getError().getStatusCode());
            return String.format("{\"message\": \"%s\", \"status\": %d}",
                    e.getError().getMessage(), e.getError().getStatusCode());
        }

    }
}
