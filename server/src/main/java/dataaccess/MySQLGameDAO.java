package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MySQLGameDAO implements GameDAO{
    private final Gson gson;
    public MySQLGameDAO() throws DataAccessException{
        gson = new Gson();
        configureDatabase();
    }
    private static final String[] GAMES_TABLE_QUERY = {
            "CREATE TABLE IF NOT EXISTS games (" +
                    "gameName VARCHAR(255) NOT NULL, " +
                    "gameID VARCHAR(255) PRIMARY KEY, " +
                    "whiteUsername VARCHAR(255), " +
                    "blackUsername VARCHAR(255), " +
                    "game TEXT NOT NULL" +
                    ")"
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();

        try (Connection conn = DatabaseManager.getConnection()) {
            for (String query : GAMES_TABLE_QUERY) {
                try (PreparedStatement statement = conn.prepareStatement(query)) {
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: " + e.getMessage());
        }
    }
    private int count = 1;
    @Override
    public void addGame(GameData newGame) throws DataAccessException{
        String gameID = String.valueOf(count);
        if (newGame.getGame() == null){
            newGame.setGame();
        }
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement("INSERT INTO games (gameName, gameID, whiteUsername, blackUsername, game) VALUES (?, ?, ?, ?, ?)")) {
            statement.setString(1, newGame.getName());
            statement.setString(2, gameID);
            statement.setString(3, newGame.getWhite());
            statement.setString(4, newGame.getBlack());
            statement.setString(5, serializeGame(newGame.getGame()));
            statement.executeUpdate();
            count++;

        } catch (SQLException e) {
            throw new DataAccessException("Error: " + e.getMessage());
        }
    }
    @Override
    public String getGameID(String gameName) throws DataAccessException{
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement("SELECT * FROM games WHERE gameName=?")) {
            statement.setString(1, gameName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("gameID");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: " + e.getMessage());
        }
        return null;
    }
    @Override
    public Boolean getGameName(String gameName) throws DataAccessException{
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement("SELECT * FROM games WHERE gameName=?")) {
            statement.setString(1, gameName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String gameID = resultSet.getString("gameID");
                    return gameID != null;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: " + e.getMessage());
        }
        return false;
    }
    @Override
    public GameData getGame(String gameID) throws DataAccessException{
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement("SELECT gameName, whiteUsername, blackUsername, game FROM games WHERE gameID = ?")) {
            statement.setString(1, gameID);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String whiteUsername = resultSet.getString("whiteUsername");
                    String blackUsername = resultSet.getString("blackUsername");
                    String gameName = resultSet.getString("gameName");
                    String game = resultSet.getString("game");
                    return new GameData(gameName, gameID, whiteUsername, blackUsername, deserializeGame(game));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: " + e.getMessage());
        }
        return null;
    }
    @Override
    public Boolean setGame(GameData currGame, String playerColor, String username) throws DataAccessException{
        GameData newGame = getGame(currGame.getGameID());
        if (newGame == null){
            return false;
        }
        if (playerColor == null) {
            return true;
        }
        if (playerColor.equals("WHITE") && (newGame.getWhite() == null)){
            newGame.setWhite(username);
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement statement = conn.prepareStatement("DELETE FROM games WHERE gameID = ?")) {
                statement.setString(1, newGame.getGameID());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException("Error: " + e.getMessage());
            }
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement statement = conn.prepareStatement("INSERT INTO games (gameName, gameID, whiteUsername, blackUsername, game) Values(?,?,?,?,?) ")) {
                statement.setString(1, newGame.getName());
                statement.setString(2, newGame.getGameID());
                statement.setString(3, username);
                statement.setString(4, newGame.getBlack());
                statement.setString(5, serializeGame(newGame.getGame()));

                statement.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException("Error: " + e.getMessage());
            }
            return true;
        }
        else if (playerColor.equals("BLACK") && (newGame.getBlack() == null)){
            newGame.setBlack(username);
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement statement = conn.prepareStatement("DELETE FROM games WHERE gameID = ?")) {
                statement.setString(1, newGame.getGameID());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException("Error: " + e.getMessage());
            }
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement statement = conn.prepareStatement("INSERT INTO games (gameName, gameID, whiteUsername, blackUsername, game) Values(?,?,?,?,?) ")) {
                statement.setString(1, newGame.getName());
                statement.setString(2, newGame.getGameID());
                statement.setString(3, newGame.getWhite());
                statement.setString(4, username);
                statement.setString(5, serializeGame(newGame.getGame()));
                statement.executeUpdate();

            } catch (SQLException e) {
                throw new DataAccessException("Error: " + e.getMessage());
            }
            return true;
        }
        else {
            return false;
        }
    }
    @Override
    public ArrayList<GameData> getList() throws DataAccessException{
        ArrayList<GameData> games = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement("SELECT gameName, gameID, whiteUsername, blackUsername, game FROM games")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String gameName = resultSet.getString("gameName");
                    String gameID = resultSet.getString("gameID");
                    String whiteUsername = resultSet.getString("whiteUsername");
                    String blackUsername = resultSet.getString("blackUsername");
                    String game = resultSet.getString("game");
                    games.add(new GameData(gameName, gameID, whiteUsername, blackUsername, deserializeGame(game)));
                }
                return games;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: " + e.getMessage());
        }
    }
    @Override
    public void clearAllGames() throws DataAccessException{
        count = 1;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement("TRUNCATE TABLE games")) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: " + e.getMessage());
        }
    }
    private String serializeGame(ChessGame game) {
        return gson.toJson(game);
    }
    private ChessGame deserializeGame(String json) {
        return gson.fromJson(json, ChessGame.class);
    }

    @Override
    public void updateGame(String gameID, GameData updatedGameData) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement("UPDATE games SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? WHERE gameID = ?")) {
            statement.setString(1, updatedGameData.getWhite());
            statement.setString(2, updatedGameData.getBlack());
            statement.setString(3, updatedGameData.getName());
            statement.setString(4, serializeGame(updatedGameData.getGame()));
            statement.setString(5, gameID);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error while updating game: " + e.getMessage());
        }
    }
}
