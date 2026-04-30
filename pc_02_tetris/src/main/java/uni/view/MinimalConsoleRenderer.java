package uni.view;

import java.util.List;
import java.util.Map;

import uni.model.Board;
import uni.model.GameState;
import uni.model.Piece;

public class MinimalConsoleRenderer {

    public void render(Board board, List<Piece> pieces, GameState state, Map<Integer, Integer> scores, List<Integer> winners) {
        if (state == GameState.GAMEOVER) {
        // Construimos todo el mensaje final en memoria antes de imprimirlo
        StringBuilder gameOverBuffer = new StringBuilder();
        
        gameOverBuffer.append("\n=== GAME OVER ===\n");
        if (winners != null) {
            if (winners.size() == 1) {
                gameOverBuffer.append("Ganador:\n");
            } else {
                gameOverBuffer.append("Ganadores:\n");
            } 
                
            for (int i = 0; i < winners.size(); i++) {
                int id = winners.get(i);
                int score = scores.get(id);
                gameOverBuffer.append("Jugador ")
                              .append(PlayerSymbolMapper.getSymbolForId(id))
                              .append(" = ")
                              .append(score)
                              .append("\n");
            }
        } else {
            gameOverBuffer.append("No hay ganador. :(\n");
        }
        
        // Un solo volcado a la consola (atómico)
        System.out.print(gameOverBuffer.toString());
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
                if (display[y][x] == 0) {
                    frameBuffer.append(" · ");
                } else {
                    int pid = display[y][x];
                    String color = PlayerSymbolMapper.getColorANSIForId(pid);
                    String reset = PlayerSymbolMapper.ANSI_RESET;
                    String symbol = PlayerSymbolMapper.getSymbolForId(pid);
                    
                    // Envolvemos todo el bloque [X] con el color
                    frameBuffer.append(color).append("[").append(symbol).append("]").append(reset);
                }
            }
            frameBuffer.append("|\n"); // Salto de línea en el buffer
        }
        frameBuffer.append("=".repeat(cols * 3 + 2)).append("\n");
        scores.keySet().forEach(p -> {
            frameBuffer.append(PlayerSymbolMapper.getSymbolForId(p)).append("=").append(scores.get(p)).append(" ");
        });
        frameBuffer.append("\n");
        

        // 4. Un solo volcado al stream de salida
        System.out.print(frameBuffer.toString());
        System.out.flush();
    }
}