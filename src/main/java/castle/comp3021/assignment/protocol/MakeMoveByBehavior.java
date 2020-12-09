package castle.comp3021.assignment.protocol;

import castle.comp3021.assignment.piece.Knight;

import java.util.ArrayList;
import java.util.Random;

public class MakeMoveByBehavior {
    private final Behavior behavior;
    private final Game game;
    private final Move[] availableMoves;

    public MakeMoveByBehavior(Game game, Move[] availableMoves, Behavior behavior){
        this.game = game;
        this.availableMoves = availableMoves;
        this.behavior = behavior;
    }

    /**
     * Return next move according to different strategies made by each piece.
     * You can add helper method if needed, as long as this method returns a next move.
     * - {@link Behavior#RANDOM}: return a random move from {@link this#availableMoves}
     * - {@link Behavior#GREEDY}: prefer the moves towards central place, the closer, the better
     * - {@link Behavior#CAPTURING}: prefer the moves that captures the enemies, killing the more, the better.
     *                               when there are many pieces that can captures, randomly select one of them
     * - {@link Behavior#BLOCKING}: prefer the moves that block enemy's {@link Knight}.
     *                              See how to block a knight here: https://en.wikipedia.org/wiki/Xiangqi (see `Horse`)
     *
     * @return a selected move adopting strategy specified by {@link this#behavior}
     */
    public Move getNextMove() {
        // TODO
        switch (this.behavior) {
            case RANDOM -> {
                return this.availableMoves[new Random().nextInt(this.availableMoves.length)];
            }

            case GREEDY -> {
                var minDistance = Integer.MAX_VALUE;
                var bestMove =  this.availableMoves[0];
                for (var move : this.availableMoves) {
                    var distance = Math.abs(move.getDestination().x() - move.getSource().x()) +
                            Math.abs(move.getDestination().y() - move.getSource().y());
                    if (distance <= minDistance) {
                        minDistance = distance;
                        bestMove = move;
                    }
                }
                return bestMove;
            }

            case CAPTURING -> {
                // num of move protection
                if (this.game.getNumMoves() <= this.game.getConfiguration().numMovesProtection) {
                    return this.availableMoves[new Random().nextInt(this.availableMoves.length)];
                }
                var movesList = new ArrayList<Move>();
                for (var move : this.availableMoves) {
                    var capturePiece = this.game.getPiece(move.getDestination());
                    if (capturePiece != null) {
                        movesList.add(move);
                    }
                }
                return movesList.get(new Random().nextInt(movesList.size()));
            }

            case BLOCKING -> {
                var movesList = new ArrayList<Move>();
                for (var move : this.availableMoves) {
                    var size = this.game.getConfiguration().getSize();
                    var destX = move.getDestination().x();
                    var destY = move.getDestination().y();
                    var upX = destX + 1;
                    var downX = destX - 1;
                    var upY = destY + 1;
                    var downY = destY - 1;

                    if (upX < size && downY >= 0) {

                    }



                        movesList.add(move);

                }


            }

        }
        return this.availableMoves[new Random().nextInt(this.availableMoves.length)];
    }
}

