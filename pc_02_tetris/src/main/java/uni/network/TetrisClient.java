package uni.network;
import java.net.*;

import uni.view.MinimalConsoleRenderer;
import static uni.model.GameState.*;
import java.io.*;

public class TetrisClient {
    public static void main(String[] args) throws Exception {
    // Un solo lector para toda la vida del programa
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    
    boolean invalidNetwork = true;
    String ip = null;
    int port = 0;

    while (invalidNetwork) {
        try {
            System.out.print("IP: ");
            String ipString = reader.readLine();
            // Si el usuario presiona Enter sin escribir, ipString será ""
            
            if (isValidIP(ipString)) {
                ip = ipString;
            } else {
                System.out.println("Error: Formato de IP inválido (use 'localhost', '' o IPv4)");
                continue; 
            }

            System.out.print("Puerto: ");
            String portString = reader.readLine();
            port = Integer.parseInt(portString); // Convierte a entero

            if (isValidPort(port)) {
                invalidNetwork = false;
            } else {
                System.out.println("Error: Puerto fuera de rango.");
                ip = null; // Reiniciamos IP para repetir el ciclo correctamente
            }

        } catch (NumberFormatException e) {
            System.out.println("Error: El puerto debe ser un número.");
            ip = null;
        }
    }

    // System.in sigue abierto y listo
    Socket socket = new Socket(ip, port);

    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
    MinimalConsoleRenderer renderer = new MinimalConsoleRenderer();

    // Hilo para recibir actualizaciones del servidor
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

    // El hilo principal ahora lee comandos usando el mismo 'reader'
    System.out.println("Conectado. Controla el Tetris con el teclado:");
    String input;
    while ((input = reader.readLine()) != null) {
        out.println(input);
    }
}

    private static boolean isValidPort(int portNumber) {
        return portNumber >= 1024 && portNumber <= 65535;
    }

    private static boolean isValidIP(String input) {
        // Si es nulo, lo tratamos como inválido
        if (input == null) return false;

        // Definimos el Regex para IPv4 (0.0.0.0 a 255.255.255.255)
        String ipv4Regex = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$";

        // Validamos si es "localhost", si está vacío, o si cumple el patrón IPv4
        return input.equals("localhost") || input.matches(ipv4Regex);
    }
}
