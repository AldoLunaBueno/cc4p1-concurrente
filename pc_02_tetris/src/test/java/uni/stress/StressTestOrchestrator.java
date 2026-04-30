package uni.stress;

import uni.network.TetrisClient;
import uni.network.TetrisServer;

public class StressTestOrchestrator {
    public static void main(String[] args) {
        final int PORT = 8080;
        final int NUMBER_OF_BOTS = 100;

        // Arrancar el servidor en un hilo separado
        new Thread(() -> {
            try {
                // Instanciando con el constructor sobrecargado
                TetrisServer server = new TetrisServer(80, 40, PORT);
                server.forceFastDrop();
                server.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // Descanso al servidor para levantar el ServerSocket
        try { Thread.sleep(500); } catch (InterruptedException e) {}

        // Arrancar los N clientes Headless
        for (int i = 0; i < NUMBER_OF_BOTS; i++) {
            HeadlessClient bot = new HeadlessClient("localhost", PORT, i + 1);
            new Thread(bot).start();

            // Le damos un micro-respiro de 10 a 20 milisegundos al servidor 
            // entre cada conexión para que procese el accept()
            try {
                Thread.sleep(15); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Arrancar el cliente humano
        System.out.println("Servidor y " + NUMBER_OF_BOTS + " bots iniciados.");
        System.out.println("Iniciando tu cliente local...");
        
        try {
            TetrisClient playerClient = new TetrisClient("localhost", PORT);
            // Bloquear el hilo principal para jugar
            playerClient.start(); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}