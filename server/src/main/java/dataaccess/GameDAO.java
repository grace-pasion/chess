package dataaccess;

import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
    void clear();
    ArrayList<GameData> getGames();
    void createGame(GameData gameData);
    int generateGameID();
    GameData getGame(int gameID);
    void updateGame(int gameID, GameData gameData);
}
