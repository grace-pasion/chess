package service;
//my data access interfaces
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
//my record classes for request/result
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;
//my record classes for data and stuff
import model.UserData;
import model.AuthData;
//my exception classes
import server.Errors.ClassError;
import server.Errors.ServerExceptions;

import java.util.UUID;

public class UserService {
    private final UserDAO userDao;
    private final AuthDAO authDao;
    private final GameDAO gameDao;

    public UserService(UserDAO userDao, AuthDAO authDao, GameDAO gameDao) {
        this.userDao = userDao;
        this.authDao = authDao;
        this.gameDao = gameDao;
    }

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
        authDao.createAuth(authData);

        return new RegisterResult(request.username(), authToken);
    }

    public LoginResult login(LoginRequest request) throws ServerExceptions {
        UserData user = userDao.getUserByUsername(request.username());
        if (user == null) {
            throw new ServerExceptions(ClassError.USER_NOT_FOUND);
        }
        if (!user.password().equals(request.password())) {
            throw new ServerExceptions(ClassError.INVALID_PASSWORD);
        }
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, request.username());
        authDao.createAuth(authData);
        return new LoginResult(request.username(), authToken);
    }

    public void clear() {
        userDao.clear();
        authDao.clear();
        gameDao.clear();
    }

}
