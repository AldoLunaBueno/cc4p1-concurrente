package uni.network;
import java.net.*;
import uni.view.MinimalConsoleRenderer;
import static uni.model.GameState.*;
import java.io.*;

public class TetrisClient {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 5000);

        // We use ObjectInputStream to read the game state.
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        MinimalConsoleRenderer renderer = new MinimalConsoleRenderer();

        // hilo para escuchar estado
        new Thread(() -> {
            try {
                while (true) {
                    GameUpdatePacket packet = (GameUpdatePacket) in.readObject();
                    renderer.render(packet.board, packet.pieces, packet.state, packet.scores);
                    if (packet.state == GAMEOVER) {
                        return;
                    }
                }
            } catch (Exception e) {
                System.out.println("\n--- Desconectado del servidor ---");
            }
        }).start();

        // input del usuario (mantiene PrintWriter y String lines)
        BufferedReader keyboard = new BufferedReader(
                new InputStreamReader(System.in));

        String input;
        while ((input = keyboard.readLine()) != null) {
            out.println(input);
        }
    }
}
