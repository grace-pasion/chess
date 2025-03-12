package service;

import dataaccess.*;
import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.*;
import result.*;
import server.errors.ServerExceptions;

import server.errors.ClassError;
import static org.junit.jupiter.api.Assertions.*;

public class GameTests {
    private GameDAO gameDao;
    private AuthDAO authDao;
    private UserDAO userDao;
    private GameService gameService;
    private RegisterResult registeredUser;
    private String authToken;

    @AfterEach
    public void clear() {
        authDao.clear();
        userDao.clear();
        gameDao.clear();
    }

    @BeforeEach
    public void setUp() throws ServerExceptions {
        //this is just setting up the information
        //since the auto-grader got made at me for having duplicate
        //code
        //UserDAO userDao = new MemoryUserDAO();
        //AuthDAO authDao = new MemoryAuthDAO();
        //gameDao = new MemoryGameDAO();
        try {
            userDao = new MySQLUserDAO();
            authDao = new MySQLAuthDAO();
            gameDao = new MySQLGameDAO();
        } catch (ServerExceptions e) {
            throw new RuntimeException("error with tests");
        }
        gameService = new GameService(userDao, authDao, gameDao);
        UserService userService = new UserService(userDao, authDao, gameDao);
        RegisterRequest registerRequest = new RegisterRequest("grace", "password123", "email.com");
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("grace", "password123");
        LoginResult loginResult = userService.login(loginRequest);
        authToken = loginResult.authToken();

    }

    //create game success
    @Test
    public void createGameSuccess() throws ServerExceptions {
        CreateGameRequest createGameRequest = new CreateGameRequest("Grace's Game");
        CreateGameResult createGameResult = gameService.createGame(createGameRequest, authToken);
        assertNotNull(createGameResult);
        //making sure the game id is not negative
        assertTrue(createGameResult.gameID() > 0);
        GameData gameData = gameDao.getGame(createGameResult.gameID());
        //making sure there is actually game data with the name of my game
        assertNotNull(gameData);
        assertEquals("Grace's Game", gameData.gameName());
        authDao.clear();
        userDao.clear();
        gameDao.clear();
    }

    //create game failure
    @Test
    public void createGameFailure() throws ServerExceptions {
        try {
            //passing in an invalid authToken, so it should fail hypothetically
            CreateGameRequest createGameRequest = new CreateGameRequest("A failure of a test :(");
            gameService.createGame(createGameRequest, "invalidAuthToken");
            fail("This should have failed (4)");

        } catch (ServerExceptions e) {
            assertEquals(ClassError.AUTHTOKEN_INVALID, e.getError());
        }
    }

    //list games success
    @Test
    public void listGamesSuccess() throws ServerExceptions {
        //creating the game to make (to later list)
        CreateGameRequest createGameRequest = new CreateGameRequest("Grace's Game");
        CreateGameResult createGameResult = gameService.createGame(createGameRequest, authToken);

        ListGameRequest listGamesRequest = new ListGameRequest(authToken);
        ListGameResult listGamesResult = gameService.getAllGames(listGamesRequest);
        //making sure the list game result is not empty
        assertNotNull(listGamesResult);
        assertFalse(listGamesResult.games().isEmpty());
    }
    //list games failure
    @Test
    public void listGamesFailure() {
        try {
            // Attempt to list games with an invalid auth token
            ListGameRequest listGamesRequest = new ListGameRequest("invalidAuthToken");
            gameService.getAllGames(listGamesRequest);
            fail("This was supposed to fail (5)");
        } catch (ServerExceptions e) {
            assertEquals(ClassError.AUTHTOKEN_INVALID, e.getError()); // Ensure invalid token error
        }
    }
    //join game succes
    @Test
    public void joinGameSuccess() throws ServerExceptions {
        CreateGameRequest createGameRequest = new CreateGameRequest("Grace's Game");
        CreateGameResult createGameResult = gameService.createGame(createGameRequest, authToken);

        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", createGameResult.gameID());
        JoinGameResult joinGameResult = gameService.joinGame(joinGameRequest, authToken);

        //making sure the join game result is not empty
        assertNotNull(joinGameResult);
        GameData gameData = gameDao.getGame(createGameResult.gameID());

        assertNotNull(gameData);
        //making sure grace is properly added to the game
        assertEquals("grace", gameData.whiteUsername());
    }

    //join game failure
    @Test
    public void joinGameFailure() {
        try {
            //this fails because the game is not found
            JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", 9999);
            gameService.joinGame(joinGameRequest, authToken);
            fail("Expected a ServerExceptions to be thrown");
        } catch (ServerExceptions e) {
            assertEquals(ClassError.BAD_REQUEST, e.getError());
        }
    }

}
