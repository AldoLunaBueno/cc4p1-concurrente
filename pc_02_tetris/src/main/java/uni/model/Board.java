package uni.model;

import java.io.Serializable;

public class Board implements Serializable {
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

    public int clearLines() {
        int completeLines = 0;
        boolean isComplete = true;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (matrix[i][j] == 0) {
                    isComplete = false;
                    break;
                }
            }
            if (isComplete) {
                clearOneLine(i);
                completeLines++;
            }
            isComplete = true;
        }

        return completeLines;
    }

    private void clearOneLine(int cleared) {
        for (int i = cleared; i > 0; i--) {
            matrix[i] = matrix[i-1];
        }
        matrix[0] = new int[columns];

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
