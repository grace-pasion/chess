package service;

import dataaccess.*;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.*;
import result.CreateGameResult;
import result.JoinGameResult;
import result.ListGameResult;
import result.LoginResult;
import server.Errors.ServerExceptions;

import server.Errors.ClassError;
import static org.junit.jupiter.api.Assertions.*;

public class JUnitTests2 {
    private UserDAO userDao;
    private AuthDAO authDao;
    private GameDAO gameDao;
    private UserService userService;
    private GameService gameService;

    @BeforeEach
    public void setUp() {
        userDao = new MemoryUserDAO();
        authDao = new MemoryAuthDAO();
        gameDao = new MemoryGameDAO();
        userService = new UserService(userDao, authDao, gameDao);
        gameService = new GameService(userDao, authDao, gameDao);

    }

    //create game success
    @Test
    public void createGameSuccess() throws ServerExceptions {
        RegisterRequest registerRequest = new RegisterRequest("grace", "password123", "email.com");
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("grace", "password123");
        LoginResult loginResult = userService.login(loginRequest);
        assertNotNull(loginResult);
        assertNotNull(loginResult.authToken());
        CreateGameRequest createGameRequest = new CreateGameRequest("Grace's Game");
        CreateGameResult createGameResult = gameService.createGame(createGameRequest, loginResult.authToken());
        assertNotNull(createGameResult);
        assertTrue(createGameResult.gameID() > 0);
        GameData gameData = gameDao.getGame(createGameResult.gameID());
        assertNotNull(gameData);
        assertEquals("Grace's Game", gameData.gameName());
    }

    //create game failure
    @Test
    public void createGameFailure() throws ServerExceptions {
        try {
            CreateGameRequest createGameRequest = new CreateGameRequest("A failure of a test :(");
            gameService.createGame(createGameRequest, "invalidAuthToken");
            fail("This should have failed (4)");
        } catch (ServerExceptions e) {
            assertEquals(ClassError.AUTHTOKEN_INVALID, e.getError()); // Ensure invalid token error
        }
    }

    //list games success
    @Test
    public void listGamesSuccess() throws ServerExceptions {
        RegisterRequest registerRequest = new RegisterRequest("grace", "password123", "email.com");
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("grace", "password123");
        LoginResult loginResult = userService.login(loginRequest);
        assertNotNull(loginResult);
        assertNotNull(loginResult.authToken());
        CreateGameRequest createGameRequest = new CreateGameRequest("Grace's Game");
        CreateGameResult createGameResult = gameService.createGame(createGameRequest, loginResult.authToken());
        assertNotNull(createGameResult);
        assertTrue(createGameResult.gameID() > 0);
        ListGameRequest listGamesRequest = new ListGameRequest(loginResult.authToken());
        ListGameResult listGamesResult = gameService.getAllGames(listGamesRequest);
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
        RegisterRequest registerRequest = new RegisterRequest("grace", "password123", "email.com");
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("grace", "password123");
        LoginResult loginResult = userService.login(loginRequest);
        assertNotNull(loginResult);
        assertNotNull(loginResult.authToken());
        CreateGameRequest createGameRequest = new CreateGameRequest("Grace's Game");
        CreateGameResult createGameResult = gameService.createGame(createGameRequest, loginResult.authToken());
        assertNotNull(createGameResult);
        assertTrue(createGameResult.gameID() > 0);
        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", createGameResult.gameID());
        JoinGameResult joinGameResult = gameService.joinGame(joinGameRequest, loginResult.authToken());
        assertNotNull(joinGameResult);
        GameData gameData = gameDao.getGame(createGameResult.gameID());
        assertNotNull(gameData);
        assertEquals("grace", gameData.whiteUsername());
    }
    //join game failure
    @Test
    public void joinGameFailure() {
        try {
            //this fails cause the game ain't found
            RegisterRequest registerRequest = new RegisterRequest("grace", "password123", "email.com");
            userService.register(registerRequest);
            LoginRequest loginRequest = new LoginRequest("grace", "password123");
            LoginResult loginResult = userService.login(loginRequest);
            assertNotNull(loginResult);
            assertNotNull(loginResult.authToken());
            JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", 9999);
            gameService.joinGame(joinGameRequest, loginResult.authToken());
            fail("Expected a ServerExceptions to be thrown");
        } catch (ServerExceptions e) {
            assertEquals(ClassError.BAD_REQUEST, e.getError());
        }
    }

}
