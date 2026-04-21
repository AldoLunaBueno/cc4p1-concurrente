package uni;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PhysicsEngineTest {
    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board(new int[][]{
        //   0  1  2  3  4  5
            {0, 0, 0, 0, 0, 0}, // 0
            {0, 0, 0, 0, 0, 0}, // 1
            {0, 0, 0, 0, 0, 0}, // 2
            {0, 0, 0, 0, 1, 1}, // 3
            {0, 1, 0, 0, 0, 1}, // 4
            {1, 1, 1, 1, 0, 1}  // 5
        });        
    }

    @Test
    void testIsValidMove() {
        ActivePiece piece = new ActivePiece(PieceType.O_PIECE, 1, 2, 1);
        assertTrue(PhysicsEngine.isValidMove(piece, board, 1, 0));
        assertTrue(PhysicsEngine.isValidMove(piece, board, -1, 0));
        assertTrue( PhysicsEngine.isValidMove(piece, board, 0, 1));;
    }

    @Test
    void testIsNotValidMove() {
        ActivePiece piece = new ActivePiece(PieceType.O_PIECE, 1, 2, 3);
        assertFalse(PhysicsEngine.isValidMove(piece, board, 1, 0));;
        assertFalse(PhysicsEngine.isValidMove(piece, board, -1, 0));
        assertFalse(PhysicsEngine.isValidMove(piece, board, 0, 1));
    }

    @Test
    void testBoardBoundaryCollisions() {
        ActivePiece piece1 = new ActivePiece(PieceType.T_PIECE, 1, 0, 0);
        piece1.rotate();
        assertTrue(PhysicsEngine.isValidMove(piece1, board, -1, 0));

        ActivePiece piece2 = new ActivePiece(PieceType.T_PIECE, 1, 3, 0);
        piece2.rotate();
        piece2.rotate();
        piece2.rotate();
        assertTrue(PhysicsEngine.isValidMove(piece2, board, 1, 0));
    }
}
