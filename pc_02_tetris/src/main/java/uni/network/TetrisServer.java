package uni.network;

import java.net.*;

import static uni.model.Shape.PIECE_B;

import java.io.*;
import java.util.*;

import uni.command.Command;
import uni.controller.FixedStepGameLoop;
import uni.controller.GameController;
import uni.controller.GameLoop;
import uni.engine.CollisionEngine;
import uni.engine.PieceGenerator;
import uni.engine.StandardPieceGenerator;
import uni.model.Board;

public class TetrisServer {
    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();
    private GameController controller;
    private int nextPlayerId = 1;
    private final GameLoop gameLoop;

    public static void main(String[] args) {
        try {
            TetrisServer server = new TetrisServer(5000);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    

    public TetrisServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        Scanner scanner  = new Scanner(System.in);
        System.out.print("Columnas: ");
        int columns = scanner.nextInt();
        System.out.print("Filas: ");
        int rows = scanner.nextInt();
        scanner.close();
        Board board = new Board(rows, columns);
        CollisionEngine engine = new CollisionEngine();        
        PieceGenerator generator = new StandardPieceGenerator(columns);

        this.controller = new GameController(board, engine, generator, this);

        // Inyectar el gameloop
        this.gameLoop = new FixedStepGameLoop(controller);
    }

    public void start() throws IOException {
        System.out.println("Servidor iniciado...");

        // aceptar clientes en otro hilo
        new Thread(this::acceptClients).start();

        // loop del juego (servidor manda)
        gameLoop.start();
    }

    private void acceptClients() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                int newPlayerId = nextPlayerId++;
                ClientHandler client = new ClientHandler(socket, this, newPlayerId);
                
                synchronized (clients) {
                    clients.add(client);
                }
                
                // NOTIFICAMOS AL MODELO: "Un nuevo jugador ha entrado"
                controller.addPlayer(newPlayerId);
                
                new Thread(client, "ClientThread-" + client.getPlayerId()).start();
                System.out.println("Cliente conectado: " + newPlayerId);
                
            } catch (IOException e) {
                break; 
            }
        }
    }

    public void disconnectClient(ClientHandler client) {
        synchronized (clients) {
            clients.remove(client);
        }
        
        // NOTIFICAMOS AL MODELO: "Alguien se fue"
        controller.removePlayer(client.getPlayerId());
        
        System.out.println("Servidor: Cliente " + client.getPlayerId() + " desconectado.");
    }

    public void receiveCommand(Command cmd, ClientHandler sender) {
        // LE PREGUNTAMOS AL MODELO: ¿Es el turno de este cliente que me envió el comando?
        if (sender.getPlayerId() == controller.getActivePlayerId()) {
            controller.enqueueCommand(cmd); 
        }
    }

    public void broadcastState() {
        GameUpdatePacket packet = new GameUpdatePacket(
            controller.getBoard(), 
            controller.getPieces(),
            controller.getState(),
            controller.getScores(),
            controller.getWinners()
        );
        synchronized(clients) {
            for (ClientHandler c : clients) {
                c.sendPacket(packet);
            }
        }
    }
}