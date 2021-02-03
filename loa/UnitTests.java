/* Skeleton Copyright (C) 2015, 2020 Paul N. Hilfinger and the Regents of the
 * University of California.  All rights reserved. */
package loa;

import ucb.junit.textui;
import org.junit.Test;
import static org.junit.Assert.*;

/** The suite of all JUnit tests for the loa package.
 *  @author Yulan Rong
 */
public class UnitTests {

    /** Run the JUnit tests in the loa package. */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTests.class);
        textui.runClasses(BoardTest.class);
    }

    /** A dummy test to avoid complaint. */
    @Test
    public void simpleTest() {
        Board board = new Board();
        System.out.println(board);
        board.makeMove(Move.mv("b1-d3"));
        System.out.println(board);
        board.makeMove(Move.mv("a3-d3"));
        System.out.println(board);
        board.makeMove(Move.mv("f1-a1"));
        System.out.println(board);
        assertEquals(board.movesMade(), 3);
    }
    @Test
    public void testRetract() {
        Board board = new Board();
        board.makeMove(Move.mv("b1-d3"));
        board.retract();
        System.out.println(board);
        assertEquals(board.movesMade(), 0);
        board.makeMove(Move.mv("b1-d3"));
        System.out.println(board);
        board.makeMove(Move.mv("a3-d3"));
        board.retract();
        System.out.println(board);
        assertEquals(board.movesMade(), 1);
    }
    @Test
    public void testLegal() {
        Board board = new Board();
        System.out.println(board.legalMoves());
        board.makeMove(Move.mv("b1-d3"));
        System.out.println(board);
        assertFalse(board.isLegal(Move.mv("a2-a3")));
        assertFalse(board.isLegal(Move.mv("d3-d6")));
        assertFalse(board.isLegal(Move.mv("a2-d2")));
        board.makeMove(Move.mv("a2-c2"));
        System.out.println(board);
        assertFalse(board.isLegal(Move.mv("c1-c4")));
        assertFalse(board.isLegal(Move.mv("c1-c3")));
        assertTrue(board.isLegal(Move.mv("d1-d4")));
        board.makeMove(Move.mv("d1-d4"));
        System.out.println(board);
    }
    @Test
    public void testRegion() {
        Board board = new Board();
        assertEquals(board.getRegionSizes(Piece.BP).size(), 2);
        assertEquals(board.getRegionSizes(Piece.WP).size(), 2);
        board.makeMove(Move.mv("b1-d3"));
        System.out.println(board);
        System.out.println(board.getRegionSizes(Piece.BP));
        board.makeMove(Move.mv("a2-c2"));
        System.out.println(board);
        System.out.println(board.getRegionSizes(Piece.BP));
        System.out.println(board.getRegionSizes(Piece.WP));
        board.retract();
        System.out.println(board);
        System.out.println(board.getRegionSizes(Piece.WP));

    }
    @Test
    public void testLimit() {
        Board board = new Board();
        board.setMoveLimit(5);
        board.makeMove(Move.mv("d8-d6"));
        board.makeMove(Move.mv("a3-c3"));
        board.makeMove(Move.mv("d1-f3"));
        board.makeMove(Move.mv("a6-c8"));
        board.makeMove(Move.mv("f8-f5"));
        board.makeMove(Move.mv("a2-c2"));
        board.makeMove(Move.mv("f1-a1"));
        board.makeMove(Move.mv("a5-b6"));
        board.makeMove(Move.mv("c1-e3"));
        board.makeMove(Move.mv("h6-e6"));
        assertTrue(board.gameOver());

    }

}


