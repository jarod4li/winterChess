package websocket.commands;

public class Resign extends UserGameCommand{
    private String gameID;
    public Resign(String token, String gameID){
        super(token);
        this.gameID = gameID;
    }
    public String getGameID(){
        return gameID;
    }
    public void setGameID(String gameID){
        this.gameID = gameID;
    }
}
