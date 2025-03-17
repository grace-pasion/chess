package dataaccess;

import model.AuthData;
import facade.errors.ServerExceptions;
import facade.errors.ClassError;
import java.sql.SQLException;
import java.util.HashMap;

public class MySQLAuthDAO implements AuthDAO {

    /**
     * This is my constructor, which creates and configures the database
     * @throws ServerExceptions if something with the server stuff goes wrong
     */
    public MySQLAuthDAO() throws ServerExceptions {
        try {
            DatabaseManager.createDatabase();
            configureDatabase();
        } catch (DataAccessException e) {
            throw new ServerExceptions(ClassError.DATABASE_ERROR);
        }
    }


    /**
     * This creates an insert statement to put the authToken and
     * username within the row of the table
     *
     * @param authToken this is the authentication string
     * @param authData this is the data associated with authentication
     *                 (username and the authToken)
     */
    @Override
    public void createAuth(String authToken, AuthData authData) {
        var statement = "INSERT INTO authData (authToken, username) VALUES(?, ?)";
        var username = authData.username();
        try {
            executeUpdate(statement, authToken, username);
        } catch (DataAccessException | SQLException | ServerExceptions e) {
           throw new RuntimeException("Error occurred when trying to create authentication");
        }
    }

    /**
     * This clears all the data within the table
     */
    @Override
    public void clear() {
        var statement = "TRUNCATE authData";
        try {
            executeUpdate(statement);
        } catch (DataAccessException | SQLException | ServerExceptions e) {
            throw new RuntimeException("Error occurred when trying to clear");
        }
    }

    /**
     * This function gets all the data. Then it gets the string
     * of the authToken and username. It then puts these into
     * a map.
     *
     * @return a map containing the username and the authData
     */
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

    /**
     * This function selects the authData that is associated with the
     * username by using an SQL select statement. It then returns
     * this authData.
     *
     * @param user the username
     * @return the authData
     */
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

    /**
     * This grabs the authData when it matches the correct authToken,
     * and it deletes this same statement
     *
     * @param authToken the authentication string
     */
    @Override
    public void deleteAuth(String authToken) {
        var statement = "DELETE FROM authData WHERE authToken=?";
        try {
            executeUpdate(statement, authToken);
        } catch (DataAccessException | SQLException | ServerExceptions e) {
            throw new RuntimeException("Error occurred when trying to delete authentication");
        }
    }

    /**
     * This selects the authData when it matches the correct authToken. It
     * then returns that authData.
     *
     * @param authToken the authentication string
     * @return the authData associated with the user
     */
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
            CREATE TABLE IF NOT EXISTS authData (
                authToken VARCHAR(256) PRIMARY KEY,
                username VARCHAR(256) NOT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };


    /**
     *This executes a SQL update statement on the database using the provided
     * parameters. This can take parameters as string or null values.
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
