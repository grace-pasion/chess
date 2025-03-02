package server.handler;

import com.google.gson.Gson;
import request.LogoutRequest;
import result.LogoutResult;
import server.errors.ServerExceptions;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LogoutHandler implements Route {
    private final UserService userService;

    public LogoutHandler(UserService userService) {
        this.userService = userService;
    }

    /**
     * I turn the request from json to a record class. Then I feed it into my service
     * classes, which returns a result object. This result object will be turned
     * back into JSON.
     *
     * @param req
     * @param res
     * @return
     * @throws ServerExceptions
     */
    public Object handle(Request req, Response res) throws ServerExceptions {
        try {
            String authToken = req.headers("Authorization");
            String json = String.format("{\"authToken\": \"%s\"}", authToken);
            LogoutRequest logoutRequest = new Gson().fromJson(json, LogoutRequest.class);

            LogoutResult result = userService.logout(logoutRequest);
            res.status(200);
            return new Gson().toJson(result);
        } catch (ServerExceptions e) {
            res.status(e.getError().getStatusCode());
            return String.format("{\"message\": \"%s\", \"status\": %d}",
                    e.getError().getMessage(), e.getError().getStatusCode());
        }

    }
}
