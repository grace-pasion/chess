import chess.*;

import facade.ServerFacade;
import facade.exception.ResponseException;

public class Main {
    public static void main(String[] args) throws ResponseException {
        ServerFacade serverFacade = new ServerFacade("http://localhost:8080");
        try {
            serverFacade.clear();
        } catch (ResponseException e) {
            System.out.println("Error in clearing");
        }
        Repl repl = new Repl("http://localhost:8080");
        repl.run();


    }
}

