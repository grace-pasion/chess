package facade.handler;

import com.google.gson.Gson;
import facade.errors.ClassError;
import facade.errors.ServerExceptions;
import spark.*;
import service.UserService;
import request.RegisterRequest;
import result.RegisterResult;

public class RegisterHandler implements Route {

    private final UserService userService;

    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }

    /**
     * I turn the request from json to a record class. Then I feed it into my service
     * classes, which returns a result object. This result object will be turned
     * back into JSON.
     *
     * @param req
     * @param res
     * @return a JSON object
     * @throws ServerExceptions if one of the request fields is null
     */
    public Object handle(Request req, Response res) throws ServerExceptions {
        try {
            RegisterRequest registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
            if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
                throw new ServerExceptions(ClassError.BAD_REQUEST);

            }
            RegisterResult result = userService.register(registerRequest);
            res.status(200);
            return new Gson().toJson(result);
        } catch (ServerExceptions e) {
            res.status(e.getError().getStatusCode());
            return String.format("{\"message\": \"%s\", \"status\": %d}",
                    e.getError().getMessage(), e.getError().getStatusCode());
        }

    }
}
