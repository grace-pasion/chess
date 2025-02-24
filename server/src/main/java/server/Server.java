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
        MemoryUserDAO userDao = new MemoryUserDAO();
        MemoryAuthDAO authDao = new MemoryAuthDAO();
        MemoryGameDAO gameDao = new MemoryGameDAO();
        this.userService = new UserService(userDao, authDao,gameDao);
        this.gameService = new GameService(userDao, authDao, gameDao);

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        RegisterHandler registerHandler = new RegisterHandler(userService);
        LoginHandler loginHandler = new LoginHandler(userService);
        LogoutHandler logoutHandler = new LogoutHandler(userService);
        ListGameHandler listGameHandler = new ListGameHandler(gameService);

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", registerHandler);
        Spark.post("/session", loginHandler);
        Spark.delete("/session", logoutHandler);
        Spark.get("/game", listGameHandler);
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
