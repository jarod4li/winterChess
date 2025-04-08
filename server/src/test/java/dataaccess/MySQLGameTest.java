package dataaccess;

import org.junit.jupiter.api.Assertions;
import dataaccess.DataAccessException;
import dataaccess.MySQLGameDAO;
import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class MySQLGameTest {
    private MySQLGameDAO gameDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        gameDAO = new MySQLGameDAO();
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        gameDAO.clearAllGames();
    }

    @Test
    public void testAddGame() throws DataAccessException {
        // Positive test
        String gameName = "testGame";
        GameData testGame = new GameData(gameName);

        gameDAO.addGame(testGame);
        GameData retrievedGame = gameDAO.getGame("1");

        Assertions.assertNotNull(retrievedGame);
        Assertions.assertEquals(gameName, retrievedGame.getName());

    }
    @Test
    public void testGetGame() throws DataAccessException {
        // Positive test
        String gameName = "testGame";
        GameData testGame = new GameData(gameName);

        gameDAO.addGame(testGame);
        GameData retrievedGame = gameDAO.getGame("1");

        Assertions.assertNotNull(retrievedGame);
        Assertions.assertEquals(gameName, retrievedGame.getName());

    }
    @Test
    public void testAddGameNegative() throws DataAccessException{
        // Negative test
        try {
            GameData nullNameGame = new GameData(null, null, null, null);
            gameDAO.addGame(nullNameGame);
        } catch (DataAccessException e) {
            Assertions.assertEquals("Error: Column 'gameName' cannot be null", e.getMessage());
        }
    }
    @Test
    public void testGetGameNegative() throws DataAccessException{
        // Negative test
        try {
            GameData nullNameGame = new GameData(null, null, null, null);
            gameDAO.addGame(nullNameGame);
        } catch (DataAccessException e) {
            Assertions.assertEquals("Error: Column 'gameName' cannot be null", e.getMessage());
        }
    }
    @Test
    public void testSetGame() throws DataAccessException {
        // Positive test
        String gameName = "testGame";
        GameData testGame = new GameData(gameName, "1", null, null);

        gameDAO.addGame(testGame);
        Assertions.assertTrue(gameDAO.setGame(testGame, "WHITE", "user1"));
    }
    @Test
    public void testSetGameNegative() throws DataAccessException{
        String gameName = "testGame";
        GameData testGame = new GameData(gameName, "1", null, null);
        // Negative test: Attempt to set player when already occupied
        GameData testGame2 = new GameData(gameName, "2", "user1", null);
        gameDAO.addGame(testGame2);
        Assertions.assertFalse(gameDAO.setGame(testGame2, "WHITE", "user2"));
    }

    @Test
    public void testGetList() throws DataAccessException {
        // Positive test
        String gameName1 = "testGame1";
        String gameName2 = "testGame2";

        GameData testGame1 = new GameData(gameName1, "1", null, null);
        GameData testGame2 = new GameData(gameName2, "2", null, null);

        gameDAO.addGame(testGame1);
        gameDAO.addGame(testGame2);

        ArrayList<GameData> gamesList = gameDAO.getList();

        Assertions.assertEquals(2, gamesList.size());
        Assertions.assertEquals(gameName1, gamesList.get(0).getName());
        Assertions.assertEquals(gameName2, gamesList.get(1).getName());
    }

    @Test
    public void testClearAllGames() throws DataAccessException {
        // Positive test
        String gameName = "testGame";
        GameData testGame = new GameData(gameName, "1", null, null);

        gameDAO.addGame(testGame);
        gameDAO.clearAllGames();

        Assertions.assertTrue(gameDAO.getList().isEmpty());
    }
}
