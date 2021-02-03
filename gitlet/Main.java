package gitlet;


import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author yulan, tianqi and jane.
 */
public class Main implements Serializable {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args)
            throws IOException, ClassNotFoundException {
        Gitlet gitlet = new Gitlet();
        checkFormat(args);
        if (args[0].equals("init")) {
            gitlet.init();
        } else if (!REPO.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        } else {
            gitlet.readGit();
        }
        if (args[0].equals("add")) {
            gitlet.add(args[1]);
        }
        if (args[0].equals("commit")) {
            if (args.length == 1) {
                System.out.println("Please enter a commit message.");
                return;
            }
            gitlet.commit(args[1]);
        }
        if (args[0].equals("log")) {
            gitlet.log();
        }
        if (args[0].equals("rm")) {
            gitlet.rm(args[1]);
        }
        if (args[0].equals("global-log")) {
            gitlet.globalLog();
        }
        if (args[0].equals("find")) {
            gitlet.find(args[1]);
        }
        if (args[0].equals("status")) {
            gitlet.status();
        }
        if (args[0].equals("branch")) {
            gitlet.branch(args[1]);
        }
        if (args[0].equals("checkout")) {
            gitlet.checkout(args);
        }
        if (args[0].equals("rm-branch")) {
            gitlet.rmBranch(args[1]);
        }
        if (args[0].equals("reset")) {
            gitlet.reset(args[1]);
        }
        if (args[0].equals("merge")) {
            gitlet.merge(args[1]);
        }
        Utils.writeContents(new File(".gitlet/gitlet"),
                Utils.serialize(gitlet));
    }

    /** helper function for main.
     * @param args input*/
    private static void helpMain(String...args)
            throws IOException, ClassNotFoundException {
        Gitlet git = new Gitlet();
        git.readGit();
        switch (args[0]) {
        case "add":
            git.add(args[1]);
            break;
        case "commit":
            git.commit(args[1]);
            break;
        case "rm":
            git.rm(args[1]);
            break;
        case "log":
            git.log();
            break;
        case "global-log":
            git.globalLog();
            break;
        case "find":
            git.find(args[1]);
            break;
        case "status":
            git.status();
            break;
        case "checkout":
            git.checkout(args);
            break;
        case "branch":
            git.branch(args[1]);
            break;
        case "rm-branch":
            git.rmBranch(args[1]);
            break;
        case "reset":
            git.reset(args[1]);
            break;
        case "merge":
            git.merge(args[1]);
            break;
        default:
        }
    }



    /** check input format.
     * @param args input. */
    private static void checkFormat(String...args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        if (!allCommands(args[0])) {
            System.out.println("No command with that name exists.");
            System.exit(0);
        }
        switch (args[0]) {
        case "add":
        case "rm":
        case "find":
        case "branch":
        case "rm-branch":
        case "reset":
        case "merge":
            if (args.length != 2) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            break;
        case "commit":
            if (args.length == 1) {
                System.out.println("Please enter a commit message.");
                return;
            }
            if (args.length != 2) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            break;
        case "log":
        case "global-log":
        case "status":
        case "init":
            if (args.length != 1) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            break;
        case "checkout":
            if (args.length > 4 || args.length < 2) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            break;
        default:
        }
    }
    /** boolean to check if command are in commands.
     * @param command input.
     * @return true or false.
     */
    private static boolean allCommands(String command) {
        ArrayList<String> commands = new ArrayList<>();
        commands.add("init");
        commands.add("add");
        commands.add("commit");
        commands.add("log");
        commands.add("global-log");
        commands.add("checkout");
        commands.add("status");
        commands.add("rm");
        commands.add("rm-branch");
        commands.add("find");
        commands.add("merge");
        commands.add("reset");
        commands.add("branch");
        return commands.contains(command);
    }


    /** get the working directory. */
    static final File CWD = new File(System.getProperty("user.dir"));

    /** get the .gitlet directory inside the working directory. */
    static final File REPO = Utils.join(CWD, ".gitlet");

    /** get the stage directory inside the .gitlet directory. */
    static final File STAGE = Utils.join(REPO, "stage");

    /** get the commit directory inside the .gitlet directory. */
    static final File COMMIT = Utils.join(REPO, "commit");

    /** get the blob directory inside the .gitlet directory. */
    static final File BLOB = Utils.join(REPO, "blob");

    /** get the remove directory inside the .gitlet directory. */
    static final File REMOVE = Utils.join(REPO, "remove");
}
