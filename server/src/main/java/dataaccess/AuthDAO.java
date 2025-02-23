package dataaccess;

import model.AuthData;

public interface AuthDAO {
    //Do stuff with AUTH

    void createAuth(AuthData authData);
    void clear();
}
