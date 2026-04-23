package uni;

public class MoveLeftCommand implements Command {
    @Override
    public void execute(Piece piece, Board board, CollisionEngine engine) {
        if (engine.isValidMove(piece, board, -1, 0)) {
            piece.moveLeft();
        }
    }
}