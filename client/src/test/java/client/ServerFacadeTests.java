package client;

import org.junit.jupiter.api.*;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.LogoutResult;
import result.RegisterResult;
import server.Server;
import server.ServerFacade;
import server.exception.ResponseException;
import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static String authToken;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:"+port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void setup() throws ResponseException {
        facade.clear();

        RegisterRequest request = new RegisterRequest
                ("Serena", "password", "serena@email.com");
        RegisterResult result = facade.register(request);
        assertNotNull(result);
        authToken = result.authToken();
    }

    //register tests
    @Test
    public void registerSuccess() throws ResponseException {
        RegisterRequest request = new RegisterRequest
                ("grace", "password", "grace@email.com");
        RegisterResult result = facade.register(request);
        assertNotNull(result);
        assertEquals("grace", result.username());
        assertNotNull(result.authToken());
    }


    @Test
    public void registerFailure() throws ResponseException {
        //here i am registering the same user twice, which should not be allowed
        RegisterRequest request = new RegisterRequest
                ("Serena", "password", "serena@email.com");
        try {
            facade.register(request);
            fail("This is supposed to fail, since a user can register twice");
        } catch (Exception e) {
            assertEquals("Error: already taken", e.getMessage());
        }
    }

    //login tests
    @Test
    public void loginSuccess() throws ResponseException {
        LoginRequest request = new LoginRequest("Serena", "password");
        LoginResult result = facade.login(request);
        assertEquals("Serena", result.username());
    }

    @Test
    public void loginFailure() throws ResponseException {
        LoginRequest request = new LoginRequest("Serena", "WRONGGG!!!");
        try {
            facade.login(request);
            fail("This is supposed to fail since Serena entered the wrong password!");
        } catch (Exception e) {
            assertEquals("Error: unauthorized", e.getMessage());
        }
    }

    //logout tests
    @Test
    public void logoutSuccess() throws ResponseException {
        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        LogoutResult result = facade.logout(logoutRequest);
        assertNotNull(result);
    }

    @Test
    public void logoutFailure() throws ResponseException {
        LogoutRequest logoutRequest = new LogoutRequest("Not valid");
        try {
            facade.logout(logoutRequest);
            fail("Excepted to fail, since the authToken is invalid");
        } catch (ResponseException e) {
            assertEquals(e.getMessage(), "Error: unauthorized");
        }
    }

}
