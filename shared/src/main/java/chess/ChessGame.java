package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor team;
    private ChessBoard board;
    private ChessMove lastMove;
    private  String resign;
    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        team = TeamColor.WHITE;
        lastMove = null;
        resign = null;
    }

    public String getResign() {
        return resign;
    }
    public void setResign(String resign){
        this.resign = resign;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return team;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.team = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }
    public void setLastMove(ChessMove move){this.lastMove = move;}

    public void switchTeam(){ team = (team == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;}

    public boolean currTurn(ChessMove move){ return board.getPiece(move.getStartPosition()).getTeamColor() == team;}

    public ChessPosition kingPosition(TeamColor currTeam){

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (board.getPiece(new ChessPosition(i, j)) != null && piece.getTeamColor() == team && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return position;
                }
            }
        }

        return  null;
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (board.getPiece(startPosition) == null){
            return null;
        }
        Collection<ChessMove> moves = board.getPiece(startPosition).pieceMoves(board, startPosition);
        // Create copy of the board
        ChessBoard boardCopy = new ChessBoard(board);

        // Create list to add all the valid moves
        var valid = new ArrayList<ChessMove>();

        for (ChessMove move: moves){
            var piece = board.getPiece(move.getStartPosition());
            board.addPiece(move.getEndPosition(), piece);
            board.addPiece(move.getStartPosition(), null);

            // check if that is in check
            if (!isInCheck(piece.teamColor)){
                valid.add(move);
            }
            board = new ChessBoard(boardCopy);
        }

        // eliminate moves that puts king in check
        return valid;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();

        // Check if the starting position has a piece
        ChessPiece piece = board.getPiece(start);
        if (piece == null) {
            throw new InvalidMoveException("No piece at the starting position");
        }
        var teamPlaying = board.getPiece(move.getStartPosition()).getTeamColor();
        Collection<ChessMove> moves = validMoves(move.getStartPosition());
        // Make sure the move is valid and it is the team's turn
        if (moves.contains(move)){
            if (teamPlaying.equals(getTeamTurn())){
                if (isInStalemate(teamPlaying)) {
                    throw new InvalidMoveException("This is a stalemate!");
                }

                else{
                    if (move.getPromotionPiece() == null) {
                        board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
                    }
                    else{
                        board.addPiece(move.getEndPosition(), new ChessPiece(board.getPiece(move.getStartPosition()).teamColor, move.getPromotionPiece()));
                    }
                    board.addPiece(move.getStartPosition(), null);
                    setTeamTurn(teamPlaying == TeamColor.WHITE ? TeamColor.BLACK: TeamColor.WHITE);
                }

            }
            else {
                throw new InvalidMoveException("It's not your turn!");
            }
        }
        else{
            throw new InvalidMoveException("Invalid move: " + move);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        var enemyMoves = new ArrayList<ChessMove>();
        ChessPosition kingPosition = null;
        for (int i = 1; i <= 8; i++){
            for (int j = 1; j <= 8; j++){
                if (board.getPiece(new ChessPosition(i, j)) != null) {
                    var piece = board.getPiece(new ChessPosition(i, j));
                    // Get enemy possible moves
                    if (piece.getTeamColor() != teamColor){
                        enemyMoves.addAll(piece.pieceMoves(this.board, new ChessPosition(i, j)));
                    }

                    // Identify where the king is located at the board
                    if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                        kingPosition = new ChessPosition(i, j);
                        // for each piece in the enemy's team check if there exists a move to the king's position
                        for (ChessMove move: enemyMoves){
                            if (move.getEndPosition().equals(kingPosition)){
                                return true;
                            }
                        }

                    }
                }
            }
        }

        // for each piece in the enemy's team check if there exists a move to the king's position
        for (ChessMove move: enemyMoves){
            if (move.getEndPosition().equals(kingPosition)){
                return true;
            }
        }
        // else return false
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        var friendMoves = new ArrayList<ChessMove>();
        // Check to see if team has any valid moves
        if (isInCheck(teamColor)) {
            for (int k = 1; k <= 8; k++) {
                for (int l = 1; l <= 8; l++) {
                    // Find each friend piece in the board and their possible moves
                    if (board.getPiece(new ChessPosition(k, l)) != null && board.getPiece(new ChessPosition(k, l)).getTeamColor() == teamColor) {
                        friendMoves.addAll(validMoves(new ChessPosition(k, l)));
                    }
                }
            }
            if (friendMoves.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        var friendMoves = new ArrayList<ChessMove>();
        // Check to see if team has any valid moves
        if (!isInCheck(teamColor)) {
            for (int k = 1; k <= 8; k++) {
                for (int l = 1; l <= 8; l++) {
                    // Find each friend piece in the board and their possible moves
                    if (board.getPiece(new ChessPosition(k, l)) != null && board.getPiece(new ChessPosition(k, l)).getTeamColor() == teamColor) {
                        friendMoves.addAll(validMoves(new ChessPosition(k, l)));
                    }
                }
            }
            if (friendMoves.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return team == chessGame.team && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(team, board);
    }
}
