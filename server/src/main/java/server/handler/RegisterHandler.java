package server.handler;

import com.google.gson.Gson;
import spark.*;
import service.UserService;
import request.RegisterRequest;
import result.RegisterResult;

public class RegisterHandler implements Route {

    private final UserService userService;

    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }
    public Object handle(Request req, Response res) throws ServerExceptions {
        try {
            RegisterRequest registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
            if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
                throw new ServerExceptions(ClassError.BAD_REQUEST);

            }
            RegisterResult result = userService.register(registerRequest);
            //could have handler implement route
            res.type("application/json");
            return new Gson().toJson(result);
        } catch (ServerExceptions e) {
            res.status(e.getError().getStatusCode());
            return String.format("{\"message\": \"%s\", \"status\": %d}",
                    e.getError().getMessage(), e.getError().getStatusCode());
        }

    }
}
