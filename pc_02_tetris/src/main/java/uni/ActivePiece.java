package uni;

public class ActivePiece {
    private PieceType piece;
    private int player;
    private int x;
    private int y;
    private int rotationIndex;
    private int rows;
    private int columns;

    public ActivePiece(PieceType piece, int player, int x, int y) {
        this.piece = piece;
        this.player = player;
        this.rotationIndex = 0;
        this.x = x;
        this.y = y;
        this.rows = piece.getShape(0).length;
        this.columns = piece.getShape(0)[0].length;
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
        return piece.getShape(rotationIndex);
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
