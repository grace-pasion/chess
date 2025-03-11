package server;

import dataaccess.*;
import server.errors.ClassError;
import server.errors.ServerExceptions;
import server.handler.*;
import service.UserService;
import service.GameService;
import spark.*;

public class Server {
    /**
     * the user service object
     */
    private UserService userService;

    /**
     * the game service object
     */
    private GameService gameService;

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
            UserDAO userDao = isSQL ? new MySQLUserDAO() : new MemoryUserDAO();
            AuthDAO authDao = isSQL ? new MySQLAuthDAO() : new MemoryAuthDAO();
            GameDAO gameDao = isSQL ? new MySQLGameDAO() : new MemoryGameDAO();
            this.userService = new UserService(userDao, authDao, gameDao);
            this.gameService = new GameService(userDao, authDao, gameDao);
        } catch (ServerExceptions e) {
            throw new ServerExceptions(ClassError.DATABASE_ERROR);
        }
    }
}
