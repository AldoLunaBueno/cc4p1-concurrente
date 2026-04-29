package uni.command;

import uni.engine.CollisionEngine;
import uni.model.Board;
import uni.model.Piece;

public interface Command {
    void execute(Piece piece, Board board, CollisionEngine engine);
}