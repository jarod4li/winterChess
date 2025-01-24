package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Pawn implements PieceMovesCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {

        // Create variable of possible moves
        var validMoves = new ArrayList<ChessMove>();
        int currRow = position.getRow();  // rows and columns are 1 through 8 while board is 0 from 7
        int currCol = position.getColumn();

        if (board.getPiece(position).getTeamColor() == ChessGame.TeamColor.WHITE) {
            if (currRow + 1 <= 8) {
                var nextPosition = new ChessPosition(currRow + 1, currCol);
                HelperFunctionWhite(validMoves, board, position, nextPosition);
            }
        } else if (board.getPiece(position).getTeamColor() == ChessGame.TeamColor.BLACK) {
            if (currRow - 1 >= 1) {
                var nextPosition = new ChessPosition(currRow - 1, currCol);
                HelperFunctionBlack(validMoves, board, position, nextPosition);
            }
        }

        return validMoves;
    }

    public void HelperFunctionWhite(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition position, ChessPosition nextPosition) {
        var attackPositionRight = new ChessPosition(position.getRow() + 1, position.getColumn() + 1);
        var attackPositionLeft = new ChessPosition(position.getRow() + 1, position.getColumn() - 1);
        if (position.getRow() == 2) {
            var doubleStep = new ChessPosition(position.getRow() + 2, position.getColumn());
            if (board.getPiece(nextPosition) == null && board.getPiece(doubleStep) == null) {
                validMoves.add(new ChessMove(position, doubleStep));
            }
        }
        if (board.getPiece(nextPosition) == null) {
            if (nextPosition.getRow() == 8){
                validMoves.add(new ChessMove(position, nextPosition, ChessPiece.PieceType.ROOK));
                validMoves.add(new ChessMove(position, nextPosition, ChessPiece.PieceType.KNIGHT));
                validMoves.add(new ChessMove(position, nextPosition, ChessPiece.PieceType.BISHOP));
                validMoves.add(new ChessMove(position, nextPosition, ChessPiece.PieceType.QUEEN));
            } else validMoves.add(new ChessMove(position, nextPosition));
        }
        if (attackPositionRight.getColumn() != 9 && board.getPiece(attackPositionRight) != null && board.getPiece(attackPositionRight).getTeamColor() != board.getPiece(position).getTeamColor()) {
            if (attackPositionRight.getRow() == 8){
                validMoves.add(new ChessMove(position, attackPositionRight, ChessPiece.PieceType.ROOK));
                validMoves.add(new ChessMove(position, attackPositionRight, ChessPiece.PieceType.KNIGHT));
                validMoves.add(new ChessMove(position, attackPositionRight, ChessPiece.PieceType.BISHOP));
                validMoves.add(new ChessMove(position, attackPositionRight, ChessPiece.PieceType.QUEEN));
            } else validMoves.add(new ChessMove(position, attackPositionRight));

        }
        if (attackPositionLeft.getColumn() != 0 && board.getPiece(attackPositionLeft) != null && board.getPiece(attackPositionLeft).getTeamColor() != board.getPiece(position).getTeamColor()) {
            if (attackPositionLeft.getRow() == 8){
                validMoves.add(new ChessMove(position, attackPositionLeft, ChessPiece.PieceType.ROOK));
                validMoves.add(new ChessMove(position, attackPositionLeft, ChessPiece.PieceType.KNIGHT));
                validMoves.add(new ChessMove(position, attackPositionLeft, ChessPiece.PieceType.BISHOP));
                validMoves.add(new ChessMove(position, attackPositionLeft, ChessPiece.PieceType.QUEEN));
            } else validMoves.add(new ChessMove(position, attackPositionLeft));
        }
    }

    public void HelperFunctionBlack(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition position, ChessPosition nextPosition) {
        var attackPositionRight = new ChessPosition(position.getRow() - 1, position.getColumn() + 1);
        var attackPositionLeft = new ChessPosition(position.getRow() - 1, position.getColumn() - 1);
        if (position.getRow() == 7) {
            var doubleStep = new ChessPosition(position.getRow() - 2, position.getColumn());
            if (board.getPiece(nextPosition) == null && board.getPiece(doubleStep) == null) {
                validMoves.add(new ChessMove(position, doubleStep));
            }
        }
        if (board.getPiece(nextPosition) == null) {
            if (nextPosition.getRow() == 1) {
                validMoves.add(new ChessMove(position, nextPosition, ChessPiece.PieceType.ROOK));
                validMoves.add(new ChessMove(position, nextPosition, ChessPiece.PieceType.KNIGHT));
                validMoves.add(new ChessMove(position, nextPosition, ChessPiece.PieceType.BISHOP));
                validMoves.add(new ChessMove(position, nextPosition, ChessPiece.PieceType.QUEEN));
            } else validMoves.add(new ChessMove(position, nextPosition));
        }
        if (attackPositionRight.getColumn() != 9 && board.getPiece(attackPositionRight) != null && board.getPiece(attackPositionRight).getTeamColor() != board.getPiece(position).getTeamColor()) {
            if (attackPositionRight.getRow() == 1) {
                validMoves.add(new ChessMove(position, attackPositionRight, ChessPiece.PieceType.ROOK));
                validMoves.add(new ChessMove(position, attackPositionRight, ChessPiece.PieceType.KNIGHT));
                validMoves.add(new ChessMove(position, attackPositionRight, ChessPiece.PieceType.BISHOP));
                validMoves.add(new ChessMove(position, attackPositionRight, ChessPiece.PieceType.QUEEN));
            } else validMoves.add(new ChessMove(position, attackPositionRight));

        }
        if (attackPositionLeft.getColumn() != 0 && board.getPiece(attackPositionLeft) != null && board.getPiece(attackPositionLeft).getTeamColor() != board.getPiece(position).getTeamColor()) {
            if (attackPositionLeft.getRow() == 1) {
                validMoves.add(new ChessMove(position, attackPositionLeft, ChessPiece.PieceType.ROOK));
                validMoves.add(new ChessMove(position, attackPositionLeft, ChessPiece.PieceType.KNIGHT));
                validMoves.add(new ChessMove(position, attackPositionLeft, ChessPiece.PieceType.BISHOP));
                validMoves.add(new ChessMove(position, attackPositionLeft, ChessPiece.PieceType.QUEEN));
            } else validMoves.add(new ChessMove(position, attackPositionLeft));
        }
    }
}