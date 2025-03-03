package server.handler;

import com.google.gson.Gson;
import result.ListGameResult;
import request.ListGameRequest;
import server.errors.ClassError;
import server.errors.ServerExceptions;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class ListGameHandler implements Route {
    private final GameService gameService;

    public ListGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * I turn the request from json to a record class. Then I feed it into my service
     *  classes, which returns a result object. This result object will be turned
     *  back into JSON.
     * @param req
     * @param res
     * @return
     * @throws ServerExceptions if the authToken is empty
     */
    public Object handle(Request req, Response res) throws ServerExceptions {
        try {
            String authToken = req.headers("authorization");
            String json = String.format("{\"authToken\": \"%s\"}", authToken);
            if (authToken == null || authToken.isEmpty()) {
                throw new ServerExceptions(ClassError.AUTHTOKEN_INVALID);  // Explicitly throw an exception
            }
            ListGameRequest listGamesRequest = new Gson().fromJson(json, ListGameRequest.class);
            ListGameResult result = gameService.getAllGames(listGamesRequest);
            res.status(200);
            return new Gson().toJson(result);
        } catch (ServerExceptions e) {
            res.status(e.getError().getStatusCode());
            return String.format("{\"message\": \"%s\", \"status\": %d}",
                    e.getError().getMessage(), e.getError().getStatusCode());
        }

    }
}
