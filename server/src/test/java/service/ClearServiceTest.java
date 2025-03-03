package service;

import dataaccess.*;
import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class ClearServiceTest {
    private UserDAO user = new UserDataDAO();
    private GameDAO game = new GameDataDAO();
    private AuthDAO auth = new AuthDataDAO();

    @BeforeEach
    public void setup() throws DataAccessException {
        user.clearAllUsers();
        game.clearAllGames();
        auth.clearAllAuth();
    }

    @Test
    public void clearApplication() throws DataAccessException{
        user.addUser("user", "123", "user@email");
        game.getGameName("new game");
        AuthData token = auth.addAuthToken("user");
        ClearService clear = new ClearService(user, game, auth);

        try{
            clear.clearApplication();
        }
        catch(DataAccessException e){}

        Assertions.assertNull(user.getUserWithUsername("user"));
        Assertions.assertNull(game.getGameID("new game"));
        Assertions.assertNull(auth.findToken(token.getToken()));
    }
}
