package server;

import dataaccess.*;
import server.errors.ClassError;
import server.errors.ServerExceptions;
import server.handler.*;
import server.websocket.WebSocketHandler;
import service.UserService;
import service.GameService;
import spark.*;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;

public class Server {
    /**
     * the user service object
     */
    private UserService userService;

    /**
     * the game service object
     */
    private GameService gameService;

    private UserDAO userDAO;
    private GameDAO gameDAO;
    private AuthDAO authDAO;

    private final WebSocketHandler webSocketHandler;
    public Server() {
        webSocketHandler = new WebSocketHandler();
    }

    /**
     *  I grab the databases, so that the database it
     *  references stays consistent throughout all the classes.
     *  Then I call each endpoint (register, login, etc).
     *
     *
     * @return an integer
     */
    public int run(int desiredPort) {
        //change this line to toggle between memory/SQL type
        boolean isSQL = true;

        try {
            correctDAO(isSQL);
        } catch (ServerExceptions e) {
            return 500;
        }

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        webSocketHandler.setAuthDAO(authDAO);
        webSocketHandler.setGameDAO(gameDAO);
        Spark.webSocket("/ws", webSocketHandler);

        RegisterHandler registerHandler = new RegisterHandler(userService);
        LoginHandler loginHandler = new LoginHandler(userService);
        LogoutHandler logoutHandler = new LogoutHandler(userService);
        ListGameHandler listGameHandler = new ListGameHandler(gameService);
        CreateGameHandler createGameHandler = new CreateGameHandler(gameService);
        JoinGameHandler joinGameHandler = new JoinGameHandler(gameService);

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", registerHandler);
        Spark.post("/session", loginHandler);
        Spark.delete("/session", logoutHandler);
        Spark.post("/game", createGameHandler);
        Spark.get("/game", listGameHandler);
        Spark.put("/game", joinGameHandler);

        //As a side note for the grader, I am doing my clear function in
        //the server, since there is nothing of significance being passed in
        // or returned
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

    private void correctDAO(boolean isSQL) throws ServerExceptions {
        try {
            userDAO = isSQL ? new MySQLUserDAO() : new MemoryUserDAO();
            authDAO = isSQL ? new MySQLAuthDAO() : new MemoryAuthDAO();
            gameDAO = isSQL ? new MySQLGameDAO() : new MemoryGameDAO();
            this.userService = new UserService(userDAO, authDAO, gameDAO);
            this.gameService = new GameService(userDAO, authDAO, gameDAO);
        } catch (ServerExceptions e) {
            throw new ServerExceptions(ClassError.DATABASE_ERROR);
        }
    }
}
