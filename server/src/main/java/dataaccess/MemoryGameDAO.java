package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{
    private final HashMap<String, GameData> gamesMap = new HashMap<>();

    @Override
    public void clear() {
        gamesMap.clear();
    }

    @Override
    public ArrayList<GameData> getGames() {
        ArrayList<GameData> gameList = new ArrayList<>();
        for (String key : gamesMap.keySet()) {
            gameList.add(gamesMap.get(key));
        }
        return gameList;
    }
}
