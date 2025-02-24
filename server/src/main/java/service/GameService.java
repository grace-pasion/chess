package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.GameData;
import request.ListGameRequest;
import result.ListGameResult;

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

    public ListGameResult getAllGames(ListGameRequest listGameRequest) {
        listGameRequest.authToken();
        //something here about verifying the authToken with getAuth(authToken) etc
        ArrayList<GameData> allGames = gameDao.getGames();
        return new ListGameResult(allGames);
    }

    public void clear() {
        userDao.clear();
        authDao.clear();
        gameDao.clear();

    }
}
