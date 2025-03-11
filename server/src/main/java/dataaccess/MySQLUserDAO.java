package dataaccess;

import model.AuthData;
import model.UserData;
import server.errors.ClassError;
import server.errors.ServerExceptions;

import java.sql.SQLException;
import java.util.HashMap;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySQLUserDAO implements UserDAO {

    public MySQLUserDAO() throws ServerExceptions {
        configureDatabase();
    }

    @Override
    public UserData getUserByUsername(String username) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM userData WHERE username = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new UserData(rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("email"));
                    }
                }
            }
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException("Error occurred when getting the authData from username");
        }
        return null;
    }

    @Override
    public void createUser(UserData userData) {
        //username password email
    }

    @Override
    public void clear() {
        var statement = "TRUNCATE userData";
        try {
            executeUpdate(statement);
        } catch (DataAccessException | SQLException | ServerExceptions e) {
            throw new RuntimeException("Error occurred when trying to clear");
        }
    }

    @Override
    public HashMap<String, UserData> getUserMap() {
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
            CREATE TABLE IF NOT EXISTS userData (
                username VARCHAR(256) PRIMARY KEY,
                password VARCHAR(256) NOT NULL,
                email VARCHAR(256) NOT NULL,
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

