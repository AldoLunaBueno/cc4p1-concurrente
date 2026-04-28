package uni;

public class OnePieceGenerator implements PieceGenerator {
    private int count;
    private int columns;
    private Shape shape;

    public OnePieceGenerator(int columns, Shape shape) {
        this.columns = columns;
        this.shape = shape;
        this.count = 0;
    }

    @Override
    public Piece createPiece(int playerId) {
        Piece piece = new Piece(shape, playerId, columns / 2, 0);
        this.count++;
        return piece;
    }

    public int getCount() {
        return count;
    }
}
