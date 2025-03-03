package dataaccess;

import model.GameData;

import java.util.ArrayList;

public class GameDataDAO implements GameDAO{
    private ArrayList<GameData> gamesList = new ArrayList<>();
    private int gameCount = 1;

    @Override
    public void addGame(GameData newGame) throws DataAccessException {
        String gameID = String.valueOf(gameCount);
        newGame.setGameID(gameID);
        newGame.setGame();
        gameCount += 1;

        gamesList.add(newGame);
    }

    @Override
    public String getGameID(String gameName) throws DataAccessException {
        for (GameData game: gamesList) {
            if (game.getName().equals(gameName)){
                return game.getGameID();
            }
        }
        return null;
    }

    @Override
    public Boolean getGameName(String gameName) throws DataAccessException {
        for (GameData game:gamesList) {
            if (game.getName().equals(gameName)){
                return true;
            }
        }
        return false;
    }

    @Override
    public GameData getGame(String gameID) throws DataAccessException {
        for (GameData game:gamesList){
            if (game.getGameID().equals(gameID)){
                return game;
            }
        }
        return null;
    }

    @Override
    public Boolean setGame(GameData currGame, String playerColor, String username) throws DataAccessException {
        GameData game = getGame(currGame.getGameID());
        if (game == null){
            return false;
        }

        if (playerColor==null){
            return true;
        }
        else if (playerColor.equals("WHITE") && (game.getWhite() == null)) {
            game.setWhite(username);
            return true;
        } else if (playerColor.equals("BLACK") && (game.getBlack() == null)) {
            game.setBlack(username);
            return true;
        }


        return false;
    }

    @Override
    public ArrayList<GameData> getList() throws DataAccessException {
        return gamesList;
    }

    @Override
    public void clearAllGames() throws DataAccessException {
        gamesList.clear();
    }

    @Override
    public void updateGame(String gameID, GameData updatedGame) throws DataAccessException {
        for (GameData game:gamesList){
            if (game.getGameID().equals(gameID)){
                gamesList.remove(game);
                gamesList.add(updatedGame);
            }
        }

    }
}
