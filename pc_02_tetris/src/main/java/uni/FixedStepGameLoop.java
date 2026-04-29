package uni;

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

        while (running) {
            long currentTime = System.nanoTime() / 1_000_000;
            long elapsedTime = currentTime - previousTime;
            previousTime = currentTime;
            lag += elapsedTime;


            // Traslada comandos de la cola concurrente (red) a la cola local (lógica)
            controller.processInputs();

            // Bandera para saber si el tiempo lógico realmente avanzó
            boolean stateChanged = false; 

            while (lag >= MS_PER_TICK) {
                // Ejecuta inputs, aplica gravedad, revisa colisiones
                controller.update();
                lag -= MS_PER_TICK;
                stateChanged = true; // El juego se movió
            }

            // El loop decide que ya terminó de calcular la física
            // y le dice al controlador que dispare el estado por la red.
            // Solo asfixiamos la red si realmente hay algo nuevo que mostrar
            if (stateChanged) {
                controller.sendState(); 
            }

            // Yielding: Dejamos respirar al procesador y a los sockets de red
            try {
                // Dormir 2ms es suficiente para que el SO atienda otros hilos (como los ClientHandlers)
                Thread.sleep(2); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}