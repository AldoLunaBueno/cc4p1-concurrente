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

            // controller.processInputs(); // Moved to Server/Client queues

            while (lag >= MS_PER_TICK) {
                controller.update();
                lag -= MS_PER_TICK;
            }

            // Aquí el broadcast bloquea el siguiente ciclo lógico
            // controller.sendState(); // Moved to Server broadcast
        }
    }
}
