package castle.comp3021.assignment.textversion;

import castle.comp3021.assignment.piece.Archer;
import castle.comp3021.assignment.piece.Knight;
import castle.comp3021.assignment.player.ComputerPlayer;
import castle.comp3021.assignment.protocol.*;
import castle.comp3021.assignment.protocol.exception.InvalidConfigurationError;

public class Main {
    protected static Player whitePlayer;
    protected static Player blackPlayer;
    protected static int size;
    protected static int numMovesProtection;
    protected static int criticalRegionSize;
    protected static int criticalRegionCapacity;

    static {
//        whitePlayer = new ConsolePlayer("White");
//        blackPlayer = new ComputerPlayer("Black");
        whitePlayer = new ComputerPlayer("PC", Color.BLUE, Strategy.RANDOM);
        blackPlayer = new ComputerPlayer("AI", Color.GREEN, Strategy.SMART);
        size = 15;
        numMovesProtection = 20;
        criticalRegionSize = 5;
        criticalRegionCapacity = 2;
    }

    /**
     * Create and initialize a game
     *
     * @param size               size of gameboard
     * @param numMovesProtection number of moves with capture protection
     * @return the game object
     */
    public static Game createGame(int size, int numMovesProtection, int criticalRegionSize,
                                  int criticalRegionCapacity) {
        Configuration configuration =
                new Configuration(size, new Player[]{whitePlayer, blackPlayer}, numMovesProtection,
                        criticalRegionSize, criticalRegionCapacity);
        try {
            configuration.validateConfiguration();
        } catch (InvalidConfigurationError e) {
            throw e;
        }

        for (int i = 0; i < size; i++) {
            if (i % 2 == 0) {
                var piece = new Knight(blackPlayer, Behavior.GREEDY);
                configuration.addInitialPiece(piece, i, size - 1);
            } else {
                var piece = new Archer(blackPlayer, Behavior.BLOCKING);
                configuration.addInitialPiece(piece, i, size - 1);
            }
        }
        for (int i = 0; i < size; i++) {
            if (i % 2 == 0) {
                configuration.addInitialPiece(new Knight(whitePlayer), i, 0);
            } else {
                configuration.addInitialPiece(new Archer(whitePlayer), i, 0);
            }
        }
        return new JesonMor(configuration);
    }

    public static void main(String[] args) {
// var helper = "four integer arguments are required specifying <size>
// <numMovesProtection> <criticalRegionSize> <criticalRegionCapacity>";

        if (args.length >= 1) {
            try {
                size = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("the first argument is not a number");
            }
        }
        if (args.length >= 2) {
            try {
                numMovesProtection = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("the second argument is not a number");
            }
        }

        if (args.length >= 3) {
            try {
                criticalRegionSize = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("the third argument is not a number");
            }
        }

        if (args.length >= 4) {
            try {
                criticalRegionCapacity = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("the fourth argument is not a number");
            }
        }
        createGame(size, numMovesProtection, criticalRegionSize, criticalRegionCapacity).start();
    }
}
