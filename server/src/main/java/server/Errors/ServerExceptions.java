package server.Errors;

public class ServerExceptions extends Exception {
    private final ClassError error;

    public ServerExceptions(ClassError error) {
        super(error.getMessage());
        this.error = error;
    }

    public ClassError getError() {
        return error;
    }

}
