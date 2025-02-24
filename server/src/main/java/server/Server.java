package server;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import server.handler.LoginHandler;
import server.handler.LogoutHandler;
import server.handler.RegisterHandler;
import service.UserService;
import service.GameService;
import spark.*;

public class Server {
    //circular imports?
    private UserService userService;
    private GameService gameService;

    public int run(int desiredPort) {
        //do i need to set up service before hand?
        this.userService = new UserService(new MemoryUserDAO(),new MemoryAuthDAO(), new MemoryGameDAO());
        this.gameService = new GameService(new MemoryUserDAO(), new MemoryAuthDAO(), new MemoryGameDAO());

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        RegisterHandler registerHandler = new RegisterHandler(userService);
        LoginHandler loginHandler = new LoginHandler(userService);
        LogoutHandler logoutHandler = new LogoutHandler(userService);
        // Register your endpoints and handle exceptions here.
        Spark.post("/user", registerHandler);
        Spark.post("/session", loginHandler);
        Spark.post("/session", logoutHandler);
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
