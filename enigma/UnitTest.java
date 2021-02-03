package enigma;

import org.junit.Test;
import ucb.junit.textui;

import java.util.HashSet;
import static org.junit.Assert.assertEquals;
import static enigma.TestUtils.NAVALB;
import static enigma.TestUtils.NAVALZ;
import static enigma.TestUtils.NAVALA;

/** The suite of all JUnit tests for the enigma package.
 *  @author Yulan Rong
 */
public class UnitTest {

    /** Run the JUnit tests in this package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        System.exit(textui.runClasses(PermutationTest.class,
                                      MovingRotorTest.class));
    }

    /** testing Alphabet. */
    @Test
    public void testAlphabet() {
        Alphabet alphabet = new Alphabet("ABCD");
        assertEquals(alphabet.size(), 4);
        Alphabet alphabet1 = new Alphabet("abCd");
        Alphabet alphabet2 = new Alphabet("ab?c.");
        assertEquals(alphabet2.size(), 3);
    }
    @Test(expected = EnigmaException.class)
    public void testAlphabetError() {
        Alphabet alphabet = new Alphabet("ABCDD");
        Alphabet alphabet1 = new Alphabet("ab?c.");
        assertEquals(alphabet1.size(), 5);
    }



    @Test
    public void testInsertRotor() {
        Alphabet alphabet = new Alphabet();
        HashSet<Rotor> testRotors = new HashSet<>();
        String cycle1 = String.valueOf(NAVALB.get("I"));
        String cycle2 = String.valueOf(NAVALB.get("II"));
        String cycle3 = String.valueOf(NAVALB.get("V"));
        String cycle4 = String.valueOf(NAVALB.get("Beta"));
        String cycle5 = String.valueOf(NAVALB.get("C"));
        testRotors.add(new MovingRotor("I",
                new Permutation(cycle1, alphabet), "A"));
        testRotors.add(new MovingRotor("II",
                new Permutation(cycle2, alphabet), "A"));
        testRotors.add(new MovingRotor("V",
                new Permutation(cycle3, alphabet), "A"));
        testRotors.add(new FixedRotor("Beta",
                new Permutation(cycle4, alphabet)));
        testRotors.add(new Reflector("C",
                new Permutation(cycle5, alphabet)));
        Machine machine = new Machine(alphabet, 5, 3, testRotors);
        String[] rotors = {"C", "Beta", "V", "II", "I"};
        machine.insertRotors(rotors);
    }
    @Test(expected = EnigmaException.class)
    public void testInsertRotorError() {
        Alphabet alphabet = new Alphabet();
        HashSet<Rotor> testRotors = new HashSet<>();
        String cycle1 = String.valueOf(NAVALB.get("I"));
        String cycle2 = String.valueOf(NAVALB.get("II"));
        String cycle3 = String.valueOf(NAVALB.get("V"));
        String cycle4 = String.valueOf(NAVALB.get("Beta"));
        String cycle5 = String.valueOf(NAVALB.get("C"));
        testRotors.add(new MovingRotor("I",
                new Permutation(cycle1, alphabet), "A"));
        testRotors.add(new MovingRotor("II",
                new Permutation(cycle2, alphabet), "A"));
        testRotors.add(new MovingRotor("V",
                new Permutation(cycle3, alphabet), "A"));
        testRotors.add(new FixedRotor("Beta",
                new Permutation(cycle4, alphabet)));
        testRotors.add(new Reflector("C",
                new Permutation(cycle5, alphabet)));
        Machine machine1 = new Machine(alphabet, 5, 3, testRotors);
        String[] rotors = {"C", "Beta", "V", "V", "V"};
        machine1.insertRotors(rotors);
        Machine machine2 = new Machine(alphabet, 5, 3, testRotors);
        String[] rotors1 = {"I", "Beta", "I", "II", "V"};
        machine2.insertRotors(rotors1);
    }

    @Test(expected = EnigmaException.class)
    public void testSetRotorError() {
        Alphabet alphabet = new Alphabet();
        HashSet<Rotor> testRotors = new HashSet<>();
        String cycle1 = String.valueOf(NAVALZ.get("I"));
        String cycle2 = String.valueOf(NAVALZ.get("II"));
        String cycle3 = String.valueOf(NAVALZ.get("V"));
        String cycle4 = String.valueOf(NAVALZ.get("Beta"));
        String cycle5 = String.valueOf(NAVALZ.get("C"));
        String cycle6 = String.valueOf(NAVALZ.get("IV"));
        testRotors.add(new MovingRotor("I",
                new Permutation(cycle1, alphabet), "A"));
        testRotors.add(new MovingRotor("II",
                new Permutation(cycle2, alphabet), "A"));
        testRotors.add(new MovingRotor("V",
                new Permutation(cycle3, alphabet), "A"));
        testRotors.add(new FixedRotor("Beta",
                new Permutation(cycle4, alphabet)));
        testRotors.add(new Reflector("C",
                new Permutation(cycle5, alphabet)));
        testRotors.add(new Reflector("IV",
                new Permutation(cycle6, alphabet)));
        Machine machine1 = new Machine(alphabet, 5, 3, testRotors);
        String[] rotors = {"C", "IV", "I", "II", "V"};
        machine1.insertRotors(rotors);
        machine1.setRotors("ABCD");
        Machine machine2 = new Machine(alphabet, 5, 3, testRotors);
        String[] rotors1 = {"C", "Beta", "I", "II", "V"};
        machine2.insertRotors(rotors1);
        machine2.setRotors("AAA?");
    }

    @Test
    public void simpleTest() {
        Alphabet alphabet = new Alphabet();
        HashSet<Rotor> testRotors = new HashSet<>();
        String cycle1 = String.valueOf(NAVALA.get("I"));
        String cycle2 = String.valueOf(NAVALA.get("III"));
        String cycle3 = String.valueOf(NAVALA.get("IV"));
        String cycle4 = String.valueOf(NAVALA.get("Beta"));
        String cycle5 = String.valueOf(NAVALA.get("B"));
        testRotors.add(new MovingRotor("I",
                new Permutation(cycle1, alphabet), "Q"));
        testRotors.add(new MovingRotor("III",
                new Permutation(cycle2, alphabet), "V"));
        testRotors.add(new MovingRotor("IV",
                new Permutation(cycle3, alphabet), "J"));
        testRotors.add(new FixedRotor("Beta",
                new Permutation(cycle4, alphabet)));
        testRotors.add(new Reflector("B",
                new Permutation(cycle5, alphabet)));
        Machine machine1 = new Machine(alphabet, 5, 3, testRotors);
        String[] rotors = {"B", "Beta", "III", "IV", "I"};
        machine1.insertRotors(rotors);
        machine1.setRotors("AXLE");
        machine1.setPlugboard(
                new Permutation("(YF) (ZH)",
                        new Alphabet("YFZH")));
        machine1.convert("Y");
    }
}


