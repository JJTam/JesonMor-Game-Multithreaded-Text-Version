package castle.comp3021.assignment.piece;

import castle.comp3021.assignment.protocol.Game;
import castle.comp3021.assignment.protocol.Move;
import castle.comp3021.assignment.protocol.Place;

public class CriticalRegionRule implements Rule {


    /**
     * Validate whether the proposed move will violate the critical region rule
     * I.e., there are no more than {link Configuration#getCriticalRegionCapacity()} in the critical region.
     * Determine whether the move is in critical region, using {@link this#isInCriticalRegion(Game, Place)}
     * @param game the current game object
     * @param move the move to be validated
     * @return whether the given move is valid or not
     */
    @Override
    public boolean validate(Game game, Move move) {
        //TODO
        int capacityCount = 0;
        for (int i = 0; i < game.getConfiguration().getSize(); i++) {
            for (int j = 0; j < game.getConfiguration().getSize(); j++)  {
                if (isInCriticalRegion(game, new Place(i,j))) {
                    var piece = game.getPiece(i,j);
                    if (piece instanceof Knight && piece.getPlayer().equals(game.getCurrentPlayer())) {
                        capacityCount++;
                    }
                }
            }
        }
        // only check Knight moving into, dont check moving out/ moving inside
        if (game.getPiece(move.getSource()) instanceof Knight
                && !isInCriticalRegion(game, move.getSource())
                && isInCriticalRegion(game, move.getDestination())) {
            return ((capacityCount + 1) <= game.getConfiguration().getCriticalRegionCapacity());
        }

        return true;
    }

    /**
     * Check whether the given move is in critical region
     * Critical region is {link Configuration#getCriticalRegionSize()} of rows, centered around center place
     * Example:
     *      In a 5 * 5 board, which center place lies in the 3rd row
     *      Suppose critical region size = 3, then for row 1-5, the critical region include row 2-4.
     * @param game the current game object
     * @param place the move to be validated
     * @return whether the given move is in critical region
     */
    private boolean isInCriticalRegion(Game game, Place place) {
        //TODO
        int offset = (game.getConfiguration().getCriticalRegionSize() - 1) / 2;
        int centralRow = game.getCentralPlace().y();
        int upperBound = centralRow + offset;
        int lowerBound = centralRow - offset;
        return (place.y() >= lowerBound && place.y() <= upperBound);
    }


    @Override
    public String getDescription() {
        return "critical region is full";
    }
}
