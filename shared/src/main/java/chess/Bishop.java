package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Bishop implements PieceMovesCalculator{
    //added notes

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position){

        var validMoves = new ArrayList<ChessMove>();
        int currRow = position.getRow();
        int currCol = position.getColumn();

        boolean upRight = true;
        boolean upLeft = true;
        boolean downRight = true;
        boolean downLeft = true;

        for (int i = 1; i <= 8; i++) {
            if (currRow + i <= 8 && currCol + i <= 8){
                var nextPosition = new ChessPosition(currRow+i, currCol+i);

                upRight = HelperFunction(validMoves, board, position, nextPosition, upRight);
            }
            if (currRow + i <= 8 && currCol - i >= 1){
                var nextPosition = new ChessPosition(currRow+i, currCol-i);
                upLeft = HelperFunction(validMoves, board, position, nextPosition, upLeft);
            }
            if (currRow - i >= 1 && currCol + i <= 8){
                var nextPosition = new ChessPosition(currRow-i, currCol+i);
                downRight = HelperFunction(validMoves, board, position, nextPosition, downRight);
            }
            if (currRow - i >= 1 && currCol - i >= 1){
                var nextPosition = new ChessPosition(currRow-i, currCol-i);
                downLeft = HelperFunction(validMoves, board, position, nextPosition, downLeft);
            }
        }
        return validMoves;
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