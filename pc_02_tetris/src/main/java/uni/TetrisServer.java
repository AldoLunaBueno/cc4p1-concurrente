package uni;

import java.net.*;
import java.io.*;
import java.util.*;

public class TetrisServer {
    public static void main(String[] args) {
        
        try {
            Board board = new Board(20, 10);
            CollisionEngine engine = new CollisionEngine();
            PieceGenerator generator = new OnePieceGenerator(10, Shape.PIECE_B);
            KeyInput in = new KeyInput();
        MinimalConsoleRenderer out = new MinimalConsoleRenderer();
        GameController controller = new GameController(board, generator, engine, in, out);
           

            TetrisServer server = new TetrisServer(5000, controller);
            server.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private MinimalConsoleRenderer renderer = new MinimalConsoleRenderer();
    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();
    private GameController controller;

    public TetrisServer(int port, GameController controller) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.controller = controller;
    }

    public void start() throws IOException {
        System.out.println("Servidor iniciado...");

        // aceptar clientes en otro hilo
        new Thread(() -> {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    ClientHandler client = new ClientHandler(socket, this);
                    clients.add(client);
                    new Thread(client).start();
                    System.out.println("Cliente conectado");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // loop del juego (servidor manda)
        while (true) {
            controller.update();
            broadcastState();
            
            try { Thread.sleep(10); } catch (InterruptedException e) {}
        }
    }

    public void receiveCommand(Command cmd) {
        controller.enqueueCommand(cmd);
    }

    private void broadcastState() {
        String state = serializeState();
        for (ClientHandler c : clients) {
            c.send(state);
        }
    }

    private String serializeState() {
    StringBuilder sb = new StringBuilder();

    int[][] grid = controller.getBoard().getMatrix();

    // copia del tablero (para no modificar el original)
    int[][] temp = new int[grid.length][grid[0].length];
    for (int i = 0; i < grid.length; i++) {
        System.arraycopy(grid[i], 0, temp[i], 0, grid[i].length);
    }

    // 🔥 dibujar piezas activas
    for (Piece p : controller.getPieces()) {
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

    // 🔥 convertir a texto
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