package dataaccess;

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
    public void createGame(GameData gameData) {
        GameData newGame = new GameData(currentGameID,
                gameData.whiteUsername(),
                gameData.blackUsername(),
                gameData.gameName(),
                gameData.game());
        gamesMap.put(currentGameID, newGame);
        currentGameID++;
    }

    @Override
    public int generateGameID() {
        return currentGameID;
    }

    @Override
    public GameData getGame(int gameID) {
        return gamesMap.get(gameID);
    }

    @Override
    public void updateGame(int gameID, GameData gameData) {
        gamesMap.remove(gameID);
        gamesMap.put(gameID, gameData);
    }
}
