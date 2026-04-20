package uni.baeldung;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GreetClientTest {
    private GreetServer server;

    @BeforeEach
    public void setUp() throws IOException {
        new Thread(() -> {
            server = new GreetServer();
            try {
                server.start(666);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // 2. Agregamos una pequeña pausa para asegurar que el servidor tenga tiempo 
        // de vincularse al puerto antes de que el cliente intente conectarse.
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void givenGreetingClient_whenServerRespondsWhenStarted_thenCorrect() throws IOException {
        GreetClient client = new GreetClient();
        client.startConnection("127.0.0.1", 666);
        String response = client.sendMessage("Hola, servidor.");
        assertEquals("¡Hola, cliente!", response);
    }
}
