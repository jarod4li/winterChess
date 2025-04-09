package client;

import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;
import ui.GameConsole;
import ui.ServerFacade;

import java.io.ByteArrayOutputStream;

import java.io.PrintStream;
import java.util.ArrayList;

public class ServerFacadeTests {

    private String username = "jared";
    private String password = "password";
    private String email = "jbou234@gmail.com";
    private String gameName = "new game";
    private static Server server;
    static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        serverFacade = new ServerFacade(port);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDatabase() {
        serverFacade.clear();
    }
    @Test
    public void testRegisterPositive() {
        String authToken = serverFacade.register(username, password, email);
        Assertions.assertNotNull(authToken);
    }

    @Test
    public void testRegisterNegative() {
        String token = serverFacade.register(username, password, email);
        Assertions.assertNotNull(token);

        token = serverFacade.register(username, password, email);
        Assertions.assertNull(token);
    }

    @Test
    public void testLoginPositive() {
        String token = serverFacade.register(username, password, email);
        serverFacade.logout(token);
        token = serverFacade.login(username, password);
        Assertions.assertNotNull(token);
    }

    @Test
    public void testLoginNegative() {
        String authToken = serverFacade.login(username + "stuff", password);
        Assertions.assertNull(authToken);
    }

    @Test
    public void testLogoutPositive() {
        String token = serverFacade.register(username, password, email);
        Assertions.assertDoesNotThrow(() -> serverFacade.logout(token));
    }

    @Test
    public void testLogoutNegative() {
        String token1 = serverFacade.register(username, password, email);
        serverFacade.logout(token1);
        String token2 = serverFacade.login(username, password);
        Assertions.assertNotEquals(token1, token2);
    }

    @Test
    public void testCreateGamePositive() {
        String token = serverFacade.register(username,password, email);
        String gameID = serverFacade.createGame(gameName, token);
        Assertions.assertNotNull(gameID);
    }

    @Test
    public void testCreateGameNegative() {
        String tokenResponse = serverFacade.createGame(gameName, "random token");
        Assertions.assertEquals("Error: unauthorized", tokenResponse);
    }
    @Test
    public void testJoinGamePositive() {
        String token = serverFacade.register(username, password, email);
        String gameID = serverFacade.createGame(gameName, token);
        boolean joinGameSuccess = serverFacade.joinGame(gameID, "WHITE", token);
        Assertions.assertTrue(joinGameSuccess);
    }

    @Test
    public void testJoinGameNegative() {
        String token = serverFacade.register(username, password, email);
        String gameID = serverFacade.createGame(gameName, token);
        boolean joinGameSuccess = serverFacade.joinGame("2", "WHITE", token);
        Assertions.assertFalse(joinGameSuccess);
    }

    @Test
    public void testListAllGamesPositive() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        String token = serverFacade.register(username, password, email);
        String gameID = serverFacade.createGame(gameName, token);
        Assertions.assertDoesNotThrow(() -> serverFacade.listAllGames(token));
    }

    @Test
    public void testListAllGamesNegative() {
        String token = serverFacade.register(username, password, email);
        String gameID = serverFacade.createGame(gameName, token);
        Assertions.assertDoesNotThrow(()->serverFacade.listAllGames("randomToken"));

    }

    @Test
    public void testClear() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        String token = serverFacade.register(username, password, email);
        String gameID = serverFacade.createGame(gameName, token);
        serverFacade.clear();

        System.setOut(System.out);
        String printedMessage = outputStream.toString();
        Assertions.assertFalse(printedMessage.contains("Failed"));
    }
}