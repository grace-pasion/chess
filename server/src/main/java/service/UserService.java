package service;
//my data access interfaces
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
//my record classes for request/result
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.LogoutResult;
import result.RegisterResult;
//my record classes for data and stuff
import model.UserData;
import model.AuthData;
//my exception classes
import server.errors.ClassError;
import server.errors.ServerExceptions;

import java.util.UUID;

public class UserService {
    /**
     * The user data access
     */
    private final UserDAO userDao;

    /**
     * The authToken data access
     */
    private final AuthDAO authDao;

    /**
     * The game data, data access
     */
    private final GameDAO gameDao;

    /**
     * This is just the constructor that takes in the
     * data access objects, so everytime will reference the same databases
     *
     * @param userDao
     * @param authDao
     * @param gameDao
     */
    public UserService(UserDAO userDao, AuthDAO authDao, GameDAO gameDao) {
        this.userDao = userDao;
        this.authDao = authDao;
        this.gameDao = gameDao;
    }

    /**
     * The register function takes in a request and creates user data based on the
     * request. It then creates the user and authToken to send to database. It
     * then stores these a register result, which it returns.
     *
     * @param request which includes the username who wants to register
     *                and their password & email
     * @return a RegisterResult which stores the username and the new authToken
     * @throws ServerExceptions  if the username is taken or a field in the request is empty
     *
     */
    public RegisterResult register(RegisterRequest request) throws ServerExceptions {
        if (userDao.getUserByUsername(request.username()) != null) {
            throw new ServerExceptions(ClassError.ALREADY_TAKEN);
        }

        if (request.username() == null || request.email() == null || request.password() == null) {
            throw new ServerExceptions(ClassError.BAD_REQUEST);
        }
        UserData user = new UserData(request.username(), request.password(), request.email());

        userDao.createUser(user);

        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, request.username());
        authDao.createAuth(authToken, authData);

        return new RegisterResult(request.username(), authToken);
    }

    /**
     *This grabs the user from the database and checks to make
     * sure their password is valid. If so, it generates a new authToken for
     * the user, and adds it to the database.
     *
     * @param request the login request, which includes the user's username and password
     * @return a login result which includes the username and new authToken
     * @throws ServerExceptions if the username DNE, or if the password is invalid
     */
    public LoginResult login(LoginRequest request) throws ServerExceptions {
        UserData user = userDao.getUserByUsername(request.username());
        if (user == null) {
            throw new ServerExceptions(ClassError.USER_NOT_FOUND);
        }
        if (!userDao.verifyUser(request.username(), request.password())) {
            throw new ServerExceptions(ClassError.INVALID_PASSWORD);
        }
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, request.username());
        authDao.createAuth(authToken, authData);
        return new LoginResult(request.username(), authToken);
    }

    /**
     * This function grabs the authData from the authToken,
     * and it deletes that authToken, which logs out that user
     *
     * @param request the logout request which includes the authToken
     * @return the logout result object
     * @throws ServerExceptions if the authentication data is null
     */
    public LogoutResult logout(LogoutRequest request) throws ServerExceptions {
        AuthData authData = authDao.getDataFromAuthToken(request.authToken());
        if (authData == null) {
            throw new ServerExceptions(ClassError.AUTHTOKEN_INVALID);
        }
        authDao.deleteAuth(request.authToken());
        return new LogoutResult();
    }

    /**
     * This function clears all the
     * data from the user, authentication, and game
     * databases.
     */
    public void clear() {
        userDao.clear();
        authDao.clear();
        gameDao.clear();
    }

}
