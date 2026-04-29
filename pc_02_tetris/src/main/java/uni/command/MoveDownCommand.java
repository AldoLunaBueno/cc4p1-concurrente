package uni.command;

import uni.engine.CollisionEngine;
import uni.model.Board;
import uni.model.Piece;

public class MoveDownCommand implements Command {
    @Override
    public void execute(Piece piece, Board board, CollisionEngine engine) {
        if (engine.isValidMove(piece, board, 0, 1)) {
            piece.moveDown();
        }
    }
}
