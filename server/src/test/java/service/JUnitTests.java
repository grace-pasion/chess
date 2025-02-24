package service;

import dataaccess.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.RegisterResult;
import result.LoginResult;
import result.LogoutResult;
import server.Errors.ClassError;
import server.Errors.ServerExceptions;


import static org.junit.jupiter.api.Assertions.*;

public class JUnitTests {
    //NEED 13 in total
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

    @Test
    public void registerSuccess() throws ServerExceptions {
        RegisterRequest registerRequest = new RegisterRequest("grace", "password123", "email.com");
        RegisterResult registerResult = userService.register(registerRequest);

        assertNotNull(registerResult);
        assertEquals("grace", registerResult.username());
        assertNotNull(registerResult.authToken());
    }

    @Test
    public void registerFail() throws ServerExceptions {
        RegisterRequest register1 = new RegisterRequest("grace", "password123", "email.com");
        RegisterRequest register2 = new RegisterRequest("grace", "password246", "grace.com");

        userService.register(register1);

        ServerExceptions  e = null;
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
        RegisterRequest registerRequest = new RegisterRequest("grace", "password123", "email.com");
        userService.register(registerRequest);

        assertNotNull(userDao.getUserByUsername("grace"));
        assertNotNull(authDao.getAuthData("grace"));
        assertFalse(authDao.getAuthMap().isEmpty());
        assertFalse(userDao.getUserMap().isEmpty());

        userService.clear();
        gameService.clear();

        assertNull(userDao.getUserByUsername("grace"));
        assertNull(authDao.getAuthData("grace"));
        assertTrue(authDao.getAuthMap().isEmpty());
        assertTrue(userDao.getUserMap().isEmpty());
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
        assertNotNull(loginResult);
        assertNotNull(loginResult.authToken());
        assertEquals("grace", loginResult.username());
        assertNotNull(authDao.getAuthData("grace"));

    }

    @Test
    public void logoutFailure() throws ServerExceptions {
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
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("grace", "password123");
        LoginResult loginResult = userService.login(loginRequest);
        assertNotNull(loginResult);
        assertNotNull(loginResult.authToken());
        LogoutRequest logoutRequest = new LogoutRequest(loginResult.authToken());
        LogoutResult logoutResult = userService.logout(logoutRequest);
        assertNotNull(logoutResult);
        assertNull(authDao.getAuthData("grace"));
        //just tryna make sure it is invalid afterwards
        try {
            userService.logout(new LogoutRequest(loginResult.authToken()));  // Try logging out again with the same token
            fail("Expected a ServerExceptions to be thrown");
        } catch (ServerExceptions e) {
            assertEquals(ClassError.AUTHTOKEN_INVALID, e.getError());  // Ensure the token is invalid
        }
    }
}
