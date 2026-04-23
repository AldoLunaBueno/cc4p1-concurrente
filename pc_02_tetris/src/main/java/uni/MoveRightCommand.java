package uni;

public class MoveRightCommand implements Command {
    @Override
    public void execute(Piece piece, Board board, CollisionEngine engine) {
        if (engine.isValidMove(piece, board, 1, 0)) {
            piece.moveRight();
        }
    }
}