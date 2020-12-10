package castle.comp3021.assignment.action;

import castle.comp3021.assignment.player.ComputerPlayer;
import castle.comp3021.assignment.player.ConsolePlayer;
import castle.comp3021.assignment.protocol.*;
import castle.comp3021.assignment.protocol.exception.ActionException;


/**
 * The action to pause a piece.
 * <p>
 * The piece must belong to {@link ComputerPlayer}.
 * The piece must not be terminated.
 */
public class PausePieceAction extends Action {
    /**
     * @param game the current {@link Game} object
     * @param args the arguments input by users in the console
     */
    public PausePieceAction(Game game, String[] args) {
        super(game, args);
    }

    /**
     * Pause the piece according to {@link this#args}
     * Expected {@link this#args}: "a1"
     * Hint:
     * Consider corner cases (e.g., invalid {@link this#args})
     * Throw {@link ActionException} when exception happens.
     * <p>
     * Related meethods:
     * - {@link Piece#pause()}
     * - {@link Thread#interrupt()}
     * <p>
     * - The piece thread can be get by
     * {@link castle.comp3021.assignment.protocol.Configuration#getPieceThread(Piece)}
     */
    @Override
    public void perform() throws ActionException {
        //TODO
        if (this.args.length == 0) {
            throw new ActionException("No piece provided");
        }

        var place= ConsolePlayer.parsePlace(this.args[0]);
        if (place == null) {
            throw new ActionException("Invalid piece at place " + args[0]);
        }

        var piece = this.game.getPiece(place);
        if (piece == null) {
            throw new ActionException("piece does not exist at " + place.toString());
        }

        if (!(piece.getPlayer() instanceof ComputerPlayer)) {
            throw new ActionException("piece at " + place.toString() + " does not belong to computer player, thus can not be paused");
        }

        piece.pause();
        this.game.getConfiguration().getPieceThread(piece).interrupt();
    }

    @Override
    public String toString() {
        return "Action[Pause piece]";
    }
}