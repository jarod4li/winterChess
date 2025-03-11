package dataaccess;

import org.junit.jupiter.api.Assertions;

import dataaccess.DataAccessException;
import dataaccess.MySQLAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MySQLAuthTest {
    private MySQLAuthDAO authDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        authDAO = new MySQLAuthDAO();
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        authDAO.clearAllAuth();
    }

    @Test
    public void testAddAuthToken() throws DataAccessException {
        // Positive test
        String username = "testUser";
        AuthData authData = authDAO.addAuthToken(username);

        Assertions.assertNotNull(authData);
        Assertions.assertNotNull(authData.getToken());

        AuthData retrievedAuthData = authDAO.findToken(authData.getToken());
        Assertions.assertNotNull(retrievedAuthData);
        Assertions.assertEquals(username, retrievedAuthData.getUsername());
    }
    @Test
    public void testFindAuthToken() throws DataAccessException {
        // Positive test
        String username = "testUser";
        AuthData authData = authDAO.addAuthToken(username);

        Assertions.assertNotNull(authData);
        Assertions.assertNotNull(authData.getToken());

        AuthData retrievedAuthData = authDAO.findToken(authData.getToken());
        Assertions.assertNotNull(retrievedAuthData);
        Assertions.assertEquals(username, retrievedAuthData.getUsername());
    }
    @Test
    public void testAddAuthTokenNegative() throws DataAccessException{
        // Negative test: Find non-existent token
        String username = "testUser";
        AuthData authData = authDAO.addAuthToken(username);
        AuthData nonExistentAuthData = authDAO.findToken("nonExistentToken");
        Assertions.assertNull(nonExistentAuthData);
    }
    @Test
    public void testFindAuthTokenNegative() throws DataAccessException{
        // Negative test: Find non-existent token
        String username = "testUser";
        AuthData authData = authDAO.addAuthToken(username);
        AuthData nonExistentAuthData = authDAO.findToken("nonExistentToken");
        Assertions.assertNull(nonExistentAuthData);
    }

    @Test
    public void testRemoveAuthToken() throws DataAccessException {
        // Positive test
        String username = "testUser";
        AuthData authData = authDAO.addAuthToken(username);

        Assertions.assertNotNull(authData);

        authDAO.removeAuthToken(authData);

        AuthData retrievedAuthData = authDAO.findToken(authData.getToken());
        Assertions.assertNull(retrievedAuthData);
    }
    @Test
    public void testRemoveAuthTokenNegative() throws  DataAccessException{
        // Negative test: Attempt to remove non-existent token
        try {
            authDAO.removeAuthToken(new AuthData("nonExistentUser", "nonExistentToken"));
        } catch (DataAccessException e) {
            Assertions.assertEquals("Error", e.getMessage());
        }
    }

    @Test
    public void testClearAllAuth() throws DataAccessException {
        // Positive test
        String username1 = "testUser1";
        String username2 = "testUser2";

        authDAO.addAuthToken(username1);
        authDAO.addAuthToken(username2);

        authDAO.clearAllAuth();

        Assertions.assertNull(authDAO.findToken(username1));
        Assertions.assertNull(authDAO.findToken(username2));

    }
}



