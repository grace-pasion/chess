package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import server.errors.ClassError;
import server.errors.ServerExceptions;

import java.sql.SQLException;
import java.util.ArrayList;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySQLGameDAO implements GameDAO {

    public MySQLGameDAO() throws ServerExceptions {
        try {
            DatabaseManager.createDatabase();  // Ensure database exists
            configureDatabase();
        } catch (DataAccessException e) {
            throw new ServerExceptions(ClassError.DATABASE_ERROR);
        }
    }

    @Override
    public void clear() {
        var statement = "TRUNCATE gameData";
        try {
            executeUpdate(statement);
        } catch (DataAccessException | SQLException | ServerExceptions e) {
            throw new RuntimeException("Error occurred when trying to clear");
        }
    }

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
        return 1233;
    }

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


    private void configureDatabase() throws ServerExceptions {
        try (var conn = DatabaseManager.getConnection()) {
            for (String statement: createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException | DataAccessException ex) {
            throw new ServerExceptions(ClassError.DATABASE_ERROR);
        }
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

    //int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game
    private void executeUpdate(String statement, Object... params) throws DataAccessException, SQLException, ServerExceptions {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i =0;  i < params.length; i++) {
                    var param = params[i];
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case ChessGame p -> ps.setString(i + 1, new Gson().toJson(p));
                        case null -> ps.setNull(i + 1, NULL);
                        default -> {
                        }
                    }
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    rs.getInt(1);
                }

            }
        } catch (SQLException e) {
            throw new DataAccessException("Database update failed" + e.getMessage());
        }
    }
}
