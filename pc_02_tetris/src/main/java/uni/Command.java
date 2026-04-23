package uni;

public interface Command {
    void execute(Piece piece, Board board, CollisionEngine engine);
}