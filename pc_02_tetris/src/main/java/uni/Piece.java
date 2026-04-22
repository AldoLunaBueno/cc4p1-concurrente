package uni;

public class Piece {
    private Shape shape;
    private int player;
    private int x;
    private int y;
    private int rotationIndex;
    private int rows;
    private int columns;

    public Piece(Shape shape, int player, int x, int y) {
        this.shape = shape;
        this.player = player;
        this.rotationIndex = 0;
        this.x = x;
        this.y = y;
        this.rows = shape.getShape(0).length;
        this.columns = shape.getShape(0)[0].length;
    }

    // Cambiar estado

    public void moveDown() {
        this.y++;
    }

    public void moveLeft() {
        this.x--;
    }

    public void moveRight() {
        this.x++;
    }    

    public void rotate() {
        this.rotationIndex = (this.rotationIndex+1) % 4;
    }  

    // Obtener estado

    public int getPlayer() {
        return player;
    }

    public int[][] getCurrentShape() {
        return shape.getShape(rotationIndex);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int rows() {
        return rows;
    }

    public int columns() {
        return columns;
    }  
}
