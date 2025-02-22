package server;


import exception.ResponseException;
import dataaccess.UserDAO;
import dataaccess.MemoryUserDAO;
import service.UserService;
import server.handler.RegisterHandler;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        RegisterHandler registerHandler = new RegisterHandler();

        // Register your endpoints and handle exceptions here.
        Spark.post("/user",registerHandler::registerEndpoint);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }


    private void exceptionHandler(ResponseException ex, Request req, Response res) {
        res.status(ex.StatusCode());
        res.body(ex.toJson());
    }
}
