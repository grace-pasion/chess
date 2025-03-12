package service;

import dataaccess.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.CreateGameRequest;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.CreateGameResult;
import result.RegisterResult;
import result.LoginResult;
import result.LogoutResult;
import server.errors.ClassError;
import server.errors.ServerExceptions;


import static org.junit.jupiter.api.Assertions.*;

public class UserTests {
    private UserDAO userDao;
    private AuthDAO authDao;
    private GameDAO gameDao;
    private UserService userService;
    private GameService gameService;

    @BeforeEach
    public void setUp() {
        try {
            userDao = new MySQLUserDAO();
            authDao = new MySQLAuthDAO();
            gameDao = new MySQLGameDAO();
        } catch (ServerExceptions e) {
            throw new RuntimeException("error with tests");
        }
        userService = new UserService(userDao, authDao, gameDao);
        gameService = new GameService(userDao, authDao, gameDao);
        authDao.clear();
        userDao.clear();
        gameDao.clear();
    }

    @Test
    public void registerSuccess() throws ServerExceptions {
        //sending in good register request then making sure it actually
        //registered
        RegisterRequest registerRequest = new RegisterRequest("grace", "password123", "email.com");
        RegisterResult registerResult = userService.register(registerRequest);

        assertNotNull(registerResult);
        assertEquals("grace", registerResult.username());
        assertNotNull(registerResult.authToken());
    }

    @Test
    public void registerFail() throws ServerExceptions {
        //registering two users (with the same username)
        RegisterRequest register1 = new RegisterRequest("grace", "password123", "email.com");
        RegisterRequest register2 = new RegisterRequest("grace", "password246", "grace.com");

        userService.register(register1);

        ServerExceptions e = null;

        //hypothetically won't let the second user register
        //since the username is taken
        try {
            userService.register(register2);
        } catch (ServerExceptions ex) {
            e = ex;
        }
        assert e != null;
        assertEquals(ClassError.ALREADY_TAKEN.getStatusCode(), e.getError().getStatusCode());
        assertEquals(ClassError.ALREADY_TAKEN.getMessage(), e.getError().getMessage());

    }

    @Test
    public void clearSuccess() throws ServerExceptions {
        //Registering the user and creating the game
        //to get game and user data (to later clear)
        RegisterRequest registerRequest = new RegisterRequest
                ("grace", "password123", "email.com");
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("grace", "password123");
        LoginResult loginResult = userService.login(loginRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest("Grace's Game");
        CreateGameResult createGameResult = gameService.createGame(createGameRequest, loginResult.authToken());

        //checking that nothing is current empty
        assertNotNull(createGameResult);
        assertNotNull(userDao.getUserByUsername("grace"));
        assertNotNull(authDao.getAuthData("grace"));
        assertFalse(authDao.getAuthMap().isEmpty());
        assertFalse(userDao.getUserMap().isEmpty());

        //emptying out the data
        userService.clear();
        gameService.clear();

        //checking to make sure everything is empty now
        assertNull(userDao.getUserByUsername("grace"));
        assertNull(authDao.getAuthData("grace"));
        assertTrue(authDao.getAuthMap().isEmpty());
        assertTrue(userDao.getUserMap().isEmpty());
        assertTrue(gameDao.getGames().isEmpty());
    }

    @Test
    public void loginFailure() throws ServerExceptions {
        //test when user DNE:
        LoginRequest loginRequest = new LoginRequest("I don't exist", "password?");
        try {
            userService.login(loginRequest);
            fail("This is supposed to fail (1)");
        } catch (ServerExceptions e) {
            assertEquals(ClassError.USER_NOT_FOUND, e.getError());
        }

        //test when user does exist, but password wrong
        RegisterRequest registerRequest = new RegisterRequest("grace", "password123", "email.com");
        userService.register(registerRequest);
        LoginRequest incorrectPassword = new LoginRequest("grace", "wrong");
        try {
            userService.login(incorrectPassword);
            fail("This is supposed to fail (2)");
        } catch (ServerExceptions e) {
            assertEquals(ClassError.INVALID_PASSWORD, e.getError());
        }
    }

    @Test
    public void loginSuccess() throws ServerExceptions {
        RegisterRequest registerRequest = new RegisterRequest("grace", "password123", "email.com");
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("grace", "password123");
        LoginResult loginResult = userService.login(loginRequest);

        //I am checking if login result is not empty and the
        //correct things are stored
        assertNotNull(loginResult);
        assertNotNull(loginResult.authToken());
        assertEquals("grace", loginResult.username());
        assertNotNull(authDao.getAuthData("grace"));

    }

    @Test
    public void logoutFailure() throws ServerExceptions {
        //Here I am logging out with an authToken that that does
        //not exist, so it will fail
        LogoutRequest logoutRequest = new LogoutRequest("This is not an authToken");
        try {
            userService.logout(logoutRequest);
            fail("This was supposed to fail (3)");
        } catch (ServerExceptions e) {
            assertEquals(ClassError.AUTHTOKEN_INVALID, e.getError());
        }

    }

    @Test
    public void logoutSuccess() throws ServerExceptions {
        RegisterRequest registerRequest = new RegisterRequest("grace", "password123", "email.com");
        RegisterResult registerResult = userService.register(registerRequest);
        LogoutRequest logoutRequest = new LogoutRequest(registerResult.authToken());
        LogoutResult logoutResult = userService.logout(logoutRequest);
        assertNotNull(logoutResult);
        assertNull(authDao.getAuthData("grace"));

        //this try and catch block is a way for me to check if the token
        //is invalid after the logging out (which is a good thing)
        try {
            userService.logout(new LogoutRequest(registerResult.authToken()));
            fail("Expected a ServerExceptions to be thrown");
        } catch (ServerExceptions e) {
            assertEquals(ClassError.AUTHTOKEN_INVALID, e.getError());
        }
    }
}
