package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import server.errors.ClassError;
import server.errors.ServerExceptions;

import java.sql.SQLException;
import java.util.HashMap;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySQLUserDAO implements UserDAO {

    /**
     * This is my constructor, which creates and configures the database
     * @throws ServerExceptions if something goes wrong with the server
     */
    public MySQLUserDAO() throws ServerExceptions {
        try {
            DatabaseManager.createDatabase();  // Ensure database exists
            configureDatabase();
        } catch (DataAccessException e) {
            throw new ServerExceptions(ClassError.DATABASE_ERROR);
        }
    }

    /**
     * It selects all the userData that matches that username.
     * It then grabs the username, password, and email and writes it to
     * a UserData object. It then returns the userData object.
     *
     * @param username a string
     * @return the userData associated with that user
     */
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

    /**
     *  This inserts userData (including a username, encrypted password, and an email)
     *  into a row in the table, which are represented all as strings in the table
     *
     * @param userData this is the data associated with the user
     */
    @Override
    public void createUser(UserData userData) {
        var statement = "INSERT INTO userData (username, password, email) VALUES(?, ?, ?)";
        var username = userData.username();
        var clearTextPassword = userData.password();
        var email = userData.email();
        String hashedPassword = BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
        try {
            executeUpdate(statement, username, hashedPassword, email);
        } catch (DataAccessException | SQLException | ServerExceptions e) {
            throw new RuntimeException("Error occurred when trying to create authentication");
        }
    }


    /**
     * This clears all the data within the table
     */
    @Override
    public void clear() {
        var statement = "TRUNCATE userData";
        try {
            executeUpdate(statement);
        } catch (DataAccessException | SQLException | ServerExceptions e) {
            throw new RuntimeException("Error occurred when trying to clear");
        }
    }

    /**
     * Since the password is encrypted, we can't compare directly,
     * which is why this method is required. We return whether the username
     * exists, and whether the clear password matches the encrypted password
     * in our database
     *
     * @param username a string representing the username
     * @param clearPassword the password not encrypted
     * @return true if the password is correct
     */
    @Override
    public boolean verifyUser(String username, String clearPassword) {
        UserData userData = getUserByUsername(username);
        if (userData == null) {
            return false;
        }
        String storedHashedPassword = userData.password();
        return BCrypt.checkpw(clearPassword, storedHashedPassword);
    }

    /**
     * This selects everything from the user data. It then grabs the
     * username, password, and email (Side note: the password will be encrypted).
     * It then turns these into a userData object and puts that in a map with it's
     * associated username
     *
     * @return a map containing the username and the associated user data
     */
    @Override
    public HashMap<String, UserData> getUserMap() {
        HashMap<String, UserData> userMap = new HashMap<>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM userData";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String username = rs.getString("username");
                        String password = rs.getString("password");
                        String email = rs.getString("email");
                        userMap.put(username, new UserData(username, password, email));
                    }
                }
            }
            return userMap;
        } catch (DataAccessException | SQLException e) {
            throw new RuntimeException("Error occurred when trying to grab the map");
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
            CREATE TABLE IF NOT EXISTS userData (
                username VARCHAR(256) PRIMARY KEY,
                password VARCHAR(256) NOT NULL,
                email VARCHAR(256) NOT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """
    };

    /**
     *This executes a SQL update statement on the database using the provided
     * parameters. This can take parameters as strings or null values.
     *
     * @param statement the SQL update statement that is to be executed
     * @param params the parameters in that SQL statement.
     * @throws DataAccessException when there is an issue accessing the database
     * @throws SQLException when SQL error occurs during execution of update
     * @throws ServerExceptions when sever-related mishaps occur
     */
    private void executeUpdate(String statement, Object... params) throws DataAccessException, SQLException, ServerExceptions {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i =0;  i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) { ps.setString(i+1, p); }
                    else if (param == null) { ps.setNull(i+1, NULL); }
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

