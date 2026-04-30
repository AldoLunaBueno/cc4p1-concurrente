package uni.controller;

public class FixedStepGameLoop implements GameLoop {
    private final GameController controller;
    private volatile boolean running;
    private static final long MS_PER_TICK = 16; // ~60 TPS

    public FixedStepGameLoop(GameController controller) {
        this.controller = controller;
    }

    @Override
    public void start() {
        running = true;
        new Thread(this::processGameLoop, "Fixed-Step-Loop-Thread").start();
    }

    @Override
    public void stop() {
        running = false;
    }

    private void processGameLoop() {
        long previousTime = System.nanoTime() / 1_000_000;
        long lag = 0;
        boolean isGameActive = true; // Control de estado global

        while (running) {
            long currentTime = System.nanoTime() / 1_000_000;
            long elapsedTime = currentTime - previousTime;
            previousTime = currentTime;
            lag += elapsedTime;

            // Traslada comandos de la cola concurrente (red) a la cola local (lógica)
            controller.processInputs();

            // Bandera para saber si el tiempo lógico realmente avanzó
            boolean stateChanged = false; 

            while (lag >= MS_PER_TICK && isGameActive) {
                // update() devuelve false cuando el estado es GAMEOVER
                isGameActive = controller.update(); // evita calcular físicas de un juego terminado
                lag -= MS_PER_TICK;
                stateChanged = true; 
            }

            // Enviamos el estado por la red (incluyendo el paquete final de GAMEOVER)
            if (stateChanged) {
                controller.sendState(); 
            }

            // Una vez que el estado final fue enviado a los clientes, detenemos el motor
            if (!isGameActive) {
                System.out.println("Servidor: Juego terminado. Deteniendo el GameLoop...");
                this.stop(); // Cambia 'running' a false, saliendo del bucle principal
            }

            // Yielding: Dejamos respirar al procesador y a los sockets de red
            try {
                // Dormir 2ms es suficiente para que el SO atienda otros hilos (como los ClientHandlers)
                Thread.sleep(2); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        System.out.println("Servidor: GameLoop finalizado correctamente.");
    }
}