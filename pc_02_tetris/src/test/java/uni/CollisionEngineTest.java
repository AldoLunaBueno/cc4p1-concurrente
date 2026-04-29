package uni;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uni.model.Shape.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uni.engine.CollisionEngine;
import uni.model.Board;
import uni.model.Piece;

public class CollisionEngineTest {
    private CollisionEngine engine;
    private Board board;

    @BeforeEach
    void setUp() {
        engine = new CollisionEngine();
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
        Piece piece = new Piece(PIECE_O, 1, 2, 1);
        assertTrue(engine.isValidMove(piece, board, 1, 0));
        assertTrue(engine.isValidMove(piece, board, -1, 0));
        assertTrue( engine.isValidMove(piece, board, 0, 1));;
    }

    @Test
    void testIsNotValidMove() {
        Piece piece = new Piece(PIECE_O, 1, 2, 3);
        assertFalse(engine.isValidMove(piece, board, 1, 0));;
        assertFalse(engine.isValidMove(piece, board, -1, 0));
        assertFalse(engine.isValidMove(piece, board, 0, 1));
    }

    @Test
    void testBoardBoundaryCollisions() {
        Piece piece1 = new Piece(PIECE_T, 1, 0, 0);
        piece1.rotate();
        assertTrue(engine.isValidMove(piece1, board, -1, 0));

        Piece piece2 = new Piece(PIECE_T, 1, 3, 0);
        piece2.rotate();
        piece2.rotate();
        piece2.rotate();
        assertTrue(engine.isValidMove(piece2, board, 1, 0));
    }
}
