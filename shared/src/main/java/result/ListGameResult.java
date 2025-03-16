package result;
import model.GameData;

import java.util.ArrayList;

/**
 * The results of whether list games functions worked
 * @param games the list of games that the game database stores
 */
public record ListGameResult(ArrayList<GameData> games) {
    public ArrayList<GameData> getGames() {
        return games;
    }
}
