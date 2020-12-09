package castle.comp3021.assignment.piece;

import castle.comp3021.assignment.protocol.*;
import castle.comp3021.assignment.textversion.JesonMor;

import java.util.ArrayList;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Knight piece that moves similar to knight in chess.
 * Rules of move of Knight can be found in wikipedia (https://en.wikipedia.org/wiki/Knight_(chess)).
 *
 * @see <a href='https://en.wikipedia.org/wiki/Knight_(chess)'>Wikipedia</a>
 */
public class Knight extends Piece {
    static class InvalidMove extends Move {
        public InvalidMove() {
            super(-1, -1, -1, -1);
        }
    }

    /**
     * A BlockingDeque containing the candidate move
     */
    private final BlockingDeque<Move> candidateMoveQueue;

    /**
     * A LinkedBlockingDeque storing the parameters {@link Game} and {@link Place}
     * When calculateMoveParametersQueue is empty, the current piece thread should be waiting
     * until parameters {@link Game} and {@link Place} are passed in, the thread starts calculate the candidate move.
     */
    private final BlockingDeque<Object[]> calculateMoveParametersQueue;

    private final AtomicBoolean isTimeout = new AtomicBoolean(true);


    public Knight(Player player, Behavior behavior) {
        super(player, behavior);
        this.candidateMoveQueue = new LinkedBlockingDeque<>();
        this.calculateMoveParametersQueue = new LinkedBlockingDeque<>();
    }

    public Knight(Player player) {
        super(player);
        this.candidateMoveQueue = new LinkedBlockingDeque<>();
        this.calculateMoveParametersQueue = new LinkedBlockingDeque<>();
    }

    @Override
    public char getLabel() {
        return 'K';
    }

    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
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
        return moves.stream()
                    .filter(move -> validateMove(game, move))
                    .toArray(Move[]::new);
    }

    /**
     * Returns a valid candidate move given the current game {@link Game} and place  {@link Place} of the piece.
     * A 1 second timeout should be set.
     * If time is out, then no candidate move is proposed for this piece this round
     * The implementation is the same as {@link Archer#getCandidateMove(Game, Place)}
     * <p>
     * Hint:
     * - The actual candidate move is selected in {@link Knight#run}
     * so in this method, you need to pick up one candidate move from {@link Knight#candidateMoveQueue}
     * - if the returned move is invalid, nothing should be returned.
     * - Handle {@link InterruptedException}:
     * - nothing should be returned in such case
     *
     * @param game   the game object
     * @param source the current place of the piece
     * @return one candidate move
     */
    @Override
    public synchronized Move getCandidateMove(Game game, Place source) {
        //TODO
        if (!this.running.get()) {
            System.out.println("Paused");
            return null;
        }

        // reset
        this.isTimeout.set(true);
        this.candidateMoveQueue.clear();
        this.calculateMoveParametersQueue.clear();
        this.calculateMoveParametersQueue.add(new Object[]{game, source});

        var pieceThread = game.getConfiguration().getPieceThread(game.getPiece(source));
        Thread thread = new Thread(() -> timeoutCounter(pieceThread));
        thread.start();
        this.notify();
        try {
            while (!pieceThread.isInterrupted()) {
                this.wait(1000);  // one second time out
                if (!this.isTimeout.get()) {
                    System.out.println("Got K");
                    return candidateMoveQueue.poll();
                }
                else if (this.isTimeout.get()) {
                    System.out.println("Timeout K");
                    return null;
                }
            }
        } catch (InterruptedException ignored) {
        }

        return this.candidateMoveQueue.poll();

    }

    private void timeoutCounter(Thread pieceThread) {
        long endTimeMillis = System.currentTimeMillis() + 1000;
        while (true) {
            // method logic
            if (System.currentTimeMillis() > endTimeMillis) {
                this.isTimeout.set(true);
                pieceThread.interrupt();
                return;
            }
        }
    }


    private boolean validateMove(Game game, Move move) {
        var rules = new Rule[]{
                new OutOfBoundaryRule(),
                new OccupiedRule(),
                new VacantRule(),
                new NilMoveRule(),
                new FirstNMovesProtectionRule(game.getConfiguration().getNumMovesProtection()),
                new KnightMoveRule(),
                new KnightBlockRule(),
                // newly added rule
                new CriticalRegionRule(),
        };
        for (var rule : rules) {
            if (!rule.validate(game, move)) {
                return false;
            }
        }
        return true;
    }


    /**
     * An atomic boolean variable which marks whether this piece thread is running
     * running = true: this piece is running.
     * running = false: this piece is paused.
     */
    private final AtomicBoolean running = new AtomicBoolean(true);

    /**
     * An atomic boolean variable which marks whether this piece thread is stopped
     * stopped = false: this piece is running.
     * stopped = true: this piece stops, and cannot be paused again.
     */
    private final AtomicBoolean stopped = new AtomicBoolean(false);

    /**
     * Pause this piece thread.
     * Hint:
     *     - Using {@link Knight#running}
     */
    @Override
    public void pause() {
        //TODO
        this.running.set(false);
    }

    /**
     * Resume the piece thread
     * Hint:
     *      - Using {@link Knight#running}
     *      - Using {@link Object#notifyAll()}
     */
    @Override
    public void resume() {
        //TODO
        this.running.set(true);
        synchronized (this) {
            notifyAll();
        }
    }

    /**
     * Stop the piece thread
     * Hint:
     *     - Using {@link Knight#stopped}
     *     - Please do NOT use the deprecated {@link Thread#stop}
     */
    @Override
    public void terminate() {
        //TODO
        this.stopped.set(true);
    }

    /**
     * The piece should be runnable
     * Consider the following situations:
     *      - When it is NOT the turn of the player which this piece belongs to:
     *          - this thread should be waiting ({@link Object#wait()})
     *      - When it is the turn of the player which this piece belongs to (marked by {@link Knight#running}):
     *          - take out the {@link Game} and {@link Place} objects from calculateMoveParametersQueue
     *          - propose a candidate move (you may take advantage of {@link Knight#getAvailableMoves}
     *            using {@link MakeMoveByBehavior#getNextMove()} according to {@link this#behavior}
     *                      come up with any strategy to pick one from {@link Knight#getAvailableMoves(Game, Place)}
     *          - add the proposed candidate move to {@link Knight#candidateMoveQueue}
     *      - When this piece has been stopped (marked by {@link Knight#stopped}): no more reaction
     *      - Handle {@link InterruptedException}
     *  Hint: the same as {@link Archer#run()}
     */
    @Override
    public void run() {
        //TODO
        while(!this.stopped.get()) {
            try {
                synchronized (this) {
                    while (this.calculateMoveParametersQueue.isEmpty()) {
//                        System.out.println(Thread.currentThread().getName() + " waiting(empty queue/pausing piece)");
                        this.wait();
                    }

                    var objects = this.calculateMoveParametersQueue.poll();
                    if (objects != null) {
                        var game = (JesonMor) objects[0];
                        var place = (Place) objects[1];
                        while (!game.getCurrentPlayer().equals(this.getPlayer())) {
//                            System.out.println(Thread.currentThread().getName() + " waiting(not this player's turn)");
                            this.wait();
                        }

                        if (this.running.get()) {
                            var availableMoves = getAvailableMoves(game, place);
                            System.out.println("L = " + availableMoves.length);
                            if (availableMoves.length != 0) {
                                this.candidateMoveQueue.add(new MakeMoveByBehavior(game, availableMoves, this.behavior).getNextMove());
                                this.isTimeout.set(false);
                            }
                            this.notify();
//                            System.out.println(Thread.currentThread().getName() + " running");
                        }
                    }
                }
            } catch (InterruptedException ignored) {
                this.isTimeout.set(true);
                this.candidateMoveQueue.clear();
            }
        }
    }

}
