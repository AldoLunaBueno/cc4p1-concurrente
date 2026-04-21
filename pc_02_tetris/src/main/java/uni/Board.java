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

    public int getState(int i, int j) {
        return matrix[i][j];
    }

    public void lockBlock(int i, int j, int player) {
        matrix[i][j] = player;
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
