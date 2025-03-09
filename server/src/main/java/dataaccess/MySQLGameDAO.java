package dataaccess;

import model.GameData;

import java.util.ArrayList;

public class MySQLGameDAO implements GameDAO {
    @Override
    public void clear() {

    }

    @Override
    public ArrayList<GameData> getGames() {
        return null;
    }

    @Override
    public void createGame(GameData gameData) {

    }

    @Override
    public int generateGameID() {
        return 0;
    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public void updateGame(int gameID, GameData gameData) {

    }
}
