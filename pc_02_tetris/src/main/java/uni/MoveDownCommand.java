package uni;

public class MoveDownCommand implements Command {
    @Override
    public void execute(Piece piece, Board board, CollisionEngine engine) {
        if (engine.isValidMove(piece, board, 0, 1)) {
            piece.moveDown();
        }
    }
}
