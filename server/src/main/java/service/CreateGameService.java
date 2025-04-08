package service;
import dataaccess.*;
import model.GameData;
import requestandresponse.CreateGameRequest;
import requestandresponse.CreateGameResponse;
public class CreateGameService {
    private GameDAO gameDA = new GameDAO();
    private AuthDAO auth = new AuthDAO();
    public CreateGameResponse gameCreator(CreateGameRequest request, String authToken) {
        try {
            if(request.getName() == null){
                return new CreateGameResponse("Error! No name");
            }
            if(auth.getTokens().isEmpty()) {
                return new CreateGameResponse("Error! Need authorization");
            }
            GameData game = new GameData();
            game.setGameName(request.getName());
            int id = gameDA.createGame(request.getName());
            game.setID(id);
            return new CreateGameResponse(id);
        }
        catch (DataAccessException exception) {
            return new CreateGameResponse(exception.getMessage());
        }
    }
}