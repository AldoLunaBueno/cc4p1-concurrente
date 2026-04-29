package uni.model;

public enum Shape {
    
    PIECE_B(new int[][][]{
        {
            { 1 }
        }
    }),
    
    PIECE_O(new int[][][]{
        {
            {1, 1},
            {1, 1}
        }
    }),

    PIECE_L2(new int[][][]{
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

    PIECE_T(new int[][][]{
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
    }),

    PIECE_I1(new int[][][] {
        {
            {1, 1},
            {0, 0}
        },
        {
            {0, 1},
            {0, 1}
        },
        {
            {0, 0},
            {1, 1}
        },
        {
            {1, 0},
            {1, 0}
        }
    }),

    PIECE_I3(new int[][][] {
        {
            {0, 0, 0},
            {1, 1, 1},
            {0, 0, 0}
        },
        {
            {0, 1, 0},
            {0, 1, 0},
            {0, 1, 0}
        }
    });

    private final int[][][] rotations;

    Shape(int[][][] rotations) {
        this.rotations = rotations;
    }

    public int[][] getShape(int rotationIndex) {
        return rotations[rotationIndex % rotations.length];
    }
}
