package castle.comp3021.assignment.protocol;

import castle.comp3021.assignment.piece.Knight;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

public class MakeMoveByStrategy {
    private final Strategy strategy;
    private final Game game;
    private final Move[] availableMoves;

    public MakeMoveByStrategy(Game game, Move[] availableMoves, Strategy strategy) {
        this.game = game;
        this.availableMoves = availableMoves;
        this.strategy = strategy;
    }

    /**
     * Return next move according to different strategies made by {@link castle.comp3021.assignment.player.ComputerPlayer}
     * You can add helper method if needed, as long as this method returns a next move.
     * - {@link Strategy#RANDOM}: select a random move from the proposed moves by all pieces
     * - {@link Strategy#SMART}: come up with some strategy to select a next move from the proposed moves by all pieces
     *
     * @return a next move
     */
    public Move getNextMove() {
        // TODO
        switch (this.strategy) {
            case RANDOM -> {
                int index = new Random().nextInt(this.availableMoves.length);
                return this.availableMoves[index];
            }

            case SMART -> {  // Enhanced greedy
                int minDistance = Integer.MAX_VALUE;
                var bestMove =  this.availableMoves[new Random().nextInt(this.availableMoves.length)];
                for (var move : this.availableMoves) {
                    var piece = this.game.getPiece(move.getSource());
                    if (piece instanceof Knight) {  // only applicable to knight
                        var calMoveMinDis = calNextBestMove(move);
                        if (calMoveMinDis <= minDistance) {
                            minDistance = calMoveMinDis;
                            bestMove = move;
                        }
                    }
                }
                return bestMove;
            }

            default -> {
                return this.availableMoves[new Random().nextInt(this.availableMoves.length)];
            }
        }
    }


    private int calNextBestMove(Move theMove) {
        var minDistances = new ArrayList<Integer>();
        var availableMoves = getFutureAvailableMoves(theMove.getDestination());
        for (var move : availableMoves) {
            var distance = Math.abs(this.game.getCentralPlace().x() - move.getDestination().x()) +
                    Math.abs(this.game.getCentralPlace().y() - move.getDestination().y());
            minDistances.add(distance);
        }
        return minDistances.stream().min(Integer::compare).get();
    }


    private Move[] getFutureAvailableMoves(Place source) {
        var moves = new ArrayList<Move>();
        var steps = new int[]{1, -1, 2, -2};
        for (var stepX : steps) {
            for (var stepY : steps) {
                var destination = new Place(source.x() + stepX, source.y() + stepY);
                if (Math.abs(destination.x() - source.x()) + Math.abs(destination.y() - source.y()) == 3) {
                    moves.add(new Move(source, destination));
                }
            }
        }
        return moves.toArray(new Move[0]);
    }


}
