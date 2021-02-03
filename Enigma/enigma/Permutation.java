package enigma;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Yulan Rong
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        cycles = cycles.trim();
        _cycles = cycles;
        _arrayCycles = _cycles.split("\\)\\s+\\(");
        for (int i = 0; i < _arrayCycles.length; i += 1) {
            _arrayCycles[i] = _arrayCycles[i].replaceAll("[()]", "");
            _arrayCycles[i].trim();
            if (_arrayCycles[i].contains(" ")) {
                throw new EnigmaException(
                        "White space between cycle is not allowed.");
            }
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        _cycles +=  cycle;
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        return _alphabet.toInt(permute(_alphabet.toChar(wrap(p))));
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        return _alphabet.toInt(invert(_alphabet.toChar(wrap(c))));
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        if (!alphabet().contains(p)) {
            throw new EnigmaException("this character is not on alphabet.");
        }
        for (int i = 0; i < _arrayCycles.length; i += 1) {
            if (_arrayCycles[i].length() > 1) {
                for (int j = 0; j < _arrayCycles[i].length(); j += 1) {
                    if (j == _arrayCycles[i].indexOf(p)) {
                        if (j == _arrayCycles[i].length() - 1) {
                            return _arrayCycles[i].charAt(0);
                        } else {
                            return _arrayCycles[i].charAt(j + 1);
                        }
                    }
                }
            }
        }
        return p;
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        if (!alphabet().contains(c)) {
            throw new EnigmaException("this character is not on alphabet.");
        }
        for (int i = 0; i < _arrayCycles.length; i += 1) {
            if (_arrayCycles[i].length() > 1) {
                for (int j = 0; j < _arrayCycles[i].length(); j += 1) {
                    int last = _arrayCycles[i].length();
                    if (j == _arrayCycles[i].indexOf(c)) {
                        if (j == 0) {
                            return _arrayCycles[i].charAt(last - 1);
                        } else {
                            return _arrayCycles[i].charAt(j - 1);
                        }
                    }
                }
            }
        }
        return c;
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        int len = 0;
        for (int i = 0; i < _arrayCycles.length; i += 1) {
            len += _arrayCycles[i].length();
        }
        return len == alphabet().size();
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** String to store cycles. */
    private String _cycles;

    /** ArrayList of string type of cycles. */
    private String[] _arrayCycles;
}
