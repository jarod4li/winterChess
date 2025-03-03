package dataaccess;

import model.AuthData;

public interface AuthDAO {
    public void clearAllAuth() throws DataAccessException;
    public AuthData addAuthToken(String username) throws DataAccessException;
    public AuthData findToken(String token) throws DataAccessException;
    public void removeAuthToken(AuthData token) throws DataAccessException;
}
