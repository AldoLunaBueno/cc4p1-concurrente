package uni;

import java.net.*;
import java.io.*;
import java.util.*;

public class TetrisServer {

    public static void main(String[] args) {
        try {
            TetrisServer server = new TetrisServer(5000);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();
    private Queue<ClientHandler> queue = new LinkedList<>();
    private ClientHandler activeClient = null;
    private GameController controller;
    private char nextPlayerChar = 'A';

    public TetrisServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        Board board = new Board(20, 10);
        CollisionEngine engine = new CollisionEngine();
        
        PieceGenerator generator = new PieceGenerator() {
            @Override
            public Piece createPiece() {
                synchronized (queue) {
                    if (!queue.isEmpty()) {
                        activeClient = queue.poll();
                        return new Piece(Shape.PIECE_B, activeClient.getPlayerId(), 10 / 2, 0);
                    }
                }
                activeClient = null;
                return null;
            }
        };

        this.controller = new GameController(board, engine, generator);
    }

    public void start() throws IOException {
        System.out.println("Servidor iniciado...");

        // aceptar clientes en otro hilo
        new Thread(() -> {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    ClientHandler client = new ClientHandler(socket, this, nextPlayerChar++);
                    synchronized (clients) {
                        clients.add(client);
                    }
                    synchronized (queue) {
                        queue.add(client);
                    }
                    new Thread(client).start();
                    System.out.println("Cliente conectado: " + client.getPlayerId());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // loop del juego (servidor manda)
        while (true) {
            controller.update();
            broadcastState();
            
            try { Thread.sleep(16); } catch (InterruptedException e) {}
        }
    }

    public void receiveCommand(Command cmd, ClientHandler sender) {
        if (sender == activeClient) {
            controller.enqueueCommand(cmd);
        }
    }

    private void broadcastState() {
        GameUpdatePacket packet = new GameUpdatePacket(
            controller.getBoard(), 
            controller.getPieces(), 
            controller.getState()
        );
        synchronized(clients) {
            for (ClientHandler c : clients) {
                c.sendPacket(packet);
            }
        }
    }
}