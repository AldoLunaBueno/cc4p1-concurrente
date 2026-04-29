package uni.engine;

import uni.model.Piece;

public interface PieceGenerator {
    Piece createPiece(int playerId); // Ahora pide el ID
}
