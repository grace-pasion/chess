package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import facade.errors.ServerExceptions;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class SQLGameTests {
    private static MySQLGameDAO gameDao;

    @BeforeEach
    public void setUp() {
        try {
            gameDao = new MySQLGameDAO();
            gameDao.clear();
        } catch (ServerExceptions e) {
            throw new RuntimeException("error with tests");
        }
    }

    //This is clear testing
    @Test
    public void onlyClearTest() {
        //int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game)
        gameDao.createGame(new GameData(123, "white1",
                "black1", "game1", new ChessGame()));
        gameDao.createGame(new GameData(124, "white2",
                "black2", "game2", new ChessGame()));
        gameDao.createGame(new GameData(125, "white3",
                "black3", "game3", new ChessGame()));
        assertFalse(gameDao.getGames().isEmpty());
        gameDao.clear();
        assertTrue(gameDao.getGames().isEmpty());

    }

    //This is testing getGames
    @Test
    public void getGamesPositive() {
        gameDao.createGame(new GameData(123, "white1",
                "black1", "game1", new ChessGame()));
        gameDao.createGame(new GameData(125, "white3",
                "black3", "game3", new ChessGame()));

        ArrayList<GameData> listOfGames = gameDao.getGames();
        assertEquals(2, listOfGames.size());

        boolean game1Found = false;
        boolean game3Found = false;

        for (GameData game : listOfGames) {
            if (game.gameID() == 123 && game.whiteUsername().equals("white1") &&
                    game.blackUsername().equals("black1") && game.gameName().equals("game1")) {
                game1Found = true;
            }
            if (game.gameID() == 125 && game.whiteUsername().equals("white3") &&
                    game.blackUsername().equals("black3") && game.gameName().equals("game3")) {
                game3Found = true;
            }
        }

        assertTrue(game1Found);
        assertTrue(game3Found);
    }

    @Test
    public void getGamesNegative() {
        ArrayList<GameData> listGames = gameDao.getGames();
        assertTrue(listGames.isEmpty());
    }

    //This is testing createGames
    @Test
    public void createGamesPositive() {
        gameDao.createGame(new GameData(123, "white1",
                "black1", "game1", new ChessGame()));

        ArrayList<GameData> listOfGames = gameDao.getGames();
        boolean gameCreated = false;
        for (GameData game : listOfGames) {
            if (game.gameID() == 123 && game.whiteUsername().equals("white1") &&
                    game.blackUsername().equals("black1") && game.gameName().equals("game1")) {
                gameCreated = true;
                break;
            }
        }
        assertTrue(gameCreated);
    }

    @Test
    public void createGamesNegative() {
        try {
            gameDao.createGame(new GameData(123, "white1",
                    "black1", null, new ChessGame()));
            fail("Excepted to fail");
        } catch (RuntimeException e) {
            assertEquals("Error occurred when trying to create the game", e.getMessage());
        }
    }

    //This is testing generateGameID
    @Test
    public void generateGameIDPositive() {
        gameDao.createGame(new GameData(1, "white1",
                "black1", "gameName", new ChessGame()));
        int newGameID = gameDao.generateGameID();
        assertEquals(2, newGameID);
    }

    @Test
    public void generateGameIDNegative() {
        //there is not really a way this fails since no matter what it
        // grabs a number unless database fails, which would not be
        // the issue of this function, so here I am just trying to
        // make sure it hits default 1
        int generateGameID = gameDao.generateGameID();
        assertEquals(1, generateGameID);
    }

    //Testing getGame
    @Test
    public void getGamePositive() {
        gameDao.createGame(new GameData(123, "white1",
                "black1", "game1", new ChessGame()));

        GameData game = gameDao.getGame(1);
        assertNotNull(game);
        assertEquals(123, game.gameID());
        assertEquals("white1", game.whiteUsername());
        assertEquals("black1", game.blackUsername());
        assertEquals("game1", game.gameName());
    }

    @Test
    public void getGameNegative() {
        GameData game = gameDao.getGame(10);
        assertNull(game);
    }

    //Testing updateGame
    @Test
    public void updateGamePositive() {
        gameDao.createGame(new GameData(1, "white1",
                "black1", "game1", new ChessGame()));
        GameData updateGameData = new GameData(1, "grace", "serena",
                        "game1", new ChessGame());
        gameDao.updateGame(1, updateGameData);
        GameData game = gameDao.getGame(1);

        assertNotNull(game);
        assertEquals(1, game.gameID());
        assertEquals("grace", game.whiteUsername());
        assertEquals("serena", game.blackUsername());

    }

    @Test
    public void updateGameNegative() {
        gameDao.createGame(new GameData(1, "white1",
                "black1", "game1", new ChessGame()));

        GameData updateGameData = new GameData(99, "grace", "serena",
                "game1", new ChessGame());
        gameDao.updateGame(999, updateGameData);
        GameData game = gameDao.getGame(99);
        assertNull(game);
    }
}
