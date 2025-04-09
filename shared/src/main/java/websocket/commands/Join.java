package websocket.commands;

import chess.ChessGame;

public class Join extends UserGameCommand{
    private String gameID;
    private ChessGame.TeamColor playerColor;

    public Join(String token, String gameID, ChessGame.TeamColor teamColor){
        super(token);
        this.gameID = gameID;
        this.playerColor = teamColor;
    }
    public String getGameID(){return gameID;}
    public ChessGame.TeamColor getTeamColor(){return playerColor;}

}
