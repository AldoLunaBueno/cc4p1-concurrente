package uni;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uni.model.Board;

public class BoardTest {
    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board(20, 10); // filas: 0-19, columnas: 0-9
    }

    @Test
    void testEmptyBoard() {
        assertEquals(board.getState(8, 7), 0); // el estado vacío es 0
        assertEquals(board.getState(9, 19), 0);
    }

    @Test
    void testBoardBounds() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            board.getState(0, -1);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            board.getState(10, 0);
        });
    }

    @Test
    void testLockBlock() {
        int player = 1;
        board.lockBlock(5, 19, player);
        assertEquals(board.getState(5, 19), player);
        assertEquals(board.getState(4, 19), 0);
        assertEquals(board.getState(6, 19), 0);
    }
}
