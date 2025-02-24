package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{
    private final HashMap<Integer, GameData> gamesMap = new HashMap<>();
    private int currentGameID = 1233;

    @Override
    public void clear() {
        gamesMap.clear();
    }

    @Override
    public ArrayList<GameData> getGames() {
        ArrayList<GameData> gameList = new ArrayList<>();
        for (Integer key : gamesMap.keySet()) {
            gameList.add(gamesMap.get(key));
        }
        return gameList;
    }

    @Override
    public GameData createGame(GameData gameData) {
        int gameID = generateGameID();
        GameData newGame = new GameData(gameID,
                gameData.whiteUsername(),
                gameData.blackUsername(),
                gameData.gameName(),
                gameData.game());
        gamesMap.put(gameID, newGame);
        return newGame;
    }

    @Override
    public int generateGameID() {
        return currentGameID++;
    }
}
