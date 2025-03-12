package service;

import dataaccess.MySQLUserDAO;
import dataaccess.UserDAO;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.errors.ServerExceptions;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class SQLUserTests {

    private static UserDAO userDao;

    @BeforeEach
    public void setUp() {
        try {
            userDao = new MySQLUserDAO();
            userDao.clear();
        } catch (ServerExceptions e) {
            throw new RuntimeException("error with tests");
        }
    }

    //This is getUserByUsername testing
    @Test
    public void getUserByUsernamePositive() {
        UserData userData = new UserData("grace",
                "password123", "email.com");
        userDao.createUser(userData);

        UserData returnedData = userDao.getUserByUsername("grace");
        assertNotNull(returnedData);
        assertEquals("grace", returnedData.username());
        assertEquals("email.com", returnedData.email());
    }

    @Test
    public void getUserByUsernameNegative() {
        UserData returnedData = userDao.getUserByUsername("I don't exist");
        assertNull(returnedData);
    }

    //This is createUser testing
    @Test
    public void createUserPositive() {
        UserData userData = new UserData("grace", "password", "email");
        userDao.createUser(userData);

        UserData returnedData = userDao.getUserByUsername("grace");
        assertNotNull(returnedData);
        assertEquals("grace", returnedData.username());
    }

    @Test
    public void createUserNegative() {
        UserData userData = new UserData(null, "password", "email");
        try {
            userDao.createUser(userData);
            fail("Expected to fail");
        } catch (RuntimeException e) {
            assertEquals("Error occurred when trying to create authentication", e.getMessage());
        }
    }

    //This is testing clear
    @Test
    public void onlyClear() {
        userDao.createUser(new UserData("serena", "pass", "email.com"));
        userDao.createUser(new UserData("noah", "pass", "noah.com"));
        userDao.createUser(new UserData("naomi", "pass", "naomi.com"));
        assertFalse(userDao.getUserMap().isEmpty());
        userDao.clear();
        assertTrue(userDao.getUserMap().isEmpty());
    }

    //This is verifyUser tests
    @Test
    public void verifyUserPositive() {
        UserData userData = new UserData("grace", "pass", "email.com");
        userDao.createUser(userData);
        boolean isVerified = userDao.verifyUser("grace", "pass");
        assertTrue(isVerified);
    }

    @Test
    public void verifyUserNegative() {
        UserData userData = new UserData("grace", "pass", "email.com");
        userDao.createUser(userData);
        boolean isVerified = userDao.verifyUser("grace", "BAD PASSWORD!");
        assertFalse(isVerified);
    }

    //This is getUserMap testing
    @Test
    public void getUserMapPositive() {
        UserData user1 = new UserData("grace", "password1", "email1");
        UserData user2 = new UserData("serena", "password2", "email2");
        UserData user3 = new UserData("naomi", "password3", "email3");
        userDao.createUser(user1);
        userDao.createUser(user2);
        userDao.createUser(user3);

        HashMap<String, UserData> userMap = userDao.getUserMap();

        assertEquals(3, userMap.size());

        assertTrue(userMap.containsKey("grace"));
        assertTrue(userMap.containsKey("serena"));
        assertTrue(userMap.containsKey("naomi"));
    }

    @Test
    public void getUserMapNegative() {
        HashMap<String, UserData> userData = userDao.getUserMap();
        assertTrue(userData.isEmpty());
    }
}
