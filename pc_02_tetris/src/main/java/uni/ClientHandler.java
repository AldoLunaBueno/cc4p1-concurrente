package uni;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;

class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private ObjectOutputStream out;
    private TetrisServer server;

    // Identidad real (lógica)
    private int playerId;
    
    // Identidad visual (presentación)
    private String displaySymbol;

    public ClientHandler(Socket socket, TetrisServer server, int playerId) throws IOException {
        this.socket = socket;
        this.server = server;
        this.playerId = playerId;
        this.displaySymbol = PlayerSymbolMapper.getSymbolForId(playerId);
        
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public int getPlayerId() {
        return playerId;
    }

    @Override
    public void run() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                Command cmd = parseCommand(line);
                if (cmd != null) {
                    server.receiveCommand(cmd, this);
                }
            }
        } catch (IOException e) {
            System.out.println("Error enviando paquete a Jugador " + playerId + " " + displaySymbol);
            server.disconnectClient(this);
        }
    }

    public void sendPacket(GameUpdatePacket packet) {
        try {
            out.writeObject(packet);
            out.reset(); // Crucial para evitar caché del estado obsoleto
            out.flush();
        } catch (IOException e) {
            System.out.println("Error enviando paquete a Jugador " + playerId + " " + displaySymbol);
        }
    }

    private Command parseCommand(String input) {
        input = input.trim().toLowerCase();
        return switch (input) {
            case "a" -> new MoveLeftCommand();
            case "d" -> new MoveRightCommand();
            case "s" -> new MoveDownCommand();
            // TODO: Añadir "w" para rotar
            default -> null;
        };
    }
}