package server.handler;

import com.google.gson.Gson;
import request.LogoutRequest;
import result.LogoutResult;
import server.Errors.ServerExceptions;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class ListGameHandler implements Route {
    private final GameService gameService;

    public ListGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Object handle(Request req, Response res) throws ServerExceptions {
        try {
            String authToken = req.headers("Authorization");
            String json = String.format("{\"authToken\": \"%s\"}", authToken);
            LogoutRequest logoutRequest = new Gson().fromJson(json, LogoutRequest.class);

            LogoutResult result = userService.logout(logoutRequest);
            //could have handler implement route
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
