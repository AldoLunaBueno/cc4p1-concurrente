package uni.network;

import java.net.*;
import java.nio.charset.StandardCharsets;

import uni.view.MinimalConsoleRenderer;
import static uni.model.GameState.*;
import java.io.*;

public class TetrisClient {
    private final String ip;
    private final int port;
    private final BufferedReader reader;

    // Constructor para uso programático (por ejemplo, desde el Orquestador)
    public TetrisClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
        // Inicializamos el lector aquí para que comparta el System.in
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    // Método que arranca la conexión y el bucle de juego
    public void start() throws Exception {
        Socket socket = new Socket(ip, port);
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        MinimalConsoleRenderer renderer = new MinimalConsoleRenderer();

        // Hilo para recibir actualizaciones del servidor y renderizar
        new Thread(() -> {
            try {
                while (true) {
                    GameUpdatePacket packet = (GameUpdatePacket) in.readObject();
                    renderer.render(packet.board, packet.pieces, packet.state, packet.scores, packet.winners);
                    if (packet.state == GAMEOVER) return;
                }
            } catch (Exception e) {
                System.out.println("\n--- Desconectado del servidor ---");
            }
        }).start();

        // El hilo que invoca start() se bloquea aquí leyendo comandos
        System.out.println("Conectado. Controla el Tetris con el teclado:");
        String input;
        while ((input = reader.readLine()) != null) {
            out.println(input);
        }
    }

    // El main original se adapta para usar el nuevo diseño
    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        boolean invalidNetwork = true;
        String ip = null;
        int port = 0;

        while (invalidNetwork) {
            try {
                System.out.print("IP: ");
                String ipString = reader.readLine();
                
                if (isValidIP(ipString)) {
                    ip = ipString;
                } else {
                    System.out.println("Error: Formato de IP inválido.");
                    continue; 
                }

                System.out.print("Puerto: ");
                String portString = reader.readLine();
                port = Integer.parseInt(portString);

                if (isValidPort(port)) {
                    invalidNetwork = false;
                } else {
                    System.out.println("Error: Puerto fuera de rango.");
                    ip = null; 
                }

            } catch (NumberFormatException e) {
                System.out.println("Error: El puerto debe ser un número.");
                ip = null;
            }
        }

        // Instanciamos y arrancamos
        TetrisClient client = new TetrisClient(ip, port);
        client.start();
    }

    private static boolean isValidPort(int portNumber) {
        return portNumber >= 1024 && portNumber <= 65535;
    }

    private static boolean isValidIP(String input) {
        if (input == null) return false;
        String ipv4Regex = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$";
        return input.equals("localhost") || input.matches(ipv4Regex);
    }
}