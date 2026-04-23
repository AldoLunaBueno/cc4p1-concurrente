package uni.baeldung;

import java.io.IOException;

import io.github.cdimascio.dotenv.Dotenv;

public class ClientMain {
    public static void main(String[] args) throws IOException {
        Dotenv dotenv = Dotenv.load();
        String ip = dotenv.get("SERVER_IP");
        int port = Integer.valueOf(dotenv.get("PORT"));
        GreetClient client = new GreetClient();
        System.out.println("IP: " + ip + " Port: " + port);
        client.startConnection(ip, port);
        String response = client.sendMessage("Hola, servidor.");
        System.out.println("Respuesta del servidor: " + response);
    }
}