package server.handler;

public enum ClassError {
    BAD_REQUEST(400, "Error: bad request"),
    ALREADY_TAKEN(403, "Error: already taken"),
    INTERNAL_ERROR(500, "Error: internal server error");

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
