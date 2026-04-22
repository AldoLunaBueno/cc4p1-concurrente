package uni;

public class Board {
    private final int[][] matrix; // bloques estáticos (ya cayeron)
    private int rows;
    private int columns;

    public Board(int rows, int columns) {
        matrix = new int[rows][columns];
        this.rows = rows;
        this.columns = columns;
    }

    public Board(int[][] matrix) {
        this.matrix = matrix;
        this.rows = matrix.length;
        this.columns = matrix[0].length;
    }

    public int getState(int x, int y) {
        return matrix[y][x];
    }

    public void lockBlock(int x, int y, int player) {
        matrix[y][x] = player;
    }

    public int rows() {
        return rows;
    }

    public int columns() {
        return columns;
    }

    public int[][] getMatrix() {
        return matrix;
    }
}
