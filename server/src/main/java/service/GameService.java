package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGameRequest;
import result.CreateGameResult;
import result.JoinGameResult;
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

    public CreateGameResult createGame(CreateGameRequest createGameRequest, String authToken) throws ServerExceptions {
        //am i allowed to also pass in the authToken?
        AuthData authData = authDao.getDataFromAuthToken(authToken);
        if (authData == null) {
            throw new ServerExceptions(ClassError.AUTHTOKEN_INVALID);
        }
        ChessGame newGame = new ChessGame();
        GameData gameData = new GameData(gameDao.generateGameID(),
                null,
                null,
                createGameRequest.gameName(),
                newGame);

        gameDao.createGame(gameData);
        return new CreateGameResult(gameData.gameID());

    }

    public JoinGameResult joinGame(JoinGameRequest joinGameRequest, String authToken) throws ServerExceptions {
        AuthData authData = authDao.getDataFromAuthToken(authToken);
        if (authData == null) {
            throw new ServerExceptions(ClassError.AUTHTOKEN_INVALID);
        }
        GameData gameData = gameDao.getGame(joinGameRequest.gameID());
        if (gameData == null || joinGameRequest.playerColor() == null) {
            throw new ServerExceptions(ClassError.BAD_REQUEST);
        }
        if (joinGameRequest.playerColor().equalsIgnoreCase("white")) {
            if (gameData.whiteUsername() != null) {
                throw new ServerExceptions(ClassError.ALREADY_TAKEN);
            }
            gameData = new GameData(gameData.gameID(),
                    authData.username(), gameData.blackUsername(), gameData.gameName(), gameData.game());
        } else if (joinGameRequest.playerColor().equalsIgnoreCase("black")) {
            if (gameData.blackUsername() != null) {
                throw new ServerExceptions(ClassError.ALREADY_TAKEN);
            }
            gameData = new GameData(gameData.gameID(), gameData.whiteUsername(),
                    authData.username(), gameData.gameName(), gameData.game());
        } else {
            throw new ServerExceptions(ClassError.BAD_REQUEST);
        }
        gameDao.updateGame(gameData.gameID(), gameData);
        return new JoinGameResult();

    }
    public void clear() {
        userDao.clear();
        authDao.clear();
        gameDao.clear();

    }
}
