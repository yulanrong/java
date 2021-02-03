/* Skeleton Copyright (C) 2015, 2020 Paul N. Hilfinger and the Regents of the
 * University of California.  All rights reserved. Hinted by
 *  my another project tablut's AI */
package loa;

import java.util.List;

import static loa.Piece.*;

/** An automated Player.
 *  @author Yulan Rong
 */
class MachinePlayer extends Player {

    /** A position-score magnitude indicating a win (for white if positive,
     *  black if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 20;
    /** A position-score magnitude indicating a forced win. */
    private static final int WILL_WIN_VALUE = Integer.MAX_VALUE - 40;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;

    /** A new MachinePlayer with no piece or controller (intended to produce
     *  a template). */
    MachinePlayer() {
        this(null, null);
    }

    /** A MachinePlayer that plays the SIDE pieces in GAME. */
    MachinePlayer(Piece side, Game game) {
        super(side, game);
    }

    @Override
    String getMove() {
        Move choice;

        assert side() == getGame().getBoard().turn();
        int depth;
        choice = searchForMove();
        getGame().reportMove(choice);
        return choice.toString();
    }

    @Override
    Player create(Piece piece, Game game) {
        return new MachinePlayer(piece, game);
    }

    @Override
    boolean isManual() {
        return false;
    }

    /** Return a move after searching the game tree to DEPTH>0 moves
     *  from the current position. Assumes the game is not over. */
    private Move searchForMove() {
        Board work = new Board(getBoard());
        int value;
        assert side() == work.turn();
        _foundMove = null;
        if (side() == WP) {
            findMove(work, chooseDepth(), true, 1, -INFTY, INFTY);
        } else {
            findMove(work, chooseDepth(), true, -1, -INFTY, INFTY);
        }
        return _foundMove;
    }

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _foundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _foundMove. If the game is over
     *  on BOARD, does not set _foundMove. */
    private int findMove(Board board, int depth, boolean saveMove,
                         int sense, int alpha, int beta) {
        if (board.gameOver()) {
            if (staticScore(board) > 0 && board.winner() == board.turn()) {
                return WINNING_VALUE;
            } else {
                return -WINNING_VALUE;
            }
        }
        if (depth == 0) {
            return staticScore(board);
        }
        int bestSoFar = 0;
        List<Move> moves = board.legalMoves();
        for (Move m : moves) {
            if (sense == 1) {
                bestSoFar = minMax(board, m,
                        depth, 1, alpha, beta);
                alpha = Math.max(bestSoFar, beta);
                _move = m;
                if (beta <= alpha) {
                    break;
                }
            } else if (sense == -1) {
                bestSoFar = minMax(board, m,
                        depth, -1, alpha, beta);
                beta = Math.min(bestSoFar, beta);
                _move = m;
                if (beta <= alpha) {
                    break;
                }
            }
        }


        if (saveMove) {
            _foundMove = _move;
        }
        return bestSoFar;
    }

    /** Return a search depth for the current position. */
    private int chooseDepth() {
        return 2;
    }

    /** minmax function to find a best move, basically from lecture code.
     * @param board input from Board.
     * @param move Move.
     * @param depth same as findMove.
     * @param alpha same as findMove.
     * @param beta same as findMove.
     * @param sense same as findMove.
     * @return bestmove.
     */
    private int minMax(Board board, Move move, int depth,
                       int sense, int alpha, int beta) {
        int bestmove = 0;
        Board b = new Board(board);
        b.makeMove(move);
        if (board.winner() != null
                && b.legalMoves() != null) {
            if (sense == 1) {
                return WILL_WIN_VALUE;
            } else if (sense == -1) {
                return -WILL_WIN_VALUE;
            }
        } else if (board.winner() != null && sense == 1) {
            return INFTY;
        } else if (board.winner() != null && sense == -1) {
            return -INFTY;
        }
        bestmove = findMove(b, depth - 1, false,
                sense * (-1), alpha, beta);

        return bestmove;
    }

    /** Return a heuristic value for BOARD when depth = 0.
     * @param board the current board.
     * @return score */
    private int staticScore(Board board) {
        int myscore = 0;
        int yourscore = 0;
        int diff;
        if (myscore != yourscore) {
            diff = myscore - yourscore;
        } else {
            myscore = board.getRegionSizes(board.turn()).size();
            yourscore = board.getRegionSizes(board.turn().opposite()).size();
            if (myscore == yourscore) {
                myscore = 0;
                yourscore = 0;
                for (int i = 0; i < Square.BOARD_SIZE; i++) {
                    for (int j = 0; j < Square.BOARD_SIZE; j++) {
                        if (board.get(Square.sq(j, i)).equals(board.turn())) {
                            myscore++;
                        } else {
                            yourscore++;
                        }
                    }
                }
            }
            diff = yourscore - myscore;
        }

        return diff;
    }


    /** Used to convey moves discovered by findMove. */
    private Move _foundMove;

    /** available moves. */
    private Move _move;

}