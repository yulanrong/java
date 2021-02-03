package enigma;

import static enigma.EnigmaException.error;

/** Class that represents a reflector in the enigma.
 *  @author Yulan Rong
 */
class Reflector extends FixedRotor {

    /** A non-moving rotor named NAME whose permutation at the 0 setting
     * is PERM. */
    Reflector(String name, Permutation perm) {
        super(name, perm);
        if (!perm.derangement()) {
            throw new EnigmaException("reflector must be mapping");
        }
    }

    @Override
    int convertBackward(int e) {
        throw new EnigmaException("reflector only map once.");
    }
    @Override
    boolean reflecting() {
        return true;
    }

    @Override
    void set(int posn) {
        if (posn != 0) {
            throw error("reflector has only one position");
        }
    }

}
