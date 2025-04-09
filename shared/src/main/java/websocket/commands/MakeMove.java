package websocket.commands;

import chess.ChessMove;

public class MakeMove extends UserGameCommand{
    private String gameID;
    private ChessMove move;

    public MakeMove(String token, String gameID, ChessMove move){
        super(token);
        this.gameID = gameID;
        this.move = move;
    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public ChessMove getMove() {
        return move;
    }

    public void setMove(ChessMove move) {
        this.move = move;
    }
}
