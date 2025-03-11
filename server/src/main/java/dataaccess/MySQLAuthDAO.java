package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLAuthDAO implements AuthDAO{
    public MySQLAuthDAO() throws DataAccessException {
        configureDatabase();
    }
    private static final String[] AUTH_TABLE_QUERY = {
            "CREATE TABLE IF NOT EXISTS authtokens (" +
                    "token VARCHAR(255) NOT NULL, " +
                    "username VARCHAR(50) NOT NULL, " +
                    "PRIMARY KEY (token)" +
                    ")"
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();

        try (Connection conn = DatabaseManager.getConnection()) {
            for (String query : AUTH_TABLE_QUERY) {
                try (PreparedStatement statement = conn.prepareStatement(query)) {
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: " + e.getMessage());
        }
    }
    @Override
    public AuthData addAuthToken(String username) throws DataAccessException{
        AuthData newUser = new AuthData(username);
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement("INSERT INTO authtokens (username, token) VALUES (?, ?)")) {
            statement.setString(1, newUser.getUsername());
            statement.setString(2, newUser.getToken());
            statement.executeUpdate();
            return newUser;
        } catch (SQLException e) {
            throw new DataAccessException("Error: " + e.getMessage());
        }

    }
    @Override
    public AuthData findToken(String token) throws DataAccessException{
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement("SELECT * FROM authtokens WHERE token = ?")) {
            statement.setString(1, token);
            try (ResultSet resultSet = statement.executeQuery()) {
                if(resultSet.next()) {
                    String username = resultSet.getString("username");
                    return new AuthData(username, token);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: " + e.getMessage());
        }
    }
    @Override
    public void removeAuthToken(AuthData token) throws DataAccessException{
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement("DELETE FROM authtokens WHERE token = ?")) {
            statement.setString(1, token.getToken());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: " + e.getMessage());
        }
    }
    @Override
    public void clearAllAuth() throws DataAccessException{
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement("TRUNCATE TABLE authtokens")) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: " + e.getMessage());
        }
    }
}
