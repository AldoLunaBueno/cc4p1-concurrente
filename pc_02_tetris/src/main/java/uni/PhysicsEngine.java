package uni;

public class PhysicsEngine {
    public static boolean isValidMove(ActivePiece piece, Board board, int deltaX, int deltaY) {
        // vale para movimientos hacia los lados, 
        // pero también hacia abajo o incluso hacia arriba
        
        // bordes del tablero
        if (piece.getX() + deltaX < 0 
                || piece.getX() + deltaX + piece.columns() > board.columns() 
                || piece.getY() + deltaY < 0
                || piece.getY() + deltaY + piece.rows() > board.rows()) {
            return false;
        }

        // bordes del stack ()
        for (int pieceRow = 0; pieceRow < piece.columns(); pieceRow++) {
            for (int pieceColumn = 0; pieceColumn < piece.rows(); pieceColumn++) {
                if (piece.getCurrentShape()[pieceRow][pieceColumn] == 0) {
                    continue;
                }
                int xFuture = piece.getX() + deltaX + pieceColumn;
                int yFuture = piece.getY() + deltaY + pieceRow;
                if (board.getMatrix()[yFuture][xFuture] != 0) {
                    return false;
                }
            }
        }
        return true;
    }
}
