package uni;

import static uni.Shape.PIECE_B;

public class App {
    public static void main(String[] args) {
        Board board = new Board(20, 10);
        CollisionEngine engine = new CollisionEngine();
        PieceGenerator generator = new OnePieceGenerator(10, PIECE_B);
        // KeyInput in = new KeyInput();
        // MinimalConsoleRenderer out = new MinimalConsoleRenderer();
        GameController controller = new GameController(board, engine, generator);
        GameLoop loop = new FixedStepGameLoop(controller);
        loop.start();
    }
}
