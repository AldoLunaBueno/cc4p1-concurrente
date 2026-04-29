package uni;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import static uni.GameState.*;

public class GameController {
    private Board board;
    private PieceGenerator generator;
    private CollisionEngine engine;
    private TetrisServer server; // El puente hacia la red

    private ArrayList<Piece> pieces;
    private int gameTick;
    private GameState state;

    // Concurrencia: Búfer seguro para múltiples hilos de red
    private ConcurrentLinkedQueue<Command> networkCommands;
    
    // Concurrencia: Búfer local y privado del hilo del Game Loop
    private Queue<Command> currentTickCommands;

    // Controladores de tiempo lógico
    private int gravityCounter = 0;
    private static final int TICKS_PER_DROP = 30; // Cae cada 30 ticks (0.5s a 60 TPS)
    private Queue<Integer> activePlayers; 
    private int currentTurnPlayerId = 0; // Carácter nulo por defecto

    public GameController(Board board, CollisionEngine engine, PieceGenerator generator, TetrisServer server) {
        this.board = board;
        this.engine = engine;
        this.generator = generator;
        this.server = server;
        this.pieces = new ArrayList<>();
        this.gameTick = 0;
        this.state = PLAYING;

        // Concurrencia
        this.networkCommands = new ConcurrentLinkedQueue<>();
        this.currentTickCommands = new LinkedList<>();        
        this.activePlayers = new LinkedList<>();
    }

    // El servidor llamará a esto cuando alguien se conecte
    public void addPlayer(int playerId) {
        activePlayers.add(playerId);
    }

    // El servidor llamará a esto cuando alguien se desconecte
    public void removePlayer(int playerId) {
        activePlayers.remove(playerId);
        // Si se desconectó el que estaba jugando, la pieza actual cae vacía o muere, 
        // lo manejaremos más adelante.
    }

    public int getActivePlayerId() {
        return currentTurnPlayerId;
    }

    // MÉTODO PRODUCTOR: Llamado por los n ClientHandlers (Hilos múltiples)
    public void enqueueCommand(Command cmd) {
        if (state == PLAYING) {
            currentTickCommands.add(cmd); // Operación atómica y segura
        }
    }

    // MÉTODO CONSUMIDOR: Llamado ÚNICAMENTE por el GameLoop (Hilo único)
    public void processInputs() {
        if (state != GameState.PLAYING) return;
        
        // Vaciamos la cola concurrente y la pasamos a nuestra cola local
        // para procesarla tranquilamente en el update() sin bloqueos.
        while (!networkCommands.isEmpty()) {
            currentTickCommands.add(networkCommands.poll());
        }
    }

    // MÉTODO DE SALIDA: Llamado ÚNICAMENTE por el GameLoop al terminar el ciclo
    public void sendState() {
        if (server != null) {
            server.broadcastState();
        }
    }

    // Mutación centralizada
    public boolean update() {
        if (state != PLAYING)
            return false;

        // Spawnear nueva pieza y manejar turnos
        if (pieces.isEmpty()) {
            if (activePlayers.isEmpty()) {
                return true;
            }

            // Lógica Round-Robin: Sacamos al primero de la fila y lo ponemos al final
            currentTurnPlayerId = activePlayers.poll();
            activePlayers.add(currentTurnPlayerId);

            // Pasamos el estado interno
            Piece piece = generator.createPiece(this.currentTurnPlayerId);
            if (piece != null) {
                if (engine.isValidMove(piece, board, 0, 0)) {
                    pieces.add(piece);
                    gameTick++;
                    return true;
                } else {
                    state = GAMEOVER;
                    gameTick++;
                    return false; // ya terminó el juego
                }
            } else {
                gameTick++;
                return true; // Esperando jugador
            }
        }

        // Resolver input del jugador
        while (!currentTickCommands.isEmpty()) {
            Command cmd = currentTickCommands.poll();
            if (!pieces.isEmpty()) {
                cmd.execute(pieces.get(0), board, engine);
            }
        }

        // Gravedad controlada por ticks
        gravityCounter++;
        if (gravityCounter >= TICKS_PER_DROP) {
            for (int i = pieces.size() - 1; i >= 0; i--) {
                Piece piece = pieces.get(i);
                if (engine.isValidMove(piece, board, 0, 1)) {
                    piece.moveDown();
                } else {
                    lockPiece(piece, board);
                }
            }
            gravityCounter = 0; // Reiniciar contador
        }

        gameTick++;
        return true;
    }

    private void lockPiece(Piece piece, Board board) {
        int player = piece.getPlayer();
        for (int i = 0; i < piece.rows(); i++) {
            for (int j = 0; j < piece.columns(); j++) {
                // Solo bloqueamos las partes sólidas
                if (piece.getCurrentShape()[i][j] != 0) {
                    int x = piece.getX() + j;
                    int y = piece.getY() + i;
                    board.lockBlock(x, y, player);
                }
            }
        }
        pieces.remove(piece);
    }

    public Board getBoard() {
        return board;
    }

    public ArrayList<Piece> getPieces() {
        return pieces;
    }

    public GameState getState() {
        return state;
    }

    public int getGameTick() {
        return this.gameTick;
    }
}
