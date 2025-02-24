package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
    //Do stuff like createGame and update game
    void clear();
    ArrayList<GameData> getGames();
    GameData createGame(GameData gameData);
    int generateGameID();
}
