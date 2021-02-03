/* Skeleton Copyright (C) 2015, 2020 Paul N. Hilfinger and the Regents of the
 * University of California.  All rights reserved. My codes are hinted from my
 * project tablut's TextPlayer.java. */
package loa;

import static loa.Move.mv;

/** A Player that prompts for moves and reads them from its Game.
 *  @author YULAN RONG
 */
class HumanPlayer extends Player {

    /** A new HumanPlayer with no piece or controller (intended to produce
     *  a template). */
    HumanPlayer() {
        this(null, null);
    }

    /** A HumanPlayer that plays the SIDE pieces in GAME.  It uses
     *  GAME.getMove() as a source of moves.  */
    HumanPlayer(Piece side, Game game) {
        super(side, game);
        _side = side;
        _game = game;
    }

    @Override
    String getMove() {
        while (true) {
            String line = _game.readLine(true);

            if (line == null) {
                return "quit";
            } else if (Move.isFormatMove(line)) {
                if (_game.getBoard().winner() != null
                        || _game.getBoard().turn() != _side) {
                    _game.reportError("misplaced move");
                    continue;
                } else {
                    Move move = mv(line);
                    if (move.isCapture()) {
                        move = mv(line, true);
                    }
                    if (!getBoard().isLegal(move)) {
                        continue;
                    }
                    if (move == null) {
                        _game.reportError("Move cannot "
                                + "be null");
                        continue;
                    }
                }
            }
            return line;
        }
    }

    @Override
    Player create(Piece piece, Game game) {
        return new HumanPlayer(piece, game);
    }

    @Override
    boolean isManual() {
        return true;
    }

    /** side. */
    private Piece _side;

    /** game. */
    private Game _game;


}
