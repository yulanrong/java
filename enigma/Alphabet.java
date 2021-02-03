package enigma;

import java.util.ArrayList;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Yulan R
 */
class Alphabet {

    /** A new alphabet containing CHARS.  Character number #k has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        if (chars.length() == 0) {
            throw new EnigmaException("Alphabet cannot be empty.");
        }
        for (char c : chars.toCharArray()) {
            if (Character.isDigit(c)
                    || Character.isLetter(c)
                    || c == '_' || c == '.') {
                if (_charArray.contains(c)) {
                    throw new EnigmaException(
                            "No character may be duplicated.");
                } else {
                    _charArray.add(c);
                }
            }
        }
        _chars = _charArray.toString()
                .substring(1, 3 * _charArray.size() - 1)
                .replaceAll(", ", "");
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _chars.length();
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        for (int i = 0; i < _chars.length(); i += 1) {
            if (_chars.charAt(i) == ch) {
                return true;
            }
        }
        return false;
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        if (index >= 0 && index < size()) {
            return _chars.charAt(index);
        } else {
            throw new EnigmaException(
                    "character number INDEX not in the alphabet");
        }
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        if (contains(ch)) {
            return _chars.indexOf(ch);
        } else {
            throw new EnigmaException("character not in the alphabet.");
        }
    }

    /** String to store characters. */
    private String _chars;

    /** Array to store letters. */
    private ArrayList<Character> _charArray = new ArrayList<>();

}
