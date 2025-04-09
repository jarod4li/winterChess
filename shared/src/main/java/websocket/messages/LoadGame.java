package websocket.messages;

public class LoadGame extends ServerMessage{
    private Object game;

    public LoadGame(Object game){
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }
    public Object getGameID(){return game;}
}
