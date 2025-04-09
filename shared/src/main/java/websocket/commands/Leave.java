package websocket.commands;

public class Leave extends UserGameCommand{
    private String gameID;
    public Leave(String token, String gameID){
        super(token);
        this.gameID = gameID;
        this.commandType = CommandType.LEAVE;
    }
    public String getGameID(){return gameID;}
}
