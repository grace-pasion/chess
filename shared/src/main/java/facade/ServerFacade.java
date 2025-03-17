package facade;
import com.google.gson.Gson;
import request.*;
import result.*;
import facade.exception.ResponseException;

import java.io.*;
import java.net.*;

public class ServerFacade {
    private final String serverUrl;

    /**
     * This is just a constructor with the specified server URL
     * @param url a string of the server url
     */
    public ServerFacade(String url) {
        serverUrl = url;
    }

    /**
     * Sends a request to clear the database
     *
     * @throws ResponseException if the request fails
     */
    public void clear() throws ResponseException {
        var path = "/db";
        makeRequest("DELETE", path, null, null, null);
    }

    /**
     * This registers a new user
     *
     * @param request the register request containing user details
     * @return a registerResult containing the registration outcomes
     * @throws ResponseException if the request fails
     */
    public RegisterResult register(RegisterRequest request) throws ResponseException {
        var path = "/user";
        return makeRequest("POST", path, request, RegisterResult.class, null);
    }

    /**
     * This logs ins a new user
     *
     * @param request the login request containing user details
     * @return a loginResult containing the login outcomes
     * @throws ResponseException if the login fails
     */
    public LoginResult login(LoginRequest request) throws ResponseException {
        var path = "/session";
        return makeRequest("POST", path, request, LoginResult.class, null);
    }

    /**
     * This logs out a new user
     *
     * @param request the logout request containing user details
     * @return a logoutResult containing logout outcomes
     * @throws ResponseException if the logout fails
     */
    public LogoutResult logout(LogoutRequest request) throws ResponseException {
        var path = "/session";
        //should i put something because the authToken is in the header and not body??
        return makeRequest("DELETE", path, null, LogoutResult.class, request.authToken());
    }

    /**
     * This gets the list of games
     *
     * @param request a listGame request
     * @return a listGameResult containing all the games
     * @throws ResponseException if the listing fails
     */
    public ListGameResult listGame(ListGameRequest request) throws ResponseException {
        var path = "/game";
        //just like logout, in header and not body?
        return makeRequest("GET", path, null, ListGameResult.class, request.authToken());
    }

    /**
     * This creates a game
     *
     * @param request the createGame request
     * @param authToken the user authentication token
     * @return the createGameResult
     * @throws ResponseException if the createGame fails
     */
    public CreateGameResult createGame(CreateGameRequest request, String authToken) throws ResponseException {
        var path = "/game";
        return makeRequest("POST", path, request, CreateGameResult.class, authToken);
    }

    /**
     * This joins a user to the desired game
     *
     * @param request the joinGame request
     * @param authToken the user authentication
     * @return a joinGameResult
     * @throws ResponseException if joining a game fails
     */
    public JoinGameResult joinGame(JoinGameRequest request, String authToken) throws ResponseException {
        var path = "/game";
        return makeRequest("PUT", path, request, JoinGameResult.class, authToken);
    }

    /**
     * This makes an HTTP request to the server, and it also processes that
     * response
     *
     * @param method the HTTP method
     * @param path the endpoint path
     * @param request the request body
     * @param responseClass the expected response class
     * @param authToken the authentication token (optional)
     * @return an instance of the response class
     * @param <T> The type of expected response
     * @throws ResponseException if the request fails
     */
    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authToken != null && !authToken.isEmpty()) {
                http.setRequestProperty("Authorization", authToken);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    /**
     * I believe this just writes the request body as JSON if
     * that is applicable.
     *
     * @param request the request object to be serialized
     * @param http the connection instance
     * @throws IOException if any errors occur while writing
     */
    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    /**
     * This checks if the HTTP response code is good, and if it is not,
     * it throws an exception if the response is unsuccessful
     * @param http the connection instance
     * @throws IOException if an error occurs while reading
     * @throws ResponseException response failure
     */
    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }

    /**
     * This reads and deserializes that response body if that's applicable
     *
     * @param http the connection
     * @param responseClass the class to deserialize
     * @return  an instance of the response class
     * @param <T> the type of expected response
     * @throws IOException if error occurs while reading
     */
    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    /**
     * Determines if the HTTP response code is successful
     * @param status the status of the response
     * @return true if the status is good
     */
    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
