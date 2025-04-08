package dataaccess;

import org.junit.jupiter.api.Assertions;
import dataaccess.DataAccessException;
import dataaccess.MySQLUserDAO;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MySQLUserTest {
    private MySQLUserDAO userDAO;
    private final String username = "testUser";
    private final String password = "testPassword";
    private final String email = "test@example.com";

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO = new MySQLUserDAO();
        userDAO.clearAllUsers();
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        userDAO.clearAllUsers();
    }

    @Test
    public void testAddUserWithUsernamePositive() throws DataAccessException {
        userDAO.addUser(username, password, email);
        UserData userData = userDAO.getUserWithUsername(username);

        Assertions.assertNotNull(userData);
        Assertions.assertEquals(username, userData.getName());
        Assertions.assertEquals(email, userData.getEmail());
    }
    @Test
    public void testGetUserWithUsernamePositive() throws DataAccessException {
        userDAO.addUser(username, password, email);
        UserData userData = userDAO.getUserWithUsername(username);

        Assertions.assertNotNull(userData);
        Assertions.assertEquals(username, userData.getName());
        Assertions.assertEquals(email, userData.getEmail());
    }
    @Test
    public void testAddUserWithUsernameNegative() throws DataAccessException {
        try {
            userDAO.addUser(null, password, email);
        } catch (DataAccessException e) {
            Assertions.assertEquals("Error: Column 'username' cannot be null", e.getMessage());
        }
    }
    @Test
    public void testGetUserWithUsernameNegative() throws DataAccessException {
        try {
            userDAO.addUser(null, password, email);
        } catch (DataAccessException e) {
            Assertions.assertEquals("Error: Column 'username' cannot be null", e.getMessage());
        }
    }
    @Test
    public void testAddUserWithEmailPositive() throws DataAccessException {

        userDAO.addUser(username, password, email);
        UserData userData = userDAO.getUserWithEmail(email);

        Assertions.assertNotNull(userData);
        Assertions.assertEquals(username, userData.getName());
        Assertions.assertEquals(email, userData.getEmail());
    }
    @Test
    public void testGetUserWithEmailPositive() throws DataAccessException {

        userDAO.addUser(username, password, email);
        UserData userData = userDAO.getUserWithEmail(email);

        Assertions.assertNotNull(userData);
        Assertions.assertEquals(username, userData.getName());
        Assertions.assertEquals(email, userData.getEmail());
    }
    @Test
    public void testAddUserWithEmailNegative() throws DataAccessException {
        String username = "testUser";
        String password = "testPassword";

        try {
            userDAO.addUser(username, password, null);
        } catch (DataAccessException e) {
            Assertions.assertEquals("Error: Column 'email' cannot be null", e.getMessage());
        }
    }
    @Test
    public void testGetUserWithEmailNegative() throws DataAccessException {
        String username = "testUser";
        String password = "testPassword";

        try {
            userDAO.addUser(username, password, null);
        } catch (DataAccessException e) {
            Assertions.assertEquals("Error: Column 'email' cannot be null", e.getMessage());
        }
    }

    @Test
    public void testClearAllUsers() throws DataAccessException {

        userDAO.addUser(username, password, email);
        userDAO.clearAllUsers();

        Assertions.assertNull(userDAO.getUserWithUsername(username));
        Assertions.assertNull(userDAO.getUserWithEmail(email));
    }
}