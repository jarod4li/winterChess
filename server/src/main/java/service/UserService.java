package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

public class UserService {
    private UserDAO user;
    private AuthDAO auth;

    public UserService(UserDAO user, AuthDAO auth){
        this.user = user;
        this.auth = auth;
    }

    public AuthData register(UserData newUser) throws DataAccessException {
        AuthData authToken = null;
        String username = newUser.getName();
        String email = newUser.getEmail();
        String password = newUser.getPassword();

        // Check if there is user with username
        if (user.getUserWithUsername(username) != null) {
            throw new DataAccessException("Error: already taken");
        }

        // Check if there is user with email
        if (user.getUserWithEmail(email) != null) {
            throw new DataAccessException("Error: already taken");
        }

        if (username == null || email == null || password == null) {
            throw new DataAccessException("Error: bad request");
        }

        // Create new user and new authToken
        else {
            user.addUser(username, password, email);
            authToken = auth.addAuthToken(username);
        }
        return authToken;
    }

    public AuthData logIn(UserData newUser) throws DataAccessException {
        String username = newUser.getName();
        String password = newUser.getPassword();
        AuthData authToken = null;

        UserData storedUser = user.getUserWithUsername(username);
        if (storedUser == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        if (!storedUser.getPassword().equals(password)) {
            throw new DataAccessException("Error: unauthorized");
        }

        authToken = auth.addAuthToken(username);
        return authToken;
    }

    public void logOut(String token) throws DataAccessException {
        AuthData authToken = auth.findToken(token);
        if (authToken == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        auth.removeAuthToken(authToken);
    }
}