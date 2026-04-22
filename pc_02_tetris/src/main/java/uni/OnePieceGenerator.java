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

    public Piece createPiece() {
        Piece piece = new Piece(shape, 1, columns/2, 0);
        this.count++;
        return piece;        
    }

    public int getCount() {
        return count;
    }
}
