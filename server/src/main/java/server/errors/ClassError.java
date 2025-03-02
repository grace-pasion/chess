package server.errors;

public enum ClassError {
    BAD_REQUEST(400, "Error: bad request"),
    ALREADY_TAKEN(403, "Error: already taken"),
    USER_NOT_FOUND(401, "Error: unauthorized"),
    INVALID_PASSWORD(401, "Error: unauthorized"),
    AUTHTOKEN_INVALID(401, "Error: unauthorized");

    private final int statusCode;
    private final String message;

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
