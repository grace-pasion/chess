package model;

import chess.ChessGame;

/**
 *  This just stores all the information surrounding the game, such as
 *  the positions of the pieces, the game name, the usernames, etc
 *
 * @param gameID the numeric value of the game ID
 * @param whiteUsername the username of the white player
 * @param blackUsername the username of the black player
 * @param gameName the name of the current game
 * @param game all the information surrounding the game (the position
 *             of the characters, etc)
 */
public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    //automatically generated getters
    @Override
    public int gameID() {
        return gameID;
    }

    @Override
    public String whiteUsername() {
        return whiteUsername;
    }

    @Override
    public String blackUsername() {
        return blackUsername;
    }

    @Override
    public String gameName() {
        return gameName;
    }

    @Override
    public ChessGame game() {
        return game;
    }
}
