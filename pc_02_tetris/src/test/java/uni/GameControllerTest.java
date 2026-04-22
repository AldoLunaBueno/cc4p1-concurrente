package uni;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static uni.Shape.PIECE_B;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameControllerTest {
    private Board board;
    private OnePieceGenerator generator;
    private PhysicsEngine engine;
    private GameController controller;

    @BeforeEach
    void setUp() {
        board = new Board(new int[][] {
                { 0, 0, 0 },
                { 0, 0, 0 },
                { 0, 0, 0 }
        });
        generator = new OnePieceGenerator(board.columns(), PIECE_B);
        engine = new PhysicsEngine();
        controller = new GameController(board, generator, engine);
    }

    @Test
    void testUpdate() {
        controller.update();
        controller.update();
        controller.update();
        controller.update(); // anclado
        controller.update();
        controller.update();
        controller.update(); // anclado
        controller.update();
        controller.update(); // anclado
        assertFalse(controller.update());
    }
}
