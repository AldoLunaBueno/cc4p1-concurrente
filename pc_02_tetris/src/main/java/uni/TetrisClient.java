/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package uni;

import java.net.*;
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
                    renderer.render(packet.board, packet.pieces, packet.state);
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
