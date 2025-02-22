package server.handler;

import com.google.gson.Gson;
import exception.ResponseException;
import spark.*;
import service.UserService;
import server.request.RegisterRequest;
import server.result.RegisterResult;

public class RegisterHandler {

    public Object registerEndpoint(Request req, Response res) {
        try {
            RegisterRequest registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
            RegisterResult result = UserService.register(registerRequest);

            res.type("application/json");
            return new Gson().toJson(result);
        } catch (ResponseException e) {
            res.status(e.StatusCode());
            return e.toJson();
        }

    }
}
