package uni.engine;

import uni.model.Piece;
import uni.model.Shape;
import static uni.model.Shape.*;
import java.util.Random;

import java.util.ArrayList;
import java.util.List;

public class StandardPieceGenerator implements PieceGenerator {
    private int count;
    private int columns;
    private ArrayList<Shape> shapes;
    private Random rand;

    public StandardPieceGenerator(int columns) {
        this.columns = columns;
        rand = new Random();
        shapes = new ArrayList<>(List.of(Shape.values()));
    }

    @Override
    public Piece createPiece(int playerId) {
        Shape shape = shapes.get(rand.nextInt(shapes.size()));
        return new Piece(shape, playerId, columns / 2, 0);
    }

    public int getCount() {
        return count;
    }
}
