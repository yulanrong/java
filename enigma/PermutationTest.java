package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import static enigma.TestUtils.msg;
import static enigma.TestUtils.NAVALA;
import static enigma.TestUtils.NAVALB;
import static enigma.TestUtils.NAVALZ;
import static enigma.TestUtils.UPPER;
import static enigma.TestUtils.UPPER_STRING;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Yulan Rong
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }

    @Test
    public void testInvertChar() {
        Permutation p3 = new Permutation("(BACD)", new Alphabet("BACD"));
        assertEquals('B', p3.invert('A'));
        assertEquals('D', p3.invert('B'));

        Permutation p = new Permutation(NAVALA.get("I"), UPPER);
        assertEquals('U', p.invert('A'));
        assertEquals('S', p.invert('S'));
        assertEquals(p.invert('J'), p.permute('J'));

        Permutation p1 = new Permutation(NAVALB.get("IV"), UPPER);
        assertEquals('G', p1.invert('Z'));
        assertEquals('M', p1.invert('G'));
        assertEquals(p1.invert('C'), p1.permute('C'));

        Permutation p2 = new Permutation(NAVALZ.get("II"), UPPER);
        assertEquals('B', p2.invert('B'));
        assertNotEquals('R', p2.invert('B'));
        assertEquals('T', p2.invert('A'));
    }
    @Test(expected = EnigmaException.class)
    public void testNotInAlphabet() {
        Permutation p = new Permutation("(BACD)", new Alphabet("BACD"));
        p.invert('F');
    }

    @Test(expected = EnigmaException.class)
    public void testBlank() {
        Permutation p = new Permutation("(AB C) (D)", UPPER);
    }

    @Test
    public void testLegalBlank() {
        Permutation p = new Permutation("   (AB)    (C)", UPPER);
    }

    @Test
    public void testPermuteChar() {
        Permutation p = new Permutation("(BACD)", new Alphabet("BACD"));
        assertEquals('C', p.permute('A'));
        assertEquals('B', p.permute('D'));

        Permutation p1 = new Permutation(NAVALB.get("IV"), UPPER);
        assertEquals('D', p1.permute('Z'));
        assertEquals('Z', p1.permute('G'));
        assertEquals(p1.invert('J'), p1.permute('J'));

        Permutation p2 = new Permutation(NAVALZ.get("II"), UPPER);
        assertEquals('B', p2.permute('B'));
        assertEquals('F', p2.permute('A'));
        assertEquals(p2.invert('C'), p2.permute('C'));

        Permutation p3 = new Permutation(NAVALA.get("I"), UPPER);
        assertEquals('E', p3.permute('A'));
        assertEquals('S', p3.permute('S'));
        assertEquals(p3.invert('J'), p3.permute('J'));
        assertEquals('A', p3.permute('U'));
    }

    @Test
    public void testDerangement() {
        Permutation p = new Permutation("(ABC) (DE)", new Alphabet("ABCDEF"));
        assertFalse(p.derangement());

        Permutation p1 = new Permutation("(ABC) (DEF)", new Alphabet("ABCDEF"));
        assertTrue(p1.derangement());
    }
}
