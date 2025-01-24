package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Knight implements PieceMovesCalculator{
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position){
        var validMoves = new ArrayList<ChessMove>();
        int currRow = position.getRow();
        int currCol = position.getColumn();

        checkAndAddMove(validMoves, board, position, currRow + 2, currCol + 1);
        checkAndAddMove(validMoves, board, position, currRow + 1, currCol + 2);
        checkAndAddMove(validMoves, board, position, currRow - 1, currCol + 2);
        checkAndAddMove(validMoves, board, position, currRow - 2, currCol + 1);
        checkAndAddMove(validMoves, board, position, currRow - 2, currCol - 1);
        checkAndAddMove(validMoves, board, position, currRow - 1, currCol - 2);
        checkAndAddMove(validMoves, board, position, currRow + 1, currCol - 2);
        checkAndAddMove(validMoves, board, position, currRow + 2, currCol - 1);

        return validMoves;

    }

    void checkAndAddMove(ArrayList<ChessMove> moves, ChessBoard board, ChessPosition position, int row, int col) {
        if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
            var nextPosition = new ChessPosition(row, col);
            HelperFunction(moves, board, position, nextPosition);
        }
    }
    public void HelperFunction(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition position, ChessPosition nextPosition) {
        if ((board.getPiece(nextPosition) == null)) {
            validMoves.add(new ChessMove(position, nextPosition));
        } else if ((board.getPiece(nextPosition).getTeamColor() != board.getPiece(position).getTeamColor())) {
            validMoves.add(new ChessMove(position, nextPosition));
        }
    }
}