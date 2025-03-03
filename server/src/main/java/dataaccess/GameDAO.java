package dataaccess;

import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
    public void addGame(GameData newGame) throws DataAccessException;
    public String getGameID(String gameName) throws DataAccessException;
    public Boolean getGameName(String gameName) throws DataAccessException;
    public GameData getGame(String gameID) throws DataAccessException;
    public Boolean setGame(GameData currGame, String playerColor, String username) throws DataAccessException;
    public ArrayList<GameData> getList() throws DataAccessException;
    public void clearAllGames() throws DataAccessException;
    public void updateGame(String gameID, GameData updatedGame) throws DataAccessException;
}
