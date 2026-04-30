package uni.stress;

import uni.network.GameUpdatePacket;
import java.io.*;
import java.net.Socket;
import java.util.Random;

import static uni.model.GameState.GAMEOVER;

public class HeadlessClient implements Runnable {
    private final String ip;
    private final int port;
    private final int botId;
    // Asume las teclas que tengas mapeadas en tu KeyInput
    private final String[] possibleCommands = {"a", "s", "d", "w", "e"}; 
    private final Random random = new Random();
    private boolean isRunning = true;

    public HeadlessClient(String ip, int port, int botId) {
        this.ip = ip;
        this.port = port;
        this.botId = botId;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(ip, port);
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Bot " + botId + " conectado.");

            // Hilo para recibir y descartar (consumir) el estado del juego
            Thread receiverThread = new Thread(() -> {
                try {
                    while (isRunning) {
                        GameUpdatePacket packet = (GameUpdatePacket) in.readObject();
                        if (packet.state == GAMEOVER) {
                            isRunning = false;
                            break;
                        }
                    }
                } catch (Exception e) {
                    isRunning = false;
                }
            });
            receiverThread.start();

            // Bucle principal: Enviar comandos aleatorios cada cierto tiempo
            while (isRunning) {
                String randomCommand = possibleCommands[random.nextInt(possibleCommands.length)];
                out.println(randomCommand);
                
                // Simula el tiempo de reacción humano o la agresividad del bot (ej. 200ms)
                Thread.sleep(50); 
            }

        } catch (Exception e) {
            System.out.println("Bot " + botId + " falló al conectar o se desconectó.");
        }
    }
}