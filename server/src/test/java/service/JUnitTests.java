package service;

import dataaccess.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.RegisterRequest;
import result.RegisterResult;
import server.handler.ClassError;
import server.handler.ServerExceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JUnitTests {
    //NEED 13 in total
    private UserDAO userDao;
    private AuthDAO authDao;
    private GameDAO gameDao;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        userDao = new MemoryUserDAO();
        authDao = new MemoryAuthDAO();
        gameDao = new MemoryGameDAO();
        userService = new UserService(userDao, authDao, gameDao);
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

}
