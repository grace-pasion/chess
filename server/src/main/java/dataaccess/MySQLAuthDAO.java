package dataaccess;

import model.AuthData;
import server.errors.ServerExceptions;
import server.errors.ClassError;
import java.sql.SQLException;
import java.util.HashMap;

public class MySQLAuthDAO implements AuthDAO {

    public MySQLAuthDAO() throws ServerExceptions {
        configureDatabase();
    }
    @Override
    public void createAuth(String authToken, AuthData authData) {

    }

    @Override
    public void clear() {

    }

    @Override
    public HashMap<String, AuthData> getAuthMap() {
        return null;
    }

    @Override
    public AuthData getAuthData(String user) {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) {

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
}
