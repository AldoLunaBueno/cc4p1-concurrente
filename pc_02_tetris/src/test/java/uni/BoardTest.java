package uni;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BoardTest {
    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board(20, 10); // filas: 0-19, columnas: 0-9
    }
    @Test
    void testEmptyBoard() {
        assertEquals(board.getState(7, 8), 0); // el estado vacío es 0
        assertEquals(board.getState(19, 9), 0);
    }

    @Test
    void testBoardBounds() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            board.getState(-1, 0);            
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            board.getState(0, 10);
        });
    }

    @Test
    void testLockBlock() {
        int player = 1;
        board.lockBlock(19, 5, player);
        assertEquals(board.getState(19, 5), player);
        assertEquals(board.getState(19, 4), 0);
        assertEquals(board.getState(19, 6), 0);
    }
}
