package dataaccess;

import com.google.gson.Gson;
import model.GameData;
import server.errors.ClassError;
import server.errors.ServerExceptions;

import java.sql.SQLException;
import java.util.ArrayList;

public class MySQLGameDAO implements GameDAO {

    /**
     * This is my constructor, which creates and configures the database
     * @throws ServerExceptions if something goes wrong with the server
     */
    public MySQLGameDAO() throws ServerExceptions {
        try {
            DatabaseManager.createDatabase();  // Ensure database exists
            configureDatabase();
        } catch (DataAccessException e) {
            throw new ServerExceptions(ClassError.DATABASE_ERROR);
        }
    }

    /**
     * This clears the data from my gameData table in my database
     */
    @Override
    public void clear() {
        var statement = "TRUNCATE gameData";
        try {
            executeUpdate(statement);
        } catch (DataAccessException | SQLException | ServerExceptions e) {
            throw new RuntimeException("Error occurred when trying to clear");
        }
    }

    /**
     * This selects the gameID and the json of gameData from the table.
     * It then turns that json to a gameData and adds it to the list, which
     * it returns
     *
     * @return an arrayList of all the different games and their data
     */
    @Override
    public ArrayList<GameData> getGames() {
       ArrayList<GameData> listOfGames = new ArrayList<>();
       try (var conn = DatabaseManager.getConnection()) {
           var statement = "SELECT gameID, json FROM gameData";
           try (var ps = conn.prepareStatement(statement)) {
               try (var rs = ps.executeQuery()) {
                   while (rs.next()) {
                       var json = rs.getString("json");
                       var gameData = new Gson().fromJson(json, GameData.class);
                       listOfGames.add(gameData);
                   }
               }
           }
           return listOfGames;
       } catch (DataAccessException | SQLException e) {
           throw new RuntimeException("Error occurred when trying to get list of games");
       }
    }

    /**
     * This takes in gameData and adds it to a row in the table.
     * This row will represent one game.
     *
     * @param gameData the gameData to be added to the database
     */
    @Override
    public void createGame(GameData gameData) {
        var statement = "INSERT INTO gameData (gameName, whiteUsername, blackUsername, json) VALUES (?, ?, ?, ?)";
        var json = new Gson().toJson(gameData);
        String whiteUsername = gameData.whiteUsername() != null ? gameData.whiteUsername() : "";
        String blackUsername = gameData.blackUsername() != null ? gameData.blackUsername() : "";
        try {
            executeUpdate(statement, gameData.gameName(), whiteUsername, blackUsername, json);
        } catch (DataAccessException | SQLException | ServerExceptions e) {
            throw new RuntimeException("Error occurred when trying to create the game");
        }
    }

    /**
     * This gives a gameID to each game. It grabs the highest current gameID
     * from the database, and it increases it by 1. This way, there are no
     * @return an integer representing the gameID
     */
    @Override
    public int generateGameID() {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT MAX(gameID) FROM gameData";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1)+1;
                    }
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Error occurred when trying to create authentication");
        }
        return 1;
    }

    /**
     * This selects the gameID and the gameData (in json form) that matches the
     * passed in gameID. It then converts the json to GameData and returns it.
     *
     * @param gameID the gameID of the current game
     * @return the gameData associated with that gameID
     */
    @Override
    public GameData getGame(int gameID) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, json FROM gameData WHERE gameID = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var json = rs.getString("json");
                        return new Gson().fromJson(json, GameData.class);
                    }
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Error occurred when trying to get the game");
        }
        return null;
    }

    /**
     * This grabs the column we are updating based on the gameID. It
     * then updates the data in that row.
     *
     * @param gameID the gameID associated with the current game
     * @param gameData the game data associated with the current game
     */
    @Override
    public void updateGame(int gameID, GameData gameData) {
        var statement = "UPDATE gameData SET gameName = ?, whiteUsername = ?," +
                " blackUsername = ?, json = ? WHERE gameID = ?";
        var json = new Gson().toJson(gameData);
        String whiteUsername = gameData.whiteUsername() != null ? gameData.whiteUsername() : "";
        String blackUsername = gameData.blackUsername() != null ? gameData.blackUsername() : "";
        try {
            executeUpdate(statement, gameData.gameName(), whiteUsername, blackUsername, json, gameID);
        } catch (DataAccessException | SQLException | ServerExceptions e) {
            throw new RuntimeException("Error occurred when updating the game");
        }
    }

    /**
     * Executes SQL statements to initialize the database.
     *
     * @throws ServerExceptions when something with the server goes wrong
     */
    private void configureDatabase() throws ServerExceptions {
        DatabaseConfigurator config = new DatabaseConfigurator(createStatements);
        config.configureDatabase();
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS gameData (
                gameID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                whiteUsername VARCHAR(256),
                blackUsername VARCHAR(256),
                gameName VARCHAR(256) NOT NULL,
                json TEXT DEFAULT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """
    };

    /**
     *This executes a SQL update statement on the database using the provided
     * parameters. This can take parameters as string, integers, and the ChessGame (which
     * is then converted into a json string), and a null value.
     *
     * @param statement the SQL update statement that is to be executed
     * @param params the parameters in that SQL statement.
     * @throws DataAccessException when there is an issue accessing the database
     * @throws SQLException when SQL error occurs during execution of update
     * @throws ServerExceptions when sever-related mishaps occur
     */
    private void executeUpdate(String statement, Object... params) throws DataAccessException, SQLException, ServerExceptions {
        DatabaseExecute execute = new DatabaseExecute();
        execute.executeUpdate(statement, params);
    }
}
