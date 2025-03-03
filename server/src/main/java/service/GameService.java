package service;

import dataaccess.*;
import model.GameData;

import java.util.ArrayList;

public class GameService {
    private final GameDAO game;
    private final AuthDAO auth;

    public GameService(GameDAO game, AuthDAO auth){
        this.game = game;
        this.auth = auth;
    }
    public String registerGame(GameData currGame, String token) throws DataAccessException {

        if (auth.findToken(token) == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        if (game.getGameName(currGame.getName())){
            throw new DataAccessException("Error: bad request");
        }
        currGame.setGame();
        game.addGame(currGame);
        return game.getGameID(currGame.getName());
    }

    public void joinGame(GameData currGame, String token) throws DataAccessException {
        String gameID = currGame.getGameID();
        GameData wantedGame;
        if (game.getGame(gameID) == null) {
            throw new DataAccessException("Error: bad request");
        }
        else {
            wantedGame = game.getGame(gameID);
        }
        String playerColor = currGame.getPlayerColor();

        // Add this check for null or empty player color
        if (playerColor == null || playerColor.isEmpty()) {
            throw new DataAccessException("Error: bad request");
        }

        if (auth.findToken(token) == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        String username = auth.findToken(token).getUsername();
        var joined = game.setGame(wantedGame, playerColor, username);
        if (!joined) {
            throw new DataAccessException("Error: already taken");
        }
    }

    public ArrayList<GameData> listGames(String token) throws DataAccessException{
        if (auth.findToken(token) == null){
            throw new DataAccessException("Error: unauthorized");
        }
        return game.getList();
    }

}
