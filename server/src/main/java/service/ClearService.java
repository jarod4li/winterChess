package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

import javax.xml.crypto.Data;

public class ClearService {
    private UserDAO user;
    private GameDAO game;
    private AuthDAO auth;

    public ClearService(UserDAO user, GameDAO game, AuthDAO auth) {
        this.user = user;
        this.game = game;
        this.auth = auth;
    }

    public void clearApplication() throws DataAccessException{
        user.clearAllUsers();
        game.clearAllGames();
        auth.clearAllAuth();
    }
}
