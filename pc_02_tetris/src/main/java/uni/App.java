package uni;

import static uni.model.Shape.PIECE_B;

import uni.controller.FixedStepGameLoop;
import uni.controller.GameController;
import uni.controller.GameLoop;
import uni.engine.CollisionEngine;
import uni.engine.OnePieceGenerator;
import uni.engine.PieceGenerator;
import uni.model.Board;

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
