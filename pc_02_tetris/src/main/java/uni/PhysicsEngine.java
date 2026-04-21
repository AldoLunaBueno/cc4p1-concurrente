package uni;

public class PhysicsEngine {
    public static boolean isValidMove(ActivePiece piece, Board board, int deltaX, int deltaY) {
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
}
