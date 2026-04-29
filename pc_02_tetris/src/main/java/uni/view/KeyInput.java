package uni.view;

import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

import uni.command.Command;
import uni.command.MoveDownCommand;
import uni.command.MoveLeftCommand;
import uni.command.MoveRightCommand;

import java.util.Queue;

public class KeyInput {
    private final ConcurrentLinkedQueue<Command> commandQueue;

    public KeyInput() {
        this.commandQueue = new ConcurrentLinkedQueue<>();
        startListening();
    }

    private void startListening() {
        Thread inputThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String input = scanner.nextLine().toLowerCase();
                switch (input) {
                    case "a" -> commandQueue.add(new MoveLeftCommand());
                    case "d" -> commandQueue.add(new MoveRightCommand());
                    // case "w" -> commandQueue.add(new RotateCommand());
                    case "s" -> commandQueue.add(new MoveDownCommand());
                }
            }
        });
        inputThread.setDaemon(true); // Termina cuando App se cierra
        inputThread.start();
    }

    public Queue<Command> pollCommands() {
        // Devuelve la cola concurrente para vaciarla
        return commandQueue;
    }
}