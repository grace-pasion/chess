package service;

import dataaccess.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    //list games success

    //list games failure


}
