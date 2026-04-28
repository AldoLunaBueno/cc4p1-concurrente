package uni;

import java.util.ArrayList;
import java.util.Queue;

public class GameController {
    private Board board;
    private PieceGenerator generator;
    private CollisionEngine engine;

    private ArrayList<Piece> pieces;
    private int gameTick;
    private GameState state;
    // El búfer lógico del tick actual
    private Queue<Command> currentTickCommands;

    // Controladores de tiempo lógico
    private int gravityCounter = 0;
    private static final int TICKS_PER_DROP = 30; // Cae cada 30 ticks (0.5s a 60 TPS)

    // ... tus otros campos ...
    private int activePlayerId = 0; // Por defecto o 0

    public GameController(Board board, CollisionEngine engine, PieceGenerator generator) {
        this.board = board;
        this.engine = engine;
        this.generator = generator;
        this.pieces = new ArrayList<>();
        this.gameTick = 0;
        this.state = GameState.PLAYING;
        this.currentTickCommands = new java.util.LinkedList<>();
    }

    // Método para que el servidor le diga al controlador quién juega
    public void setActivePlayer(int playerId) {
        this.activePlayerId = playerId;
    }

    public void enqueueCommand(Command cmd) {
        if (state == GameState.PLAYING) {
            currentTickCommands.add(cmd);
        }
    }

    // Mutación centralizada
    public boolean update() {
        if (state != GameState.PLAYING)
            return false;

        // Spawnear nueva pieza
        if (pieces.isEmpty()) {
            // AQUÍ ESTÁ EL CAMBIO: Pasamos el estado interno
            Piece piece = generator.createPiece(this.activePlayerId);
            if (piece != null) {
                if (engine.isValidMove(piece, board, 0, 0)) {
                    pieces.add(piece);
                    gameTick++;
                    return true;
                } else {
                    state = GameState.GAMEOVER;
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
