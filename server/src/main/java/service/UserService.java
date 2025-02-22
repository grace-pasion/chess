package service;

import dataaccess.UserDAO;
import request.RegisterRequest;
import chess.result.RegisterResult;
import model.UserData;
import model.AuthData;
import server.handler.ClassError;
import server.handler.ServerExceptions;

import java.util.UUID;

public class UserService {
    private final UserDAO userDao;

    public UserService(UserDAO userDao) {
        this.userDao = userDao;
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
        userDao.createAuth(authData);

        return new RegisterResult(request.username(), authToken);
    }


}
