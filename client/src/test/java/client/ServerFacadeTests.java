package client;

import org.junit.jupiter.api.*;
import request.*;
import result.*;
import facade.Server;
import facade.ServerFacade;
import facade.exception.ResponseException;
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
            assertEquals("Error: unauthorized", e.getMessage());
        }
    }

    //clear (only one clear since really no way to fail)
    @Test
    public void clearSuccess() throws ResponseException {
        try {
            facade.clear();
            assertTrue(true);
        } catch (ResponseException e) {
            fail("Excepted no errors, since this is a successful test");
        }
    }

    //create games tests
    @Test
    public void createGamesSuccess() throws ResponseException {
        CreateGameRequest createGameRequest = new CreateGameRequest("game name");
        CreateGameResult result = facade.createGame(createGameRequest, authToken);
        assertNotNull(result);
        assertInstanceOf(Integer.class, result.gameID());
    }

    @Test
    public void createGamesFailure() throws ResponseException {
        CreateGameRequest createGameRequest = new CreateGameRequest("game name");
        try {
            facade.createGame(createGameRequest, "not valid authToken");
            fail("This is supposed to fail since the authToken is not valid");
        } catch (ResponseException e) {
            assertEquals("Error: unauthorized", e.getMessage());
        }
    }

    //list games tests
    @Test
    public void listGamesSuccess() throws ResponseException {
        CreateGameRequest createGameRequest = new CreateGameRequest("list game");
        facade.createGame(createGameRequest, authToken);

        ListGameRequest listGameRequest = new ListGameRequest(authToken);
        ListGameResult result = facade.listGame(listGameRequest);
        assertNotNull(result);
        assertEquals(1, result.games().size());
    }

    @Test
    public void listGamesFailure() throws ResponseException {
        try {
            ListGameRequest listGameRequest = new ListGameRequest("not valid");
            facade.listGame(listGameRequest);
            fail("This is supposed to fail because the authToken is invalid");
        } catch (ResponseException e) {
            assertEquals("Error: unauthorized", e.getMessage());
        }
    }

    //join games tests
    @Test
    public void joinGameSuccess() throws ResponseException {
        CreateGameRequest createGameRequest = new CreateGameRequest("join game");
        CreateGameResult createGameResult = facade.createGame(createGameRequest, authToken);
        int gameID = createGameResult.gameID();

        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", gameID);
        JoinGameResult joinGameResult = facade.joinGame(joinGameRequest, authToken);

        assertNotNull(joinGameResult);
    }

    @Test
    public void joinGameFailure() throws ResponseException {
        CreateGameRequest createGameRequest = new CreateGameRequest("join failure game");
        CreateGameResult createGameResult = facade.createGame(createGameRequest, authToken);
        int gameFailureID = createGameResult.gameID();

        JoinGameRequest joinGameRequest = new JoinGameRequest("BLACK", gameFailureID);
        try {
            facade.joinGame(joinGameRequest, "not valid");
            fail("This is supposed to fail because the authToken is invalid");
        } catch (ResponseException e) {
            assertEquals("Error: unauthorized", e.getMessage());
        }
    }

}
