package dataaccess;

import com.google.gson.Gson;
import model.AuthData;
import server.errors.ServerExceptions;
import server.errors.ClassError;
import java.sql.SQLException;
import java.util.HashMap;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySQLAuthDAO implements AuthDAO {

    public MySQLAuthDAO() throws ServerExceptions {
        try {
            DatabaseManager.createDatabase();  // Ensure database exists
            configureDatabase();
        } catch (DataAccessException e) {
            throw new ServerExceptions(ClassError.DATABASE_ERROR);
        }
    }

    @Override
    public void createAuth(String authToken, AuthData authData) {
        //var statement = "INSERT INTO authData (authToken, username, json) VALUES(?, ?, ?)";
        var statement = "INSERT INTO authData (authToken, username) VALUES(?, ?)";
        var username = authData.username();
        //var json = new Gson().toJson(authData);
        try {
            executeUpdate(statement, authToken, username);
            //executeUpdate(statement, authToken, username, json);
        } catch (DataAccessException | SQLException | ServerExceptions e) {
           throw new RuntimeException("Error occurred when trying to create authentication");
        }
    }

    @Override
    public void clear() {
        var statement = "TRUNCATE authData";
        try {
            executeUpdate(statement);
        } catch (DataAccessException | SQLException | ServerExceptions e) {
            throw new RuntimeException("Error occurred when trying to clear");
        }
    }

    @Override
    public HashMap<String, AuthData> getAuthMap()  {
        HashMap<String, AuthData> authMap = new HashMap<>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM authData";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String token = rs.getString("authToken");
                        String username = rs.getString("username");
                        authMap.put(token, new AuthData(token, username));
                    }
                }
            }
            return authMap;
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException("Error occurred when trying to grab the map");
        }
    }

    @Override
    public AuthData getAuthData(String user) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM authData WHERE username = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, user);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new AuthData(rs.getString("authToken"),
                                rs.getString("username"));
                    }
                }
            }
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException("Error occurred when getting the authData from username");
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) {
        var statement = "DELETE FROM authData WHERE authToken=?";
        try {
            executeUpdate(statement, authToken);
        } catch (DataAccessException | SQLException | ServerExceptions e) {
            throw new RuntimeException("Error occurred when trying to delete authentication");
        }
    }

    @Override
    public AuthData getDataFromAuthToken(String authToken) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM authData WHERE authToken = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new AuthData(authToken, rs.getString("username"));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error occurred when trying to get authData");
        }
        return null;
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
            CREATE TABLE IF NOT EXISTS authData (
                authToken VARCHAR(256) PRIMARY KEY,
                username VARCHAR(256) NOT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };


    private void executeUpdate(String statement, Object... params) throws DataAccessException, SQLException, ServerExceptions {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i =0;  i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i+1, p);
                    else if (param == null) ps.setNull(i+1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    rs.getInt(1);
                }

            }
        } catch (SQLException e) {
            throw new DataAccessException("Database update failed: " + e.getMessage());
        }
    }
}
