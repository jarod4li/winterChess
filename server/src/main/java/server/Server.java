package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.*;

import java.util.ArrayList;
import java.util.Map;

public class Server {

    private UserService userService;
    private GameService gameService;
    private ClearService clearService;

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        UserDAO user = null;
        AuthDAO auth = null;
        GameDAO game = null;
        try {
            user = new MySQLUserDAO();
            auth = new MySQLAuthDAO();
            game = new MySQLGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        userService = new UserService(user, auth);
        gameService = new GameService(game, auth);
        clearService = new ClearService(user, game, auth);

        Spark.staticFiles.location("web");
        Spark.post("/user", this::registerHandler);
        Spark.post("/session", this::loginHandler);
        Spark.post("/game", this::createGameHandler);
        Spark.put("/game", this::joinGameHandler);
        Spark.get("/game", this::listGamesHandler);
        Spark.delete("/db", this::clearApplicationHandler);
        Spark.delete("/session", this::logoutHandler);
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public Object registerHandler(Request req, Response res) throws DataAccessException {
        try {
            UserData request = new Gson().fromJson(req.body(), UserData.class);
            AuthData authToken = userService.register(request);
            res.status(200);
            return new Gson().toJson(authToken);
        } catch (DataAccessException e) {
            var message = e.getMessage();
            if (message.equals("Error: bad request")) {
                res.status(400);
            } else if (message.equals("Error: already taken")) {
                res.status(403);
            } else {
                res.status(500);
            }
            return new Gson().toJson(Map.of("message", e.getMessage()));
        }
    }

    public Object loginHandler(Request req, Response res) throws DataAccessException {
        try {
            UserData request = new Gson().fromJson(req.body(), UserData.class);
            AuthData authToken = userService.logIn(request);
            res.status(200);
            return new Gson().toJson(authToken);
        } catch (DataAccessException e) {
            var message = e.getMessage();
            if (message.equals("Error: unauthorized")) {
                res.status(401);
            } else {
                res.status(500);
            }
            return new Gson().toJson(Map.of("message", e.getMessage()));
        }
    }

    public Object createGameHandler(Request req, Response res) throws DataAccessException {
        try {
            String token = req.headers("authorization");
            GameData request = new Gson().fromJson(req.body(), GameData.class);
            String gameID = gameService.registerGame(request, token);
            res.status(200);
            return new Gson().toJson(new GameData(null, gameID));
        } catch (DataAccessException e) {
            var message = e.getMessage();
            if (message.equals("Error: unauthorized")) {
                res.status(401);
            } else if (message.equals("Error: bad request")) {
                res.status(400);
            } else {
                res.status(500);
            }
            return new Gson().toJson(Map.of("message", e.getMessage()));
        }
    }

    public Object joinGameHandler(Request req, Response res) {
        try {
            String token = req.headers("authorization");
            GameData request = new Gson().fromJson(req.body(), GameData.class);
            gameService.joinGame(request, token);
            res.status(200);
            return "{}";
        } catch (DataAccessException e) {
            var message = e.getMessage();
            if (message.equals("Error: bad request")) {
                res.status(400);
            } else if (message.equals("Error: unauthorized")) {
                res.status(401);
            } else if (message.equals("Error: already taken")) {
                res.status(403);
            } else {
                res.status(500);
            }
            return new Gson().toJson(Map.of("message", e.getMessage()));
        }
    }


    public Object logoutHandler(Request req, Response res) throws DataAccessException {
        try {
            String token = req.headers("authorization");
            userService.logOut(token);
            res.status(200);
            return "{}";
        } catch (DataAccessException e) {
            var message = e.getMessage();
            if (message.equals("Error: unauthorized")) {
                res.status(401);
            } else {
                res.status(500);
            }
            return new Gson().toJson(Map.of("message", e.getMessage()));
        }
    }

    public Object listGamesHandler(Request req, Response res) {
        try {
            String token = req.headers("authorization");
            ArrayList<GameData> listGames = gameService.listGames(token);
            res.status(200);
            return new Gson().toJson(Map.of("games", listGames));
        } catch (DataAccessException e) {
            var message = e.getMessage();
            if (message.equals("Error: unauthorized")) {
                res.status(401);
            } else {
                res.status(500);
            }
            return new Gson().toJson(Map.of("message", e.getMessage()));
        }
    }

    public Object clearApplicationHandler(Request req, Response res) {
        try {
            clearService.clearApplication();
            res.status(200);
            return "{}";
        } catch (DataAccessException e) {
            res.status(500);
            return new Gson().toJson(Map.of("message", e.getMessage()));
        }
    }
}