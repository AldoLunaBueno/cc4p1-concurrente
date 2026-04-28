package uni;

import java.io.Serializable;
import java.util.List;

public class GameUpdatePacket implements Serializable {
    private static final long serialVersionUID = 1L;

    public final Board board;
    public final List<Piece> pieces;
    public final GameState state;

    public GameUpdatePacket(Board board, List<Piece> pieces, GameState state) {
        this.board = board;
        this.pieces = pieces;
        this.state = state;
    }
}
