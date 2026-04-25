package uni;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class GameController {
    private Board board;
    private PieceGenerator generator;
    private CollisionEngine engine;
    private KeyInput in;
    private MinimalConsoleRenderer out;
    
    private ArrayList<Piece> pieces;
    private int gameTick;
    private GameState state;
    // El búfer lógico del tick actual
    private Queue<Command> currentTickCommands;

    // Controladores de tiempo lógico
    private int gravityCounter = 0;
    private static final int TICKS_PER_DROP = 30; // Cae cada 30 ticks (0.5s a 60 TPS)

    public GameController(Board board, PieceGenerator generator, CollisionEngine engine, KeyInput in, MinimalConsoleRenderer out) {
        this.board = board;
        this.generator = generator;
        this.engine = engine;
        this.in = in;
        this.out = out;
        this.pieces = new ArrayList<>();
        this.gameTick = 0;
        this.state = GameState.PLAYING;
        this.currentTickCommands = new java.util.concurrent.ConcurrentLinkedQueue<>();
        
        
    }
    public Board getBoard() { return board; }
        public List<Piece> getPieces() { return pieces; }
        public GameState getState() { return state; }
    
    public void enqueueCommand(Command cmd) {
    currentTickCommands.add(cmd);
    }

    // Solo extrae del exterior y encola, no muta el estado
    public void processInputs() {

        if (state != GameState.PLAYING) return;

        // Vaciamos la cola concurrente del hilo de I/O y la pasamos 
        // a la cola secuencial de nuestro motor lógico.
        Queue<Command> externalCommands = in.pollCommands();
        System.out.println("commands: " + externalCommands.size());
        while (!externalCommands.isEmpty()) {
            currentTickCommands.add(externalCommands.poll());
        }
    }

        // Mutación centralizada
    public boolean update() {
        if (state != GameState.PLAYING) return false;

        // Spawnear nueva pieza
        if (pieces.isEmpty()) {
            Piece piece = generator.createPiece();
            if (engine.isValidMove(piece, board, 0, 0)) {
                pieces.add(piece);
                gameTick++;
                return true;
            } else {
                gameTick++;
                return false; // ya termińo el juego
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

    public void sendState() {
        out.render(board, pieces, state);
    }

    public int getGameTick() {
        return this.gameTick;
    }
}
