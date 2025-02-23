package server;

import server.handler.RegisterHandler;
import service.UserService;
import service.GameService;
import spark.*;

public class Server {
    //circular imports?
    private UserService userService;
    private GameService gameService;

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        RegisterHandler registerHandler = new RegisterHandler();
        // Register your endpoints and handle exceptions here.
        Spark.post("/user", registerHandler);
        Spark.delete("/db", (req, res) -> {
                try {
                    userService.clear();
                    gameService.clear();
                    res.status(200);
                    return "{}";
                } catch (Exception e) {
                    res.status(500);
                    return String.format("{\"message\": \"Error: %s\"}", e.getMessage());
                }
        });

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

}
