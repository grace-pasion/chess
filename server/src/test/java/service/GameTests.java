package service;

import dataaccess.*;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.*;
import result.*;
import server.Errors.ServerExceptions;

import server.Errors.ClassError;
import static org.junit.jupiter.api.Assertions.*;

public class GameTests {
    private GameDAO gameDao;
    private GameService gameService;
    private RegisterResult registeredUser;
    private String authToken;

    @BeforeEach
    public void setUp() throws ServerExceptions {
        UserDAO userDao = new MemoryUserDAO();
        AuthDAO authDao = new MemoryAuthDAO();
        gameDao = new MemoryGameDAO();
        UserService userService = new UserService(userDao, authDao, gameDao);
        gameService = new GameService(userDao, authDao, gameDao);

        RegisterRequest registerRequest = new RegisterRequest("grace", "password123", "email.com");
        RegisterResult registeredUser  = userService.register(registerRequest);
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
        CreateGameRequest createGameRequest = new CreateGameRequest("Grace's Game");
        CreateGameResult createGameResult = gameService.createGame(createGameRequest, authToken);
        assertNotNull(createGameResult);
        assertTrue(createGameResult.gameID() > 0);
        ListGameRequest listGamesRequest = new ListGameRequest(authToken);
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
        CreateGameRequest createGameRequest = new CreateGameRequest("Grace's Game");
        CreateGameResult createGameResult = gameService.createGame(createGameRequest, authToken);

        assertNotNull(createGameResult);
        assertTrue(createGameResult.gameID() > 0);

        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", createGameResult.gameID());
        JoinGameResult joinGameResult = gameService.joinGame(joinGameRequest, authToken);

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
            JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", 9999);
            gameService.joinGame(joinGameRequest, authToken);
            fail("Expected a ServerExceptions to be thrown");
        } catch (ServerExceptions e) {
            assertEquals(ClassError.BAD_REQUEST, e.getError());
        }
    }

}
