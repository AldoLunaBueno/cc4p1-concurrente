package uni;

import java.util.List;

public class MinimalConsoleRenderer {

    public void render(Board board, List<Piece> pieces, GameState state) {
        if (state == GameState.GAMEOVER) {
            System.out.println("=== GAME OVER ===");
            return;
        }

        int rows = board.rows();
        int cols = board.columns();
        int[][] display = new int[rows][cols];

        // 1. Copiar el estado estático
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                display[y][x] = board.getState(x, y);
            }
        }

        // 2. Superponer las piezas dinámicas
        for (Piece piece : pieces) {
            int[][] shape = piece.getCurrentShape();
            for (int i = 0; i < piece.rows(); i++) {
                for (int j = 0; j < piece.columns(); j++) {
                    if (shape[i][j] != 0) {
                        int drawY = piece.getY() + i;
                        int drawX = piece.getX() + j;
                        if (drawY >= 0 && drawY < rows && drawX >= 0 && drawX < cols) {
                            display[drawY][drawX] = piece.getPlayer();
                        }
                    }
                }
            }
        }

        // 3. Double Buffering: Construir el frame completo en memoria
        // Estimamos la capacidad inicial para evitar realojamientos de memoria
        int capacity = (cols * 3 + 4) * rows + cols * 3 + 10; 
        StringBuilder frameBuffer = new StringBuilder(capacity);

        // Mover el cursor a la posición [0,0] SIN borrar la pantalla
        frameBuffer.append("\033[H");

        // Construir la matriz combinada
        for (int y = 0; y < rows; y++) {
            frameBuffer.append("|");
            for (int x = 0; x < cols; x++) {
                frameBuffer.append(display[y][x] == 0 ? " . " : "[] ");
            }
            frameBuffer.append("|\n"); // Salto de línea en el buffer
        }
        frameBuffer.append("=".repeat(cols * 3 + 2)).append("\n");

        // 4. Un solo volcado al stream de salida
        System.out.print(frameBuffer.toString());
        System.out.flush();
    }
    
    public String renderToString(Board board, List<Piece> pieces, GameState state) {
    StringBuilder sb = new StringBuilder();

    int[][] grid = board.getMatrix();

    // copia para no romper estado
    int[][] temp = new int[grid.length][grid[0].length];
    for (int i = 0; i < grid.length; i++) {
        System.arraycopy(grid[i], 0, temp[i], 0, grid[i].length);
    }

    // dibujar piezas activas
    for (Piece p : pieces) {
        int[][] shape = p.getCurrentShape();

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    int x = p.getX() + j;
                    int y = p.getY() + i;

                    if (y >= 0 && y < temp.length && x >= 0 && x < temp[0].length) {
                        temp[y][x] = 1;
                    }
                }
            }
        }
    }

    // construir string
    for (int i = 0; i < temp.length; i++) {
        for (int j = 0; j < temp[i].length; j++) {
            sb.append(temp[i][j] == 0 ? "." : "#");
        }
        sb.append("\n");
    }

    sb.append("-----\n");

    return sb.toString();
}
}