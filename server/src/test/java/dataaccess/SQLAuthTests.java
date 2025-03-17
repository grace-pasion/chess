package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import facade.errors.ServerExceptions;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class SQLAuthTests {
    private static MySQLAuthDAO authDao;

    @BeforeEach
    public void setUp()  {
        try {
            authDao = new MySQLAuthDAO();
            authDao.clear();
        } catch (ServerExceptions e) {
            throw new RuntimeException("error with tests");
        }
    }

    //This is createAuth Tests
    @Test
    public void createAuthPositive() {
        String authToken = "fakeToken";
        AuthData authData = new AuthData(authToken, "graceUser");
        authDao.createAuth(authToken, authData);

        AuthData returnedData = authDao.getAuthData("graceUser");
        assertNotNull(returnedData);
        assertEquals(authToken, returnedData.authToken());
        assertEquals("graceUser", returnedData.username());
    }

    @Test
    public void createAuthNegative() {
        AuthData authDataNoToken = new AuthData(null, "graceUser");
        try {
            authDao.createAuth(null, authDataNoToken);
            fail("Expeceted exception");
        } catch (RuntimeException e) {
            assertEquals("Error occurred when trying to create authentication", e.getMessage());
        }
    }

    //This is clear testing
    @Test
    public void onlyClearTest() {
        authDao.createAuth("goodToken", new AuthData("goodToken", "serena"));
        authDao.createAuth("token1", new AuthData("token1", "noah"));
        authDao.createAuth("token2", new AuthData("token2", "naomi"));
        assertFalse(authDao.getAuthMap().isEmpty());
        authDao.clear();
        assertTrue(authDao.getAuthMap().isEmpty());

    }

    //This is getAuth Tests
    @Test
    public void getAuthMapPositive() {
        authDao.createAuth("token1", new AuthData("token1", "serena"));
        authDao.createAuth("token2", new AuthData("token2", "noah"));
        authDao.createAuth("token3", new AuthData("token3", "naomi"));
        HashMap<String, AuthData> authMap = authDao.getAuthMap();

        assertEquals(3, authMap.size());

        assertTrue(authMap.containsKey("token1"));
        assertTrue(authMap.containsKey("token2"));
        assertTrue(authMap.containsKey("token3"));

        assertEquals("serena", authMap.get("token1").username());
        assertEquals("noah", authMap.get("token2").username());
        assertEquals("naomi", authMap.get("token3").username());
    }

    @Test
    public void getAuthMapNegative() {
        HashMap<String, AuthData> authMap = authDao.getAuthMap();
        assertTrue(authMap.isEmpty());
    }

    //This is getAuthData Tests
    @Test
    public void getAuthDataPositive() {
        authDao.createAuth("validToken",
                new AuthData("validToken", "grace"));
        AuthData returnedData = authDao.getAuthData("grace");
        assertEquals("grace", returnedData.username());
        assertEquals("validToken", returnedData.authToken());
    }

    @Test
    public void getAuthDataNegative() {
        AuthData returnedData = authDao.getAuthData("I don't exist");
        assertNull(returnedData);
    }

    //This is deleteAuth Tests
    @Test
    public void deleteAuthPositive() {
        authDao.createAuth("validToken",
                new AuthData("validToken", "grace"));
        assertNotNull(authDao.getDataFromAuthToken("validToken"));
        authDao.deleteAuth("validToken");
        assertNull(authDao.getDataFromAuthToken("validToken"));
    }

    @Test
    public void deleteAuthNegative() {
        //I didn't know how to create another type of negative test,
        //so I am just deleting with an invalid token,
        //and making sure it the thing with the valid token
        //is still there afterwards
        authDao.createAuth("validToken",
                new AuthData("validToken", "grace"));
        assertNotNull(authDao.getDataFromAuthToken("validToken"));
        authDao.deleteAuth("Invalid token");
        assertNotNull(authDao.getDataFromAuthToken("validToken"));
    }

    //This is getDataFromAuthToken Tests
    @Test
    public void getDataFromAuthTokenPositive() {
        authDao.createAuth("validToken",
                new AuthData("validToken", "grace"));
        AuthData returnedData = authDao.getDataFromAuthToken("validToken");
        assertNotNull(returnedData);
        assertEquals("validToken", returnedData.authToken());
        assertEquals("grace", returnedData.username());
    }

    @Test
    public void getDataFromAuthTokenNegative() {
        AuthData returnedData = authDao.getDataFromAuthToken("invalid token");
        assertNull(returnedData);
    }
}
