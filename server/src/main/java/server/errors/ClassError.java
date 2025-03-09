package server.errors;

/**
 * This is just an enum which holds all the standard
 * errors I could get.
 */
public enum ClassError {
    BAD_REQUEST(400, "Error: bad request"),
    ALREADY_TAKEN(403, "Error: already taken"),
    USER_NOT_FOUND(401, "Error: unauthorized"),
    INVALID_PASSWORD(401, "Error: unauthorized"),
    AUTHTOKEN_INVALID(401, "Error: unauthorized"),
    DATABASE_ERROR(500, "Error: database operation failed");

    private final int statusCode;
    private final String message;

    /**
     * This is just a constructor for my class error class.
     *
     * @param statusCode the current code to indicate what type of failure (or good thing) it is
     * @param message the message to identify the problem
     */
    ClassError(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
