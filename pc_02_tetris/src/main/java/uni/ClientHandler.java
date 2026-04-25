package uni;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private TetrisServer server;

    public ClientHandler(Socket socket, TetrisServer server) throws IOException {
        this.socket = socket;
        this.server = server;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                Command cmd = parseCommand(line);
                if (cmd != null) { // 🔥 FIX
                    server.receiveCommand(cmd);
                }               
            }
        } catch (IOException e) {
            System.out.println("Cliente desconectado");
        }
    }

    public void send(String msg) {
        out.println(msg);
    }

    private Command parseCommand(String input) {
        input = input.trim().toLowerCase(); // 🔥 clave
        return switch (input) {
            case "a" -> new MoveLeftCommand();
            case "d" -> new MoveRightCommand();
            case "s" -> new MoveDownCommand();
            default -> null;
        };
    }
}