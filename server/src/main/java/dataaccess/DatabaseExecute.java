package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import server.errors.ServerExceptions;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class DatabaseExecute {

    /**
     *This executes a SQL update statement on the database using the provided
     * parameters. This can take in any sort of parameters
     *
     * @param statement the SQL update statement that is to be executed
     * @param params the parameters in that SQL statement.
     * @throws DataAccessException when there is an issue accessing the database
     * @throws SQLException when SQL error occurs during execution of update
     * @throws ServerExceptions when sever-related mishaps occur
     */
    public void executeUpdate(String statement, Object... params) throws DataAccessException, SQLException, ServerExceptions {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                setParameters(ps, params);
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

    /**
     * This takes in the prepared statement and the parameters. This is able to
     * loop through the parameters setting the strings, so we can use many different parameter
     * types.
     * @param ps the prepared statement
     * @param params the parameters
     * @throws SQLException if something goes wrong with the SQL
     */
    private void setParameters(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            var param = params[i];

            if (param instanceof String p) {
                ps.setString(i + 1, p);
            } else if (param instanceof Integer p) {
                ps.setInt(i + 1, p);
            } else if (param instanceof ChessGame p) {
                ps.setString(i + 1, new Gson().toJson(p)); // Convert ChessGame to JSON
            } else if (param == null) {
                ps.setNull(i + 1, NULL);
            } else {
                throw new SQLException("Unsupported parameter type: " + param.getClass().getName());
            }
        }
    }

}
