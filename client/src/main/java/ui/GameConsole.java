package ui;

import chess.*;
import com.google.gson.Gson;
import websocket.messages.*;

import java.util.Collection;
import java.util.Scanner;

public class GameConsole implements GameHandler{
    private static ChessBoard board = new ChessBoard();
    private static ChessGame game;
    private static ChessGame.TeamColor teamColor;
    private static String token;
    private static String gameID;
    private static int portNumber = 8080;
    private final WebSocketFacade ws = new WebSocketFacade("http://localhost:8080", GameConsole.this);


    private static final Scanner scanner = new Scanner(System.in);

    public GameConsole(ChessGame.TeamColor teamColor, String token, String gameID){
        GameConsole.teamColor = teamColor;
        GameConsole.token = token;
        GameConsole.gameID = gameID;

    }
    public void runGame() {
        displayMenu();

        ws.joinGame(token, gameID, teamColor);

        while(true){
            String input = scanner.nextLine().toLowerCase();

            switch (input) {
                case "help":
                    displayMenu();
                    break;
                case "redraw":
                    if(ChessGame.TeamColor.WHITE == teamColor) {
                        teamWhiteBoard(board);
                    } else if(ChessGame.TeamColor.BLACK == teamColor) {
                        teamBlackBoard(board);
                    } else {
                        teamWhiteBoard(board);
                    }
                    break;
                case "leave":
                    ws.leaveGame(token, gameID);
                    System.out.println("You out!");
                    PostLogin.postLogin(token, portNumber,PostLogin.getServerFacade());

                    break;
                case "move":
                    if (teamColor == null){
                        System.out.println("Psss... don't! Just watch!");
                    }
                    else {
                        helperMakeMove(board);
                    }
                    break;
                case "resign":
                    System.out.println("Sure? (Y/N)");
                    String resigning = scanner.nextLine().toLowerCase();
                    if(resigning.equals("y")) {
                        ws.resign(token, gameID);
                        game.setTeamTurn(null);
                        PostLogin.postLogin(token, portNumber,PostLogin.getServerFacade());
                    } else if (resigning.equals("n")){
                        System.out.println("Keep playing.");
                    }
                    break;
                case "directions":
                    possibleMoves(board);
                    break;
            }
        }

    }
    private void displayMenu(){
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLACK);
        System.out.println("Select one of the options");
        System.out.println("Help - Displays this information again in case you forgot");
        System.out.println("Redraw - Redraws chess board");
        System.out.println("Leave - Remove yourself from the game");
        System.out.println("Move - Input what move they want to make");
        System.out.println("Resign - If you want to give up");
        System.out.println("Directions - Highlight legal moves in case you need some help");
    }
    private void helperMakeMove(ChessBoard board){
        if (game.getTeamTurn() != null){

            System.out.println("Row: ");
            String row = scanner.nextLine().toLowerCase();
            int rowValue = Integer.parseInt(row);

            System.out.println("Column: ");
            String column = scanner.nextLine().toLowerCase();
            int colValue = (int) column.charAt(0) - 96;

            ChessPiece piece = board.getPiece(new ChessPosition(rowValue, colValue));

            if(colValue < 1 || colValue > 8 || rowValue < 1 || rowValue > 8) {
                System.out.println("Invalid input!");
                return;
            }
            else if(piece == null) {
                System.out.println("Square is empty");
                return;
            }

            System.out.println("New row: ");
            row = scanner.nextLine().toLowerCase();
            int newRowVal = Integer.parseInt(row);

            System.out.println("New column: ");
            column = scanner.nextLine().toLowerCase();
            int newColVal = (int) column.charAt(0) - 96;
            if(newColVal < 1 || newColVal > 8 || newRowVal < 1 || newRowVal > 8) {
                System.out.println("Invalid input!");
                return;
            }
            ChessMove move = new ChessMove(new ChessPosition(rowValue, colValue), new ChessPosition(newRowVal, newColVal), null);
            Collection<ChessMove> validMoves = game.validMoves(new ChessPosition(rowValue, colValue));
            if(!validMoves.contains(move)) {
                System.out.println("Invalid move!");
            }
            else if(piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                if(piece.getTeamColor() == ChessGame.TeamColor.WHITE && newRowVal == 8 || piece.getTeamColor() == ChessGame.TeamColor.BLACK && newRowVal == 1) {
                    System.out.println("Here are your options: Queen, bishop, knight, rook");
                    String newPiece = scanner.nextLine().toLowerCase();
                    switch (newPiece) {
                        case "queen" ->
                                move = new ChessMove(new ChessPosition(rowValue, colValue), new ChessPosition(newRowVal, newColVal), ChessPiece.PieceType.QUEEN);
                        case "bishop" ->
                                move = new ChessMove(new ChessPosition(rowValue, colValue), new ChessPosition(newRowVal, newColVal), ChessPiece.PieceType.BISHOP);
                        case "knight" ->
                                move = new ChessMove(new ChessPosition(rowValue, colValue), new ChessPosition(newRowVal, newColVal), ChessPiece.PieceType.KNIGHT);
                        case "rook" ->
                                move = new ChessMove(new ChessPosition(rowValue, colValue), new ChessPosition(newRowVal, newColVal), ChessPiece.PieceType.ROOK);
                        default -> System.out.println("Not Valid.");
                    }
                }
            }
            ws.makeMove(token, gameID, move);
        }
        else {
            System.out.println("Game already ended!");
        }
    }

    private void possibleMoves(ChessBoard board){
        System.out.println("Row: ");
        String row = scanner.nextLine().toLowerCase();
        int rowValue = Integer.parseInt(row);

        System.out.println("Column: ");
        String column = scanner.nextLine().toLowerCase();
        int colValue = (int) column.charAt(0) - 96;

        if(colValue < 1 || colValue > 8 || rowValue < 1 || rowValue > 8) {
            System.out.println("Invalid input!");
            return;
        }
        ChessPosition currPosition = new ChessPosition(rowValue, colValue);
        ChessPiece piece = board.getPiece(currPosition);
        if (piece == null){
            System.out.println("There is nobody there (oO)");
        }
        Collection<ChessMove> validMoves = game.validMoves(currPosition);
        if (teamColor == ChessGame.TeamColor.BLACK){
            System.out.println(EscapeSequences.SET_TEXT_COLOR_BLACK);
            System.out.println("   h  g  f  e  d  c  b  a ");
            for (int i = 1; i <= 8; i++){
                System.out.print((i) + " ");

                for (int j = 8; j >= 1; j--) {
                    ChessPosition nextPosition = new ChessPosition(i, j);
                    ChessPiece nextPiece = board.getPiece(nextPosition);

                    boolean isLegal = false;
                    boolean highlighted = false;
                    for (ChessMove move : validMoves) {
                        if (move.getEndPosition().equals(nextPosition)) {
                            isLegal = true;
                            break;
                        } if(move.getStartPosition().equals(nextPosition)) {
                            highlighted = true;
                            break;
                        }
                    }
                    if (isLegal) {
                        System.out.print(EscapeSequences.SET_BG_COLOR_YELLOW);
                    }else if (highlighted) {
                        System.out.print(EscapeSequences.SET_BG_COLOR_GREEN);
                    } else if ((i + j) % 2 == 0) {
                        System.out.print(EscapeSequences.SET_BG_COLOR_BLACK);
                    } else {
                        System.out.print(EscapeSequences.SET_BG_COLOR_WHITE);
                    }
                    System.out.print(helperFunctionPieceColor(nextPiece) + pieceType(nextPiece));
                }

                System.out.print(EscapeSequences.RESET_BG_COLOR);
                System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK + " " + (i));
                System.out.println(EscapeSequences.SET_TEXT_COLOR_BLACK);
            }
            System.out.println("   h  g  f  e  d  c  b  a ");
        }
        else{
            System.out.println(EscapeSequences.SET_TEXT_COLOR_BLACK);
            System.out.println("   a  b  c  d  e  f  g  h  ");

            for (int i = 8; i >= 1; i--) {
                System.out.print((i) + " ");

                for (int j = 1; j <= 8; j++) {
                    ChessPosition nextPosition = new ChessPosition(i, j);
                    ChessPiece nextPiece = board.getPiece(nextPosition);

                    boolean isLegal = false;
                    boolean highlighted = false;
                    for (ChessMove move : validMoves) {
                        if (move.getEndPosition().equals(nextPosition)) {
                            isLegal = true;
                            break;
                        } if(move.getStartPosition().equals(nextPosition)) {
                            highlighted = true;
                            break;
                        }
                    }
                    if (isLegal) {
                        System.out.print(EscapeSequences.SET_BG_COLOR_YELLOW);
                    }else if (highlighted) {
                        System.out.print(EscapeSequences.SET_BG_COLOR_GREEN);
                    } else if ((i + j) % 2 == 0) {
                        System.out.print(EscapeSequences.SET_BG_COLOR_BLACK);
                    } else {
                        System.out.print(EscapeSequences.SET_BG_COLOR_WHITE);
                    }
                    System.out.print(helperFunctionPieceColor(nextPiece) + pieceType(nextPiece));
                }

                System.out.print(EscapeSequences.RESET_BG_COLOR);
                System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK + " " + (i));
                System.out.println(EscapeSequences.SET_TEXT_COLOR_BLACK);

            }
            System.out.println("   a  b  c  d  e  f  g  h  ");

        }
    }
    public static void teamWhiteBoard(ChessBoard board) {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLACK);
        System.out.println("   a  b  c  d  e  f  g  h  ");

        for (int i = 8; i >= 1; i--) {
            System.out.print((i) + " ");

            for (int j = 1; j <= 8; j++) {
                ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                if ((i + j ) % 2 == 0) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_BLACK + helperFunctionPieceColor(piece) + pieceType(piece));
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_WHITE + helperFunctionPieceColor(piece) + pieceType(piece));
                }

            }
            System.out.print(EscapeSequences.RESET_BG_COLOR);
            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK + " " + (i));
            System.out.println(EscapeSequences.SET_TEXT_COLOR_BLACK);

        }
        System.out.println("   a  b  c  d  e  f  g  h  ");
    }
    public static void teamBlackBoard(ChessBoard board){
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLACK);
        System.out.println("   h  g  f  e  d  c  b  a ");
        for (int i = 1; i <= 8; i++){
            System.out.print((i) + " ");

            for (int j = 8; j >= 1; j--){
                ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                if ((i+j)%2 == 0){
                    System.out.print(EscapeSequences.SET_BG_COLOR_BLACK + helperFunctionPieceColor(piece) + pieceType(piece));
                }
                else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_WHITE + helperFunctionPieceColor(piece) + pieceType(piece));
                }

            }
            System.out.print(EscapeSequences.RESET_BG_COLOR);
            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLACK + " " + (i));
            System.out.println(EscapeSequences.SET_TEXT_COLOR_BLACK);
        }
        System.out.println("   h  g  f  e  d  c  b  a ");

    }


    private static String helperFunctionPieceColor(ChessPiece piece){
        if (piece == null){
            return "";
        }
        else if (piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            return EscapeSequences.SET_TEXT_COLOR_RED;
        }
        else{
            return EscapeSequences.SET_TEXT_COLOR_BLUE;
        }
    }
    private static String pieceType(ChessPiece piece){
        if (piece == null){
            return "   ";
        }
        String pieceLetter = switch (piece.getPieceType()) {
            case KING -> "K";
            case QUEEN -> "Q";
            case BISHOP -> "B";
            case KNIGHT -> "N";
            case ROOK -> "R";
            case PAWN -> "P";
        };
        return " " + pieceLetter + " ";
    }

    @Override
    public void updateGame(LoadGame game) {
        Object updatedGame = game.getGameID();
        ChessGame chessGame = new Gson().fromJson(updatedGame.toString(), ChessGame.class);
        System.out.println("GAME HAS BEEN RECEIVED");
        GameConsole.game = chessGame;

        if(GameConsole.game.isInCheckmate(ChessGame.TeamColor.WHITE) || GameConsole.game.isInCheckmate(ChessGame.TeamColor.BLACK)
                || GameConsole.game.isInStalemate(ChessGame.TeamColor.WHITE) || GameConsole.game.isInStalemate(ChessGame.TeamColor.BLACK)) {
            GameConsole.game.setTeamTurn(null);
        }
        GameConsole.board = chessGame.getBoard();
        if(teamColor == ChessGame.TeamColor.BLACK) {
            teamBlackBoard(board);
        } else {
            teamWhiteBoard(board);
        }
    }

    @Override
    public void printNotification(Notification message) {
        System.out.println(message.getMessage());
    }
}
