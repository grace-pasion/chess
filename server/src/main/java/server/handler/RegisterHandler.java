package server.handler;

import com.google.gson.Gson;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import spark.*;
import service.UserService;
import request.RegisterRequest;
import result.RegisterResult;

public class RegisterHandler implements Route {

    public Object handle(Request req, Response res) throws ServerExceptions {
        try {
            RegisterRequest registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
            if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
                throw new ServerExceptions(ClassError.BAD_REQUEST);

            }
            UserService userService = new UserService(new MemoryUserDAO(), new MemoryAuthDAO(), new MemoryGameDAO());
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
