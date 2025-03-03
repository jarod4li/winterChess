package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.rmi.ServerException;


public class UserServiceTest {
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
    public void goodRegister() throws ServerException, DataAccessException {
        UserData userData = new UserData("user1", "123", "user1@email");
        UserService registerUser = new UserService(user, auth);
        AuthData authResponse = null;
        try {
            authResponse = registerUser.register(userData);
        }
        catch (Exception e){}

        Assertions.assertNotNull(authResponse);

    }

    @Test
    public void badRegister() throws DataAccessException {
        UserData userData = new UserData(null, "123", "user1@email");
        UserService registerUser = new UserService(user, auth);
        AuthData authResponse = null;
        try {
            authResponse = registerUser.register(userData);
        }
        catch (DataAccessException e) {
            Assertions.assertEquals("Error: bad request", e.getMessage());
        }
    }

    @Test
    public void goodLogIn() throws  DataAccessException{
        UserData userData = new UserData("user1", "123", "user1@email");
        UserService registerUser = new UserService(user, auth);
        AuthData authResponse = registerUser.register(userData);
        AuthData authResponseLogin = null;

        try {
            authResponseLogin = registerUser.logIn(userData);
        }
        catch (DataAccessException e) {}

        Assertions.assertNotNull(authResponseLogin);
    }

    @Test
    public void badLogIn() throws  DataAccessException{
        UserData userData = new UserData("user1", "123", "user1@email");
        UserService registerUser = new UserService(user, auth);
        AuthData authResponse = registerUser.register(userData);
        UserData userDataWrongPassword = new UserData("user1", "321", "user1@email");
        AuthData authResponseLogin = null;

        try {
            authResponseLogin = registerUser.logIn(userDataWrongPassword);
        }
        catch (DataAccessException e) {
            Assertions.assertEquals("Error: unauthorized", e.getMessage());
        }
    }

    @Test
    public void goodLogOut() throws DataAccessException{
        UserData userData = new UserData("user1", "123", "user1@email");
        UserService registerUser = new UserService(user, auth);
        AuthData authResponse = registerUser.register(userData);
        authResponse = registerUser.logIn(userData);

        try{
            registerUser.logOut(authResponse.getToken());
        }
        catch (DataAccessException e){}

        Assertions.assertNull(auth.findToken(authResponse.getToken()));
    }

    @Test
    public void badLogOut() throws DataAccessException{
        UserData userData = new UserData("user1", "123", "user1@email");
        UserService registerUser = new UserService(user, auth);
        AuthData authResponse = registerUser.register(userData);
        authResponse = registerUser.logIn(userData);

        try{
            registerUser.logOut(authResponse.getToken() + "bad stuff");
        }
        catch (DataAccessException e) {
            Assertions.assertEquals("Error: unauthorized", e.getMessage());
        }
    }
}
