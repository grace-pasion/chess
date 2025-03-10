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
        configureDatabase();
    }

    @Override
    public void createAuth(String authToken, AuthData authData) {
        var statement = "INSERT INTO authData (token, userId, json) VALUES(?, ?, ?)";
        var username = authData.username();
        var json = new Gson().toJson(authData);
        try {
            executeUpdate(statement, authToken, username, json);
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
    public HashMap<String, AuthData> getAuthMap() {
        return null;
        //just get the whole table somehow
    }

    @Override
    public AuthData getAuthData(String user) {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) {
        var statement = "DELETE FROM authData WHERE token=?";
        try {
            executeUpdate(statement);
        } catch (DataAccessException | SQLException | ServerExceptions e) {
            throw new RuntimeException("Error occurred when trying to delete authentication");
        }
    }

    @Override
    public AuthData getDataFromAuthToken(String authToken) {
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
                username VARCHAR(256) NOT NULL,
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    //only needs to handle strings since authData has
    //authToken and username? or need to do the pet thing they did
    //in petshop?

    //Also made it void, since it doesn't utilize the int it returns
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
