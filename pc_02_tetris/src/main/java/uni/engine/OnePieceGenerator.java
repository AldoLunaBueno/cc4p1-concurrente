package uni.engine;

import uni.model.Piece;
import uni.model.Shape;

public class OnePieceGenerator implements PieceGenerator {
    private int count;
    private int columns;
    private Shape shape;

    public OnePieceGenerator(int columns, Shape shape) {
        this.columns = columns;
        this.shape = shape;
    }

    @Override
    public Piece createPiece(int playerId) {
        return new Piece(shape, playerId, columns / 2, 0);
    }

    public int getCount() {
        return count;
    }
}
