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
import server.errors.ClassError;
import server.errors.ServerExceptions;

import java.util.ArrayList;

public class GameService {
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
     * This is my constructor where I have a user, authToken, and game
     * data access objects, so the current memory I am using will be the same
     * throughout
     *
     * @param userDao
     * @param authDao
     * @param gameDao
     */
    public GameService(UserDAO userDao, AuthDAO authDao, GameDAO gameDao) {
        this.userDao = userDao;
        this.authDao = authDao;
        this.gameDao = gameDao;
    }

    /**
     * I grab all the games in the current database.
     * Then I add that to my listGameResult object.
     *
     * @param listGameRequest which includes an authToken
     * @return a ListGameResult object
     * @throws ServerExceptions if the authentication token is invalid
     */
    public ListGameResult getAllGames(ListGameRequest listGameRequest) throws ServerExceptions {
        AuthData authData = authDao.getDataFromAuthToken(listGameRequest.authToken());
        if (authData == null) {
            throw new ServerExceptions(ClassError.AUTHTOKEN_INVALID);
        }
        ArrayList<GameData> allGames = gameDao.getGames();
        return new ListGameResult(allGames);
    }

    /**
     * It creates a new chess game by setting up the board properly. It then
     * generates all the other game data associated with the beginning of a game.
     * Then I add this game data to the game database
     *
     * @param createGameRequest which includes the game name
     * @param authToken the authentication token of the current user
     * @return a createGameResult object
     * @throws ServerExceptions if the authToken in invalid
     */
    public CreateGameResult createGame(CreateGameRequest createGameRequest, String authToken) throws ServerExceptions {
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

    /**
     *
     * @param joinGameRequest which includes the color of the player joining and the game
     *                        they want to join (in the form of a game ID)
     * @param authToken the authentication token of the user
     * @return a joinGameResult
     * @throws ServerExceptions if the authToken is invalid,
     *                          the color they want is already taken,or if there color
     *                          is not white or black
     */
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

    /**
     * This function goes through
     * and clears the user, authentication, and game
     * databases.
     */
    public void clear() {
        userDao.clear();
        authDao.clear();
        gameDao.clear();

    }
}
