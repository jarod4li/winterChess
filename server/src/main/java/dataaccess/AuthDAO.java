package dataaccess;

import java.util.*;
import model.AuthData;
import java.sql.*;
import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class AuthDAO {
    private static HashMap<String, AuthData> authMap = new HashMap<>();
    private DatabaseManager dbMan = new DatabaseManager();

    public AuthData returnToken(String authToken) throws DataAccessException {
        Connection conn = dbMan.getConnection();
        try (var prepStatement = conn.prepareStatement("SELECT * FROM authtoken WHERE authToken=?")) {
            prepStatement.setString(1, authToken);
            try (var resSet = prepStatement.executeQuery()) {
                if (resSet.next()) {
                    String retrievedToken = resSet.getString("authToken");
                    String username = resSet.getString("username");
                    return new AuthData(retrievedToken, username);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        } finally {
            dbMan.closeConnection(conn);
        }
    }
    public void createToken(String authToken, String username) throws DataAccessException {
        Connection connection = dbMan.getConnection();
        try (var prepStatement = connection.prepareStatement("INSERT INTO authtoken (authToken, username) VALUES(?, ?)", RETURN_GENERATED_KEYS)) {
            prepStatement.setString(1, authToken);
            prepStatement.setString(2, username);
            prepStatement.executeUpdate();
        } catch (SQLException exception) {
            throw new DataAccessException(exception.getMessage());
        } finally {
            dbMan.closeConnection(connection);
        }
    }

    public HashMap<String, AuthData> getTokens() {
        return authMap;
    }
    public void clear(Connection conn) throws DataAccessException {
        try (var prepStatement = conn.prepareStatement("DELETE FROM authtoken")) {
            prepStatement.executeUpdate();
        } catch (SQLException exception) {
            throw new DataAccessException("Error");
        }
    }
    public void delete(String authToken) throws DataAccessException {
        Connection connection = dbMan.getConnection();
        try (var prepStatement = connection.prepareStatement("DELETE FROM authtoken WHERE authToken=?")) {
            prepStatement.setString(1, authToken);
            prepStatement.executeUpdate();
        } catch (SQLException exception) {
            throw new DataAccessException(exception.getMessage());
        } finally {
            dbMan.closeConnection(connection);
        }
    }
}
