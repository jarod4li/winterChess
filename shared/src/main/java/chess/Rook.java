package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Rook implements PieceMovesCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        var validMoves = new ArrayList<ChessMove>();
        int currRow = position.getRow();
        int currCol = position.getColumn();

        checkAndAddMoves(validMoves, board, position, 1, 0, 8); // up
        checkAndAddMoves(validMoves, board, position, 0, -1, 8); // left
        checkAndAddMoves(validMoves, board, position, -1, 0, 8); // down
        checkAndAddMoves(validMoves, board, position, 0, 1, 8); // right

        return validMoves;
    }

    void checkAndAddMoves(ArrayList<ChessMove> moves, ChessBoard board, ChessPosition position, int rowStep, int colStep, int limit) {
        for (int i = 1; i <= limit; i++) {
            int nextRow = position.getRow() + i * rowStep;
            int nextCol = position.getColumn() + i * colStep;

            if (nextRow < 1 || nextRow > 8 || nextCol < 1 || nextCol > 8) {
                break; // exit loop if out of bounds
            }

            var nextPosition = new ChessPosition(nextRow, nextCol);
            if (!HelperFunction(moves, board, position, nextPosition, true)) {
                break;
            }
        }
    }



    public boolean HelperFunction(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition position, ChessPosition nextPosition, boolean keepGoing) {
        if (keepGoing) {
            if ((board.getPiece(nextPosition) == null)) {
                validMoves.add(new ChessMove(position, nextPosition));
                return true;
            } else if ((board.getPiece(nextPosition).getTeamColor() != board.getPiece(position).getTeamColor())) {
                validMoves.add(new ChessMove(position, nextPosition));
                return false;
            }
            else return false;
        }
        return false;
    }
}