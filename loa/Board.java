/* Skeleton Copyright (C) 2015, 2020 Paul N. Hilfinger and the Regents of the
 * University of California.  All rights reserved. */
package loa;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;

import java.util.regex.Pattern;

import static loa.Piece.*;
import static loa.Square.*;

/** Represents the state of a game of Lines of Action.
 *  @author Yulan Rong
 */
class Board {

    /** Default number of moves for each side that results in a draw. */
    static final int DEFAULT_MOVE_LIMIT = 60;

    /** Pattern describing a valid square designator (cr). */
    static final Pattern ROW_COL = Pattern.compile("^[a-h][1-8]$");

    /** A Board whose initial contents are taken from INITIALCONTENTS
     *  and in which the player playing TURN is to move. The resulting
     *  Board has
     *        get(col, row) == INITIALCONTENTS[row][col]
     *  Assumes that PLAYER is not null and INITIALCONTENTS is 8x8.
     *
     *  CAUTION: The natural written notation for arrays initializers puts
     *  the BOTTOM row of INITIALCONTENTS at the top.
     */
    Board(Piece[][] initialContents, Piece turn) {
        initialize(initialContents, turn);
    }

    /** A new board in the standard initial position. */
    Board() {
        this(INITIAL_PIECES, BP);
    }

    /** A Board whose initial contents and state are copied from
     *  BOARD. */
    Board(Board board) {
        this();
        copyFrom(board);
    }

    /** Set my state to CONTENTS with SIDE to move. */
    void initialize(Piece[][] contents, Piece side) {
        _winner = null;
        _winnerKnown = false;
        _moves.clear();
        _turn = side;
        _moveLimit = DEFAULT_MOVE_LIMIT;
        for (int i = 0; i < BOARD_SIZE; i += 1) {
            for (int j = 0; j < BOARD_SIZE; j += 1) {
                _board[i * j] = EMP;
            }
        }
        for (int i = 0; i < BOARD_SIZE; i += 1) {
            for (int j = 0; j < BOARD_SIZE; j += 1) {
                set(sq(j, i), contents[i][j]);
            }
        }
        _subsetsInitialized = false;
    }

    /** Set me to the initial configuration. */
    void clear() {
        initialize(INITIAL_PIECES, BP);
    }

    /** Set my state to a copy of BOARD. */
    void copyFrom(Board board) {
        if (board == this) {
            return;
        }
        Piece[][] b = new Piece[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i += 1) {
            for (int j = 0; j < BOARD_SIZE; j += 1) {
                b[i][j] = board._board[sq(j, i).index()];
            }
        }
        this.initialize(b, board._turn);
        _winner = board._winner;
        _turn = board._turn;
        _moveLimit = board._moveLimit;
        _moves.clear();
        _moves.addAll(board._moves);

    }

    /** Return the contents of the square at SQ. */
    Piece get(Square sq) {
        return _board[sq.index()];
    }

    /** Set the square at SQ to V and set the side that is to move next
     *  to NEXT, if NEXT is not null. */
    void set(Square sq, Piece v, Piece next) {
        if (v == BP || v == WP || v == EMP) {
            _board[sq.index()] = v;
        } else {
            throw new IllegalArgumentException("illegal piece.");
        }
        if (next != null) {
            if (next == BP || next == WP || next == EMP) {
                _turn = next;
            } else {
                throw new IllegalArgumentException("illegal piece.");
            }
        }
    }

    /** Set the square at SQ to V, without modifying the side that
     *  moves next. */
    void set(Square sq, Piece v) {
        set(sq, v, null);
    }

    /** Set limit on number of moves by each side that results in a tie to
     *  LIMIT, where 2 * LIMIT > movesMade(). */
    void setMoveLimit(int limit) {
        if (2 * limit <= movesMade()) {
            throw new IllegalArgumentException("move limit too small");
        }
        _moveLimit = 2 * limit;
    }

    /** Assuming isLegal(MOVE), make MOVE. Assumes MOVE.isCapture()
     *  is false. */
    void makeMove(Move move) {
        if (!isLegal(move)) {
            return;
        }
        if (gameOver()) {
            return;
        }
        Square from = move.getFrom();
        Square to = move.getTo();
        _moves.add(move);
        if (_board[to.index()].equals(turn().opposite())) {
            _moveCapture = true;
            set(to, EMP);
        }
        if (turn().equals(BP)) {
            set(to, BP);
        } else {
            set(to, WP);
        }
        set(from, EMP);
        _subsetsInitialized = false;
        _visited[from.col()][from.row()] = false;
        _visited[to.col()][to.row()] = false;
        if (!gameOver()) {
            _turn = _turn.opposite();
            _winnerKnown = false;
        } else {
            _winner = _turn;
            _winnerKnown = true;
        }
    }


    /** Retract (unmake) one move, returning to the state immediately before
     *  that move.  Requires that movesMade () > 0. */
    void retract() {
        assert movesMade() > 0;
        Move move = _moves.remove(_moves.size() - 1);
        Square from = move.getFrom();
        Square to = move.getTo();
        if (!_moveCapture) {
            set(to, EMP);
        } else {
            set(to, turn());
        }
        set(from, _turn.opposite());

        _turn = _turn.opposite();
        _winner = null;
        _winnerKnown = false;
        _subsetsInitialized = false;
    }

    /** Return the Piece representing who is next to move. */
    Piece turn() {
        return _turn;
    }

    /** Return true iff FROM - TO is a legal move for the player currently on
     *  move. */
    boolean isLegal(Square from, Square to) {
        int fc = from.col();
        int fr = from.row();
        int tc = to.col();
        int tr = to.row();
        if (!Square.exists(fc, fr)
                || !Square.exists(tc, tr)) {
            return false;
        }

        if (!get(from).equals(turn())) {
            return false;
        }
        if (!from.isValidMove(to)) {
            return false;
        }
        int d = from.direction(to);
        int d1 = to.direction(from);
        int count = 0;
        for (int i = 1; i < Square.BOARD_SIZE; i += 1) {
            if (from.moveDest(d, i) != null) {
                if (get(from.moveDest(d, i)) != EMP) {
                    count += 1;
                }
            }
            if (from.moveDest(d1, i) != null) {
                if (get(from.moveDest(d1, i)) != EMP) {
                    count += 1;
                }
            }
        }
        if (from.distance(to) != count + 1) {
            return false;
        }
        if (blocked(from, to)) {
            return false;
        }
        return true;
    }

    /** Return true iff MOVE is legal for the player currently on move.
     *  The isCapture() property is ignored. */
    boolean isLegal(Move move) {
        return isLegal(move.getFrom(), move.getTo());
    }

    /** Return a sequence of all legal moves from this position. */
    List<Move> legalMoves() {
        _allWhite = new ArrayList<>();
        _allBlack = new ArrayList<>();
        for (int i = 0; i < BOARD_SIZE; i += 1) {
            for (int j = 0; j < BOARD_SIZE; j += 1) {
                if (_board[sq(j, i).index()].equals(WP)) {
                    legalHelper(i, j, _allWhite);
                } else if (_board[sq(j, i).index()].equals(BP)) {
                    legalHelper(i, j, _allBlack);
                }

            }
        }
        if (turn().equals(WP)) {
            return _allWhite;
        } else {
            return _allBlack;
        }
    }


    /** helper function for legal moves.
     * @param i  row index.
     * @param j column index.
     * @param allMoves all legal moves.
     */
    private void legalHelper(int i, int j, ArrayList<Move> allMoves) {
        for (int r = 0; r < BOARD_SIZE; r += 1) {
            if (isLegal(sq(j, i), sq(j, r))) {
                allMoves.add(Move.mv(sq(j, i), sq(j, r)));
            }
        }
        for (int c = 0; c < BOARD_SIZE; c += 1) {
            if (isLegal(sq(j, i), sq(c, i))) {
                allMoves.add(Move.mv(sq(j, i), sq(c, i)));
            }
        }
        int dr = i;
        int dc = j;
        while (dc - 1 >= 0 && dr - 1 >= 0) {
            if (isLegal(sq(j, i), sq(dc - 1, dr - 1))) {
                allMoves.add(Move.mv(sq(j, i),
                        sq(dc - 1, dr - 1)));
            }
            dr--;
            dc--;
        }
        int dr1 = i;
        int dc1 = j;
        while (dc1 + 1 < BOARD_SIZE && dr1 + 1 < BOARD_SIZE) {
            if (isLegal(sq(j, i), sq(dc1 + 1, dr1 + 1))) {
                allMoves.add(Move.mv(sq(j, i),
                        sq(dc1 + 1, dr1 + 1)));
            }
            dr1++;
            dc1++;
        }
        int dr2 = i;
        int dc2 = j;
        while (dc2 + 1 < BOARD_SIZE && dr2 - 1 >= 0) {
            if (isLegal(sq(j, i), sq(dc2 + 1, dr2 - 1))) {
                allMoves.add(Move.mv(sq(j, i),
                        sq(dc2 + 1, dr2 - 1)));
            }
            dr2--;
            dc2++;
        }
        int dr3 = i;
        int dc3 = j;
        while (dr3 + 1 < BOARD_SIZE && dc3 - 1 >= 0) {
            if (isLegal(sq(j, i), sq(dc3 - 1, dr3 + 1))) {
                allMoves.add(Move.mv(sq(j, i),
                        sq(dc3 - 1, dr3 + 1)));
            }
            dr3++;
            dc3--;
        }
    }

    /** Return true iff the game is over (either player has all his
     *  pieces continguous or there is a tie). */
    boolean gameOver() {
        return winner() != null;
    }

    /** Return true iff SIDE's pieces are continguous. */
    boolean piecesContiguous(Piece side) {
        return getRegionSizes(side).size() == 1;
    }

    /** Return the winning side, if any.  If the game is not over, result is
     *  null.  If the game has ended in a tie, returns EMP. */
    Piece winner() {
        if (!_winnerKnown) {
            if (piecesContiguous(turn())) {
                _winner = _turn;
                return _turn;
            }
            if (piecesContiguous(turn()) && piecesContiguous(turn().opposite())) {
                _winner = _turn;
                return _turn;
            }
            if (legalMoves().isEmpty() && !piecesContiguous(turn())) {
                _winner = _turn.opposite();
            }
            if (piecesContiguous(turn().opposite())
                    && !piecesContiguous(turn())) {
                _winner = _turn.opposite();
            }
            if (_moves.size() == _moveLimit) {
                _winner = EMP;
            }
            _winnerKnown = true;
        }
        return _winner;
    }

    /** Return the total number of moves that have been made (and not
     *  retracted).  Each valid call to makeMove with a normal move increases
     *  this number by 1. */
    int movesMade() {
        return _moves.size();
    }

    @Override
    public boolean equals(Object obj) {
        Board b = (Board) obj;
        return Arrays.deepEquals(_board, b._board) && _turn == b._turn;
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(_board) * 2 + _turn.hashCode();
    }

    @Override
    public String toString() {
        Formatter out = new Formatter();
        out.format("===%n");
        for (int r = BOARD_SIZE - 1; r >= 0; r -= 1) {
            out.format("    ");
            for (int c = 0; c < BOARD_SIZE; c += 1) {
                out.format("%s ", get(sq(c, r)).abbrev());
            }
            out.format("%n");
        }
        out.format("Next move: %s%n===", turn().fullName());
        return out.toString();
    }

    /** Return true if a move from FROM to TO is blocked by an opposing
     *  piece or by a friendly piece on the target square. */
    private boolean blocked(Square from, Square to) {
        if (get(from).equals(get(to))) {
            return true;
        }
        if (get(from).equals(EMP)) {
            return true;
        }
        int d = from.direction(to);
        int steps = from.distance(to);
        for (int i = 1; i < steps; i += 1) {
            if (get(from.moveDest(d, i)).equals(get(from).opposite())) {
                return true;
            }
        }
        return false;
    }

    /** Return the size of the as-yet unvisited cluster of squares
     *  containing P at and adjacent to SQ.  VISITED indicates squares that
     *  have already been processed or are in different clusters.  Update
     *  VISITED to reflect squares counted. */
    private int numContig(Square sq, boolean[][] visited, Piece p) {
        if (!get(sq).equals(p) || p.equals(EMP)) {
            return 0;
        }
        if (_visited[sq.col()][sq.row()]) {
            return 0;
        }
        _visited[sq.col()][sq.row()] = true;
        int count = 1;
        for (Square square : sq.adjacent()) {
            count += numContig(square, _visited, p);
        }
        return count;
    }

    /** Set the values of _whiteRegionSizes and _blackRegionSizes. */
    private void computeRegions() {
        if (_subsetsInitialized) {
            return;
        }
        _whiteRegionSizes.clear();
        _blackRegionSizes.clear();
        _visited = new boolean[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (_board[sq(j, i).index()].equals(WP)) {
                    int wconnected;
                    wconnected = numContig(sq(j, i), _visited, WP);
                    if (wconnected != 0) {
                        _whiteRegionSizes.add(wconnected);
                    }
                } else if (_board[sq(j, i).index()].equals(BP)) {
                    int connected;
                    connected = numContig(sq(j, i), _visited, BP);
                    if (connected != 0) {
                        _blackRegionSizes.add(connected);
                    }
                }

            }
        }

        Collections.sort(_whiteRegionSizes, Collections.reverseOrder());
        Collections.sort(_blackRegionSizes, Collections.reverseOrder());
        _subsetsInitialized = true;
    }

    /** Return the sizes of all the regions in the current union-find
     *  structure for side S. */
    List<Integer> getRegionSizes(Piece s) {
        computeRegions();
        if (s == WP) {
            return _whiteRegionSizes;
        } else {
            return _blackRegionSizes;
        }
    }


    /** The standard initial configuration for Lines of Action (bottom row
     *  first). */
    static final Piece[][] INITIAL_PIECES = {
        { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP }
    };

    /** Current contents of the board.  Square S is at _board[S.index()]. */
    private final Piece[] _board = new Piece[BOARD_SIZE  * BOARD_SIZE];

    /** List of all unretracted moves on this board, in order. */
    private final ArrayList<Move> _moves = new ArrayList<>();
    /** Current side on move. */
    private Piece _turn;
    /** Limit on number of moves before tie is declared.  */
    private int _moveLimit;
    /** True iff the value of _winner is known to be valid. */
    private boolean _winnerKnown;
    /** Cached value of the winner (BP, WP, EMP (for tie), or null (game still
     *  in progress).  Use only if _winnerKnown. */
    private Piece _winner;

    /** True iff subsets computation is up-to-date. */
    private boolean _subsetsInitialized = false;

    /** List of the sizes of continguous clusters of pieces, by color. */
    private final ArrayList<Integer>
        _whiteRegionSizes = new ArrayList<>(),
        _blackRegionSizes = new ArrayList<>();

    /** true if capture. */
    private boolean _moveCapture;
    /** legal moves for White. */
    private ArrayList<Move> _allWhite = new ArrayList<>();
    /** legal moves for Black. */
    private ArrayList<Move> _allBlack = new ArrayList<>();

    /** boolean of visited squares. */
    private boolean[][] _visited = new boolean[BOARD_SIZE][BOARD_SIZE];
}
