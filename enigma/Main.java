package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Collection;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Yulan Rong
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine M = readConfig();
        String set = _input.nextLine();
        if (set.substring(0, 1).equals("*")) {
            setUp(M, set);
            while (_input.hasNextLine()) {
                String next = _input.nextLine();
                if (next.startsWith("*")) {
                    setUp(M, next);
                } else if (next.isEmpty()) {
                    printMessageLine(next);
                } else {
                    next = next.replace(" ", "");
                    next = M.convert(next);
                    printMessageLine(next);
                }
            }
        } else {
            throw new EnigmaException("Not start with setting.");
        }
    }


    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            Collection<Rotor> allRotors = new ArrayList<>();
            String config = _config.nextLine();
            if (config.contains("{}|+)(*&^%$#@`~[]=-")) {
                throw new EnigmaException(
                        "The configuration file has the wrong format.");
            }
            _alphabet = new Alphabet(config);
            rotors = _config.nextInt();
            pawls = _config.nextInt();
            _config.nextLine();
            while (_config.hasNext()) {
                allRotors.add(readRotor());
            }
            return new Machine(_alphabet, rotors, pawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String rotorNotches;
            String notches = "";
            String name = _config.next().toUpperCase();
            String rotorType = _config.next();
            while (_config.hasNext("([(]\\S+[)])+")) {
                notches += _config.next().replaceAll(
                        "[)][(]", ") (") + " ";
            }
            Permutation p = new Permutation(notches, _alphabet);
            if (rotorType.charAt(0) == 'M') {
                if (rotorType.length() > 1) {
                    rotorNotches = rotorType.substring(1);
                    return new MovingRotor(name, p, rotorNotches);
                } else {
                    throw new EnigmaException(
                            "Moving rotors should have notch.");
                }
            } else if (rotorType.charAt(0) == 'N') {
                if (rotorType.length() == 1) {
                    return new FixedRotor(name, p);
                } else {
                    throw new EnigmaException("Fixed rotors don't have notch.");
                }
            } else if (rotorType.charAt(0) == 'R') {
                if (rotorType.length() == 1) {
                    return new Reflector(name, p);
                } else {
                    throw new EnigmaException("Reflectors don't have notch.");
                }
            } else {
                throw new EnigmaException("Rotor type is misnamed.");
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        String[] newRotors = new String[M.numRotors()];
        Scanner read = new Scanner(settings);
        if (settings.substring(0, 1).equals("*")) {
            read.next();
            for (int i = 0; i < M.numRotors(); i += 1) {
                newRotors[i] = read.next();
            }
            M.insertRotors(newRotors);
            String cycles = "";
            String firstSetting = read.next();
            if (firstSetting.length() == M.numRotors() - 1) {
                M.setRotors(firstSetting);
                while (read.hasNext("[(]\\w+[)]")) {
                    cycles += read.next() + " ";
                }
            }
            if (read.hasNext()) {
                throw new EnigmaException("Wrong setting for plugboard.");
            } else {
                M.setPlugboard(new Permutation(cycles, _alphabet));
            }

        } else {
            throw new EnigmaException("Setting must start with '*'.");
        }

    }


    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        while (msg.length() >= 5) {
            String sub = msg.substring(0, 5);
            _output.print(sub + " ");
            msg = msg.substring(5);
        }
        _output.println(msg);
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** number of rotors. */
    private int rotors;

    /** number of pawls. */
    private int pawls;

    /** rotors. */
    private String[] _rotors;
}
