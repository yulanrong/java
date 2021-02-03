package enigma;

import java.util.ArrayList;
import java.util.Collection;

/** Class that represents a complete enigma machine.
 *  @author Yulan Rong
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors.toArray();
    }


    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        _rotorsArray = new ArrayList<>();
        _name = new ArrayList<>();

        for (int i = 0; i < rotors.length; i += 1) {
            for (int j = 0; j < _allRotors.length; j += 1) {
                String s = rotors[i].toUpperCase();
                if (s.equals(((Rotor) _allRotors[j]).
                        name().toUpperCase())) {
                    ((Rotor) _allRotors[j]).set(0);
                    _rotorsArray.add((Rotor) _allRotors[j]);
                    if (!_name.contains(s)) {
                        _name.add(s);
                    } else {
                        throw new EnigmaException(
                                "A rotor cannot be "
                                        + "repeated in "
                                        + "the setting line.");
                    }
                }
            }
        }
        if (!_rotorsArray.get(0).reflecting()) {
            throw new EnigmaException(
                    "The first rotor must be a reflector");
        }
        if (_rotorsArray.size() != _numRotors) {
            throw new EnigmaException("Number of rotors is not correct.");
        }
    }

    /** return the available rotors in array. */
    Object[] allRotors() {
        return _allRotors;
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != numRotors() - 1) {
            throw new EnigmaException(
                    "Fewer settings than rotors.");
        }
        for (int i = 1; i < _rotorsArray.size(); i += 1) {
            if (i <= _numRotors - _pawls - 1) {
                if (_rotorsArray.get(i).rotates()
                        || _rotorsArray.get(i).reflecting()) {
                    throw new EnigmaException("It should be fixed rotor.");
                }
                if (!_alphabet.contains(setting.charAt(i - 1))) {
                    throw new EnigmaException(
                            "The initial positions "
                                    + "string cannot contain "
                                    + "characters not in the "
                                    + "alphabet.");
                } else {
                    _rotorsArray.get(i).set(setting.charAt(i - 1));
                }
            }
            if (i >= _numRotors - _pawls) {
                if (!_rotorsArray.get(i).rotates()) {
                    throw new EnigmaException("It should be moving rotor.");
                }
                if (!_alphabet.contains(setting.charAt(i - 1))) {
                    throw new EnigmaException(
                            "The initial positions "
                                    + "string cannot contain "
                                    + "characters not in the "
                                    + "alphabet.");
                } else {
                    _rotorsArray.get(i).set(setting.charAt(i - 1));
                }
            }
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing

     *  the machine. */
    int convert(int c) {
        doubleStepping();
        c = c % _alphabet.size();
        if (_plugboard != null) {
            c = _plugboard.permute(c);
        }

        for (int i = _rotorsArray.size() - 1; i >= 0; i -= 1) {
            c = _rotorsArray.get(i).convertForward(c);
        }
        for (int j = 1; j < _rotorsArray.size(); j += 1) {
            c = _rotorsArray.get(j).convertBackward(c);
        }
        if (_plugboard != null) {
            c = _plugboard.permute(c);
        }
        return c;
    }

    /** Helper method for convert which handles advance. */
    private void doubleStepping() {
        boolean[] canRotate = new boolean[_rotorsArray.size()];

        for (int i = _numRotors - _pawls; i < _numRotors; i += 1) {
            if (i == _rotorsArray.size() - 1) {
                canRotate[_rotorsArray.size() - 1] = true;
            }
            if (_rotorsArray.get(i).rotates()) {
                if (_rotorsArray.get(i).atNotch()) {
                    canRotate[i] = true;
                    if (_rotorsArray.get(i - 1).rotates()) {
                        canRotate[i - 1] = true;
                    }
                }
            }
        }
        for (int j = 0; j < _rotorsArray.size(); j += 1) {
            if (canRotate[j]) {
                _rotorsArray.get(j).advance();
            }
        }

    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String message = "";
        for (int i = 0; i < msg.length(); i += 1) {
            int c = convert(_alphabet.toInt(msg.charAt(i)));
            message += _alphabet.toChar(c);
        }
        return message;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of rotors. */
    private int _numRotors;

    /** Number of pawls. */
    private int _pawls;

    /** Object of allRotors. */
    private Object[] _allRotors;

    /** Array of rotors' names and rotors. */
    private ArrayList<Rotor> _rotorsArray;

    /** Permutation of plugboard. */
    private Permutation _plugboard;

    /** name of rotors. */
    private ArrayList<String> _name;

}
