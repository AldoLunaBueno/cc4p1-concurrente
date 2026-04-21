package uni;

public enum PieceType {
    
    B_PIECE(new int[][][]{
        {
            { 1 }
        }
    }),
    
    O_PIECE(new int[][][]{
        {
            {1, 1},
            {1, 1}
        }
    }),

    L_PIECE(new int[][][]{
        {
            {0, 1},
            {1, 1}
        },
        {
            {1, 0},
            {1, 1}
        },
        {
            {1, 1},
            {1, 0}
        },
        {
            {1, 1},
            {0, 1}
        }
    }),

    T_PIECE(new int[][][]{
        {
            {0, 1, 0},
            {1, 1, 1},
            {0, 0, 0}
        },
        {
            {0, 1, 0},
            {0, 1, 1},
            {0, 1, 0}
        },
        {
            {0, 0, 0},
            {1, 1, 1},
            {0, 1, 0}
        },
        {
            {0, 1, 0},
            {1, 1, 0},
            {0, 1, 0}
        }
    });

    private final int[][][] rotations;

    PieceType(int[][][] rotations) {
        this.rotations = rotations;
    }

    public int[][] getShape(int rotationIndex) {
        return rotations[rotationIndex % rotations.length];
    }
}
