package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import request.CreateGameRequest;
import request.ListGameRequest;
import result.CreateGameResult;
import result.ListGameResult;
import server.Errors.ClassError;
import server.Errors.ServerExceptions;

import java.util.ArrayList;

public class GameService {
    private final UserDAO userDao;
    private final AuthDAO authDao;
    private final GameDAO gameDao;

    public GameService(UserDAO userDao, AuthDAO authDao, GameDAO gameDao) {
        this.userDao = userDao;
        this.authDao = authDao;
        this.gameDao = gameDao;
    }

    public ListGameResult getAllGames(ListGameRequest listGameRequest) throws ServerExceptions {
        AuthData authData = authDao.getDataFromAuthToken(listGameRequest.authToken());
        if (authData == null) {
            throw new ServerExceptions(ClassError.AUTHTOKEN_INVALID);
        }
        ArrayList<GameData> allGames = gameDao.getGames();
        return new ListGameResult(allGames);
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) {

    }
    public void clear() {
        userDao.clear();
        authDao.clear();
        gameDao.clear();

    }
}
