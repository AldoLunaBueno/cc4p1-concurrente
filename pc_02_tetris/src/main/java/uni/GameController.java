package uni;

import java.util.ArrayList;

public class GameController {
    private Board board;
    private PieceGenerator generator;
    private CollisionEngine engine;
    private ArrayList<Piece> pieces;
    private int gameTick;

    public GameController(Board board, PieceGenerator generator, CollisionEngine engine, KeyInput in, GameRenderer renderer) {
        this.board = board;
        this.generator = generator;
        this.engine = engine;
        this.pieces = new ArrayList<>();
        this.gameTick = 0;
    }

    public boolean update() {
        // Spawnea nueva pieza
        if (pieces.isEmpty()) {
            Piece piece = generator.createPiece();
            if (engine.isValidMove(piece, board, 0, 0)) {
                pieces.add(piece);
                gameTick++;
                return true;
            } else {
                gameTick++;
                return false; // ya termińo el juego
            }
        }

        // Gravedad
        for (int i = pieces.size() - 1; i >= 0; i--) {
            Piece piece = pieces.get(i);
            if (engine.isValidMove(piece, board, 0, 1)) {
                piece.moveDown();
            } else {
                lockPiece(piece, board); // se ancla si no puede caer más
            }
        }

        gameTick++;
        return true;
    }

    private void lockPiece(Piece piece, Board board) {
        int player = piece.getPlayer();
        for (int i = 0; i < piece.rows(); i++) {
            for (int j = 0; j < piece.columns(); j++) {
                int x = piece.getX() + j;
                int y = piece.getY() + i;
                board.lockBlock(x, y, player);
            }
        }
        pieces.remove(piece);
    }

    public int getTick() {
        return gameTick;
    }
}
