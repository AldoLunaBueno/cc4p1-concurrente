package uni.engine;

import uni.model.Board;
import uni.model.Piece;

public class CollisionEngine {
    public boolean isValidMove(Piece piece, Board board, int deltaX, int deltaY) {
        // vale para movimientos hacia los lados, 
        // pero también hacia abajo o incluso hacia arriba

        // bordes del stack
        for (int pieceRow = 0; pieceRow < piece.columns(); pieceRow++) {
            for (int pieceColumn = 0; pieceColumn < piece.rows(); pieceColumn++) {
                if (piece.getCurrentShape()[pieceRow][pieceColumn] == 0) {
                    continue;
                }
                int xFuture = piece.getX() + deltaX + pieceColumn;
                int yFuture = piece.getY() + deltaY + pieceRow;
                if (xFuture < 0 || xFuture >= board.columns() || yFuture < 0 || yFuture >= board.rows()) {
                    return false;                              
                }
                if (board.getMatrix()[yFuture][xFuture] != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isValidMove(Piece piece, Board board, int rotation) {
        // vale para movimientos de rotación hacia la derecha o hacia la izquierda

        if (rotation == 1) {
            piece.rotateRight();
            if (!isValidMove(piece, board, 0, 0)) {
                return false;
            }
            piece.rotateLeft();
        } else if (rotation == -1) {
            piece.rotateLeft();
            if (!isValidMove(piece, board, 0, 0)) {
                return false;
            }
            piece.rotateRight();
        } else {
            return false;
        }
        return true;
    }
}
