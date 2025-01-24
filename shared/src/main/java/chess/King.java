package chess;

import java.util.ArrayList;
import java.util.Collection;

public class King implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {

        var validMoves = new ArrayList<ChessMove>();
        int currRow = position.getRow();  // rows and columns are 1 through 8 while board is 0 from 7
        int currCol = position.getColumn();
        int[] directions = {-1, 0, 1};

        for (int rowOffset : directions) {
            for (int colOffset : directions) {
                if (rowOffset == 0 && colOffset == 0) {
                    continue;
                }

                int newRow = currRow + rowOffset;
                int newCol = currCol + colOffset;

                if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                    ChessPosition nextPosition = new ChessPosition(newRow, newCol);
                    HelperFunction(validMoves, board, position, nextPosition);
                }
            }
        }

        return validMoves;
    }
    public void HelperFunction(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition position, ChessPosition nextPosition) {
        if ((board.getPiece(nextPosition) == null)) {
            validMoves.add(new ChessMove(position, nextPosition));
        } else if ((board.getPiece(nextPosition).getTeamColor() != board.getPiece(position).getTeamColor())) {
            validMoves.add(new ChessMove(position, nextPosition));
        }
    }

}