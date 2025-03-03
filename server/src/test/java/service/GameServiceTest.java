package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class GameServiceTest {
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
    public void goodRegisterGame() throws DataAccessException{
        UserData userData = new UserData("user", "123", "user@email");
        UserService registerUser = new UserService(user, auth);
        AuthData authResponse = registerUser.register(userData);
        authResponse = registerUser.logIn(userData);
        GameData currGame = new GameData("game");
        String gameID = null;
        GameService gameService = new GameService(game, auth);

        try{
            gameID = gameService.registerGame(currGame, authResponse.getToken());
        }
        catch (DataAccessException e) {}

        Assertions.assertNotNull(gameID);

    }

    @Test
    public void badRegisterGame() throws DataAccessException{
        UserData userData = new UserData("user", "123", "user@email");
        UserService registerUser = new UserService(user, auth);
        AuthData authResponse = registerUser.register(userData);
        authResponse = registerUser.logIn(userData);
        GameData currGame = new GameData("game");
        String gameID = null;
        GameService gameService = new GameService(game, auth);

        try{
            gameID = gameService.registerGame(currGame, authResponse.getToken() + "bad stuff");
        }
        catch (DataAccessException e) {
            Assertions.assertEquals("Error: unauthorized", e.getMessage());
        }
    }

    @Test
    public void goodJoinGame() throws DataAccessException{
        UserData userData = new UserData("user", "123", "user@email");
        UserService registerUser = new UserService(user, auth);
        AuthData authResponse = registerUser.register(userData);
        authResponse = registerUser.logIn(userData);
        GameData currGame = new GameData("game");
        GameService gameService = new GameService(game, auth);
        String gameID = gameService.registerGame(currGame, authResponse.getToken());
        GameData joinGame = new GameData("WHITE", gameID);

        UserData userData1 = new UserData("user1", "123", "user1@email");
        AuthData authResponse1 = registerUser.register(userData1);
        authResponse1 = registerUser.logIn(userData1);

        try{
            gameService.joinGame(joinGame, authResponse1.getToken());
        }
        catch (DataAccessException e) {}

        Assertions.assertNotNull(game.getGame(gameID).getWhite());
    }

    @Test
    public void badJoinGame() throws DataAccessException{
        UserData userData = new UserData("user", "123", "user@email");
        UserService registerUser = new UserService(user, auth);
        AuthData authResponse = registerUser.register(userData);
        authResponse = registerUser.logIn(userData);
        GameData currGame = new GameData("game");
        GameService gameService = new GameService(game, auth);
        String gameID = gameService.registerGame(currGame, authResponse.getToken());
        GameData joinGame = new GameData("WHITE", gameID);

        UserData userData1 = new UserData("user1", "123", "user1@email");
        AuthData authResponse1 = registerUser.register(userData1);
        authResponse1 = registerUser.logIn(userData1);

        try{
            gameService.joinGame(joinGame, authResponse1.getToken() + "bad stuff");
        }
        catch (DataAccessException e) {
            Assertions.assertEquals("Error: unauthorized", e.getMessage());
        }
    }

    @Test void goodListAllGames() throws DataAccessException{
        UserData userData = new UserData("user", "123", "user@email");
        UserService registerUser = new UserService(user, auth);
        AuthData authResponse = registerUser.register(userData);
        authResponse = registerUser.logIn(userData);
        GameService gameService = new GameService(game, auth);
        ArrayList<GameData> allGames = new ArrayList<>();

        GameData currGame = new GameData("game");
        String gameID = gameService.registerGame(currGame, authResponse.getToken());

        try{
            allGames = gameService.listGames(authResponse.getToken());
        }
        catch (DataAccessException e){}
        Assertions.assertNotNull(allGames);
    }

    @Test void badListAllGames() throws DataAccessException{
        UserData userData = new UserData("user", "123", "user@email");
        UserService registerUser = new UserService(user, auth);
        AuthData authResponse = registerUser.register(userData);
        authResponse = registerUser.logIn(userData);
        GameService gameService = new GameService(game, auth);
        ArrayList<GameData> allGames = new ArrayList<>();

        GameData currGame = new GameData("game");
        String gameID = gameService.registerGame(currGame, authResponse.getToken());

        try{
            allGames = gameService.listGames(authResponse.getToken() + "bad stuff");
        }
        catch (DataAccessException e) {
            Assertions.assertEquals("Error: unauthorized", e.getMessage());
        }
    }
}
