package dataaccess;
import model.*;
import chess.ChessGame;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Collection;
import java.sql.Connection;
import java.sql.SQLException;

public class GameTests {
    private static GameDAO myGame;
    @BeforeEach
    public void setup() throws DataAccessException, SQLException {
        myGame = new GameDAO();
        Connection connection = new DatabaseManager().getConnection();
        myGame.clear(connection);
        connection.close();
    }
    @Test
    public void clearTest(){
        try {
            myGame.createGame("testerGame");
            Connection connection = new DatabaseManager().getConnection();
            myGame.clear(connection);
            connection.close();
            Collection<GameData> gameColl = myGame.returnGameMap();
            assertTrue(gameColl.isEmpty());
            assertNotNull(gameColl);
        }
        catch (DataAccessException | SQLException exception) {
            throw new RuntimeException(exception);
        }
    }
    @Test
    public void createGameTest(){
        try{
            myGame.createGame("NameOGame");
        }
        catch(DataAccessException exception){
            throw new RuntimeException(exception);
        }
    }

    @Test
    public void dittoGameTest(){
        try {
            myGame.createGame("NameOGame");
            Connection connection = new DatabaseManager().getConnection();
            myGame.clear(connection);
            connection.close();
            GameData ditto = new GameData("NameOGame");
            assertThrows(DataAccessException.class, () -> myGame.createGame(ditto.getGameName()));
        }
        catch (DataAccessException | SQLException exception) {
            throw new RuntimeException(exception);
        }
    }
    @Test
    public void readGameTest(){
        try {
            GameData reading = myGame.returnGame(myGame.createGame("NameOGame"));
            assertNotNull(reading);
        } catch (DataAccessException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Test
    public void cantReadGameTest(){
        try {
            Connection connection = new DatabaseManager().getConnection();
            myGame.clear(connection);
            connection.close();
            GameData game = myGame.returnGame(myGame.createGame("NameOGame"));
            assertNotNull(game);
        }
        catch (DataAccessException | SQLException exception) {
            throw new RuntimeException(exception);
        }
    }
    @Test
    public void readGamesTest() {
        try {
            myGame.createGame("Mario");
            myGame.createGame("Luigi");
            myGame.createGame("Donkey Kong Too");
            Collection<GameData> gameMap = myGame.returnGameMap();
            assertFalse(gameMap.isEmpty());
            assertNotNull(gameMap);
        }
        catch (DataAccessException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Test
    public void cantReadGamesTest(){
        try {
            Connection connection = new DatabaseManager().getConnection();
            myGame.clear(connection);
            connection.close();
            Collection<GameData> gameMap = myGame.returnGameMap();
            assertTrue(gameMap.isEmpty());
            assertNotNull(gameMap);
        }
        catch (DataAccessException | SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Test
    public void playerNamerPassTest(){
        try {
            int jaredId = myGame.createGame("Jared's Game");
            String username = "Jaredizer";
            myGame.playerNamer(username, jaredId, ChessGame.TeamColor.WHITE);
            GameData jaredGame = myGame.returnGame(jaredId);
            assertEquals(username, jaredGame.getWhiteUsername());
            assertNotNull(jaredGame);
            myGame.playerNamer(username, jaredId, ChessGame.TeamColor.BLACK);
            jaredGame = myGame.returnGame(jaredId);
            assertEquals(username, jaredGame.getBlackUsername());
            assertNotNull(jaredGame);
        }
        catch (DataAccessException exception) {
            throw new RuntimeException(exception);
        }
    }
    @Test
    public void playerNamerFailTest() {
        try {
            int JaredId = myGame.createGame("Jared's Game");
            String jaredUsername = "Jaredizer";
            String jonahUsername = "Jo Bro";
            myGame.playerNamer(jaredUsername, JaredId, ChessGame.TeamColor.WHITE);
            assertThrows(DataAccessException.class, () -> myGame.playerNamer(jonahUsername, JaredId, ChessGame.TeamColor.WHITE));
        }
        catch (DataAccessException exception) {
            throw new RuntimeException(exception);
        }
    }
}
