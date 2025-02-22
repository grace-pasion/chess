package service;

import dataaccess.UserDAO;
import exception.ResponseException;
import server.request.RegisterRequest;
import server.result.RegisterResult;
import model.UserData;
import model.AuthData;
import java.util.UUID;

public class UserService {
    private final UserDAO userDao;

    public UserService(UserDAO userDao) {
        this.userDao = userDao;
    }

    public RegisterResult register(RegisterRequest request) throws ResponseException {
        if (userDao.getUserByUsername(request.username()) != null) {
            throw new ResponseException(403, "Error: already taken");
        }

        UserData user = new UserData(request.username(), request.password(), request.email());
        userDao.createUser(user);

        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, request.username());
        userDao.createAuth(authData);

        return new RegisterResult(request.username(), authToken);
    }


}
