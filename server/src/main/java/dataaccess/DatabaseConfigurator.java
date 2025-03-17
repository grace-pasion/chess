package dataaccess;

import facade.errors.ClassError;
import facade.errors.ServerExceptions;

import java.sql.SQLException;

public class DatabaseConfigurator {
    /**
     * A multi-line string holding the statements
     */
    private String[] createStatements;

    /**
     * This is just the constructor
     * @param statements the statements to run into the configuration
     */
    DatabaseConfigurator(String[] statements) {
        createStatements = statements;
    }

    /**
     * Executes SQL statements to initialize the database.
     *
     * @throws ServerExceptions when something with the server goes wrong
     */
    public void configureDatabase() throws ServerExceptions {
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
}
