package uni.network;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import uni.model.Board;
import uni.model.GameState;
import uni.model.Piece;

public class GameUpdatePacket implements Serializable {
    private static final long serialVersionUID = 1L;

    public final Board board;
    public final List<Piece> pieces;
    public final GameState state;
    public final Map<Integer, Integer> scores;

    public GameUpdatePacket(Board board, List<Piece> pieces, GameState state, Map<Integer, Integer> scores) {
        this.board = board;
        this.pieces = pieces;
        this.state = state;
        this.scores = scores;
    }
}
