package gitlet;

import java.io.Serializable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/** Gitlet class which stores all the commands of gitlet.
 * @author yulan
 */

public class Gitlet implements Serializable {

    /** Creates a new Gitlet version-control system in the
     * current directory. This system will automatically start
     * with one commit: a commit that contains no files and
     * has the commit message initial commit (just like that,
     * with no punctuation). It will have a single branch:
     * master, which initially points to this initial commit,
     * and master will be the current branch. The timestamp
     * for this initial commit will be
     * 00:00:00 UTC, Thursday, 1 January 1970 in whatever format
     * you choose for dates (this is called "The (Unix) Epoch",
     * represented internally by the time 0.) Since the initial
     * commit in all repositories created by Gitlet will have
     * exactly the same content, it follows that all repositories
     * will automatically share this commit (they will all
     * have the same UID) and all commits in all
     * repositories will trace back to it.
     */
    public void init() throws IOException {
        if (!Main.REPO.exists()) {
            Main.REPO.mkdir();
            Main.COMMIT.mkdir();
            Main.STAGE.mkdir();
            Main.BLOB.mkdir();
            Main.REMOVE.mkdir();
            _dir = "master";
            branch("master");
            Commit initial = new Commit("initial commit",
                    null, null);
            _head = "master";
            _shaID = initial.getID();
            _parent = null;
            _parentID = initial.getID();
            _ids.add(initial.getID());
            _allCommit.put(initial.getID(), initial);
            _allBranch.put(_dir, new ArrayList<>());
            _allBranch.get(_dir).add(initial);
            Utils.writeContents(initial.getSavingCommit(),
                    Utils.serialize(initial));
        } else {
            System.out.println("A Gitlet version-control "
                    + "system already exists "
                    + "in the current directory.");
            System.exit(0);
        }

    }

    /** read the info from existing .gitlet directory.
     * */

    public void readGit()
            throws IOException, ClassNotFoundException {
        File dir = new File(".gitlet/gitlet");
        Gitlet read = Utils.readObject(dir, Gitlet.class);
        _branchMap = read._branchMap;
        _allCommit = read._allCommit;
        _allBranch = read._allBranch;
        _parent = read._parent;
        _parentID = read._parentID;
        _ids = read._ids;
        _allNames = read._allNames;
        _newBranchMap = read._newBranchMap;
        _staging = read._staging;
        _head = read._head;
        _toRemove = read._toRemove;
        _toRemoveBranch = read._toRemoveBranch;
        _blobs = read._blobs;
        _notStaging = read._notStaging;
        _shaID = read._shaID;
        _trackRemove = read._trackRemove;
        _dir = read._dir;
    }



    /** Adds a copy of the file as it currently exists
     *  to the staging area (see the description of the
     *  commit command). For this reason, adding a file
     *  is also called staging the file for addition.
     *  Staging an already-staged file overwrites the
     *  previous entry in the staging area with the new
     *  contents. The staging area should be somewhere
     *  in .gitlet. If the current working version of the
     *  file is identical to the version in the current
     *  commit, do not stage it to be added, and remove
     *  it from the staging area if it is already there
     *  (as can happen when a file is changed, added,
     *  and then changed back). The file will no longer
     *  be staged for removal (see gitlet rm), if it was
     *  at the time of the command.
     * @param fileName the file name
     */
    public void add(String fileName)
            throws IOException, ClassNotFoundException {
        File file = Utils.join(Main.CWD, fileName);
        if (!file.isFile()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        Blob blob = new Blob(fileName);

        if (_toRemove.contains(fileName)) {
            _toRemove.remove(fileName);
            _notStaging.remove(fileName);
            return;
        }
        Blob blob1 = new Blob(fileName);
        HashMap<String, Blob> blobs = new HashMap<>();
        if (!_allBranch.isEmpty()) {
            if (_allBranch.get(_dir).size() >= 1) {
                for (Commit commit : _allBranch.get(_dir)) {
                    blobs.putAll(commit.getBlobsMap());
                }
            }
        }
        for (Blob b : blobs.values()) {
            if (b.getID().equals(blob1.getID())) {
                if (b.getName().equals(blob1.getName())) {
                    return;
                }
            }
        }


        if (_parentID.equals(blob.getID())) {
            File toDelete = Utils.join(Main.STAGE,
                    _staging.get(fileName).getID());
            toDelete.delete();
            _staging.remove(fileName);
        } else {
            if (!_staging.isEmpty()
                    && _staging.containsKey(fileName)) {
                return;
            }
            if (_staging.isEmpty()
                    || !_staging.containsKey(fileName)) {
                _blobs.put(fileName, blob);
                _staging.put(fileName, blob);
                Utils.writeContents(Utils.join(Main.STAGE,
                        blob.getID()), Utils.serialize(blob));
            }
        }

    }


    /** Saves a snapshot of certain files in the
     * current commit and staging area
     * so they can be restored at a later time,
     * creating a new commit. The commit
     * is said to be tracking the saved files.
     * By default, each commit's snapshot
     * of files will be exactly the same as its
     * parent commit's snapshot of files;
     * it will keep versions of files exactly as
     * they are, and not update them.
     * A commit will only update the contents of
     * files it is tracking that have
     * been staged for addition at the time of commit,
     * in which case the commit
     * will now include the version of the file that
     * was staged instead of the
     * version it got from its parent. A commit will
     * save and start tracking any
     * files that were staged for addition but weren't
     * tracked by its parent. Finally, files tracked in
     * the current commit may be untracked in the new
     * commit as a result being staged for removal
     * by the rm command (below).

     The bottom line: By default a commit is the
     same as its parent. Files staged
     for addition and removal are the updates to the commit.

     Some additional points about commit:
     + The staging area is cleared after a commit.
     + The commit command never adds, changes, or
     removes files in the working directory (other than those in
     the .gitlet directory). The rm command will remove
     such files, as well as staging them for removal,
     so that they will be untracked after a commit.
     + Any changes made to files after staging for
     addition or removal are ignored
     by the commit command, which only modifies
     the contents of the .gitlet directory.
     For example, if you remove a tracked file using
     the Unix rm command (rather than
     Gitlet's command of the same name), it has no
     effect on the next commit, which
     will still contain the deleted version of the file.
     + After the commit command, the new commit is added
     as a new node in the commit tree.
     + The commit just made becomes the "current commit",
     and the head pointer now
     points to it. The previous head
     commit is this commit's parent commit.
     + Each commit should contain the date and time it was made.
     + Each commit has a log message associated
     with it that describes the changes
     to the files in the commit. This is specified
     by the user. The entire message
     should take up only one entry in the array
     args that is passed to main.
     To include multiword messages, you'll have
     to surround them in quotes.
     + Each commit is identified by its SHA-1 id,
     which must include the file (blob)
     references of its files, parent reference,
     log message, and commit time.
     * @param message commit message.
     */
    public void commit(String message) {
        if (message.equals("") || message == null) {
            System.out.println("Please enter a commit message.");
            return;
        }
        Commit toCommit = new Commit(message, _parentID, _blobs);
        if (_staging.isEmpty() && _toRemove.isEmpty()
                || _parentID.equals(toCommit.getID())) {
            System.out.println("No changes added to the commit.");
            return;
        }
        _branchMap.put(_dir, toCommit.getID());
        if (!_allBranch.containsKey(_dir)) {
            _allBranch.put(_dir, new ArrayList<>());
        }
        _allBranch.get(_dir).add(toCommit);
        _allCommit.put(toCommit.getID(), toCommit);
        _ids.add(toCommit.getID());
        _parentID = toCommit.getID();
        _parent = toCommit.getParent();
        for (String s : _staging.keySet()) {
            _allNames.add(s);
        }
        Utils.writeContents(Utils.join(Main.COMMIT, toCommit.getID()),
                Utils.serialize(toCommit));
        for (File file : Main.STAGE.listFiles()) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }
        _toRemove.clear();
        _toRemoveBranch.clear();
        if (!_staging.isEmpty()) {
            _staging.clear();
        }
    }


    /** Unstage the file if it is currently
     * staged for addition. If the file is
     * tracked in the current commit, stage it
     * for removal and remove the file
     * from the working directory if the user
     * has not already done so (do not
     * remove it unless it is tracked in the current commit).
     * @param fileName file name.
     */
    public void rm(String fileName) throws IOException {
        File file = Utils.join(Main.CWD, fileName);
        Commit toRemove = Utils.readObject(Utils.join(Main.COMMIT,
                _parentID), Commit.class);
        boolean removed = false;
        if (_staging.containsKey(fileName)) {
            Utils.join(Main.STAGE,
                    _staging.get(fileName).getID()).delete();
            _notStaging.put(fileName, new Blob(fileName));
            _staging.remove(fileName);
            _blobs.remove(fileName);
            removed = true;
        }

        if (toRemove.getBlobsMap().containsKey(fileName)) {
            removed = true;
            _toRemove.add(fileName);
            _toRemoveBranch.add(fileName);
            _blobs.remove(fileName);
            if (_notStaging.containsKey(fileName)) {
                _notStaging.remove(fileName);
            }
            Utils.restrictedDelete(file);
        }
        Utils.writeContents(Utils.join(Main.REMOVE, toRemove.getID()),
                Utils.serialize(toRemove));
        if (!removed) {
            System.out.println("No reason to remove the file.");
            return;
        }

    }

    /** Starting at the current head commit, display
     * information about each commit
     * backwards along the commit tree until the
     * initial commit, following the first
     * parent commit links, ignoring any second
     * parents found in merge commits.
     * (In regular Git, this is what you get with
     * git log --first-parent). This set
     * of commit nodes is called the commit's history.
     * For every node in this history,
     * the information it should display is the commit
     * id, the time the commit was made,
     * and the commit message.
     *
     */
    public void log() {
        ArrayList<String> commitId = new ArrayList<>();
        ArrayList<String> commitTime = new ArrayList<>();
        ArrayList<String> commitMsg = new ArrayList<>();
        String mergeCommit = null;
        for (Commit c : _allBranch.get(_dir)) {
            commitId.add(c.getID());
            commitTime.add(c.getTimestamp());
            commitMsg.add(c.getMessage());
        }
        Collections.reverse(commitId);
        Collections.reverse(commitMsg);
        Collections.reverse(commitTime);
        for (int i = 0; i < commitId.size(); i += 1) {
            System.out.println("===");
            System.out.println("commit " + commitId.get(i));
            if (mergeCommit != null) {
                System.out.println(mergeCommit);
            }
            System.out.println("Date: " + commitTime.get(i));
            System.out.println(commitMsg.get(i));
            System.out.println();
        }
    }


    /** Like log, except displays information
     * about all commits ever made.The order of the
     * commits does not matter.
     */
    public void globalLog() {
        List<String> commits = Utils.plainFilenamesIn(Main.COMMIT);
        Collections.reverse(commits);
        for (String file : commits) {
            Commit c = Utils.readObject(Utils.join(Main.COMMIT,
                    file), Commit.class);
            System.out.println("===");
            System.out.println("commit " + c.getID());
            System.out.println("Date: " + c.getTimestamp());
            System.out.println(c.getMessage());
            System.out.println();
        }

    }

    /** Prints out the ids of all commits
     * that have the given commit message,
     * one per line. If there are multiple
     * such commits, it prints the ids out
     * on separate lines. The commit message
     * is a single operand; to indicate a
     * multiword message, put the operand in
     * quotation marks, as for the commit
     * command below.
     * @param commitMsg the commit message.
     */
    public void find(String commitMsg) {
        List<String> commits = Utils.plainFilenamesIn(Main.COMMIT);
        boolean found = false;
        for (String file : commits) {
            Commit c = Utils.readObject(Utils.join(Main.COMMIT,
                    file), Commit.class);
            if (c.getMessage().equals(commitMsg)) {
                System.out.println(c.getID());
                found = true;
            }
        }
        if (commitMsg == null || !found) {
            System.out.println("Found no commit with that message.");
        }

    }


    /** Displays what branches currently exist, and marks
     *  the current branch with a *. Also displays what files
     *  have been staged for addition or removal.
     */
    public void status() {
        System.out.println("=== Branches ===");
        if (!_branchMap.isEmpty()) {
            ArrayList<String> branches = new ArrayList<>();
            branches.addAll(_branchMap.keySet());
            Collections.reverse(branches);
            for (String s : branches) {
                if (s.equals(_dir)) {
                    System.out.println("*" + _dir);
                } else {
                    System.out.println(s);
                }
            }
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        if (!_staging.isEmpty()) {
            for (String s : _staging.keySet()) {
                System.out.println(s);
            }
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        if (!_toRemove.isEmpty()) {
            for (String s : _toRemove) {
                System.out.println(s);
            }
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    /** helper function to find all the untracked files.
     * @return list of untracked file ids.  */
    private ArrayList<String> findUntracked() {
        List<String> allFiles = Utils.plainFilenamesIn(Main.CWD);
        ArrayList<String> unTrack = new ArrayList<>();
        return unTrack;
    }


    /** Creates a new branch with the given name, and points it at
     * the current head node. A branch is nothing more than a name
     * for a reference (a SHA-1 identifier) to a commit node.
     * This command does NOT immediately switch to the newly
     * created branch (just as in real Git). Before you ever
     * call branch, your code should be running with a default
     * branch called "master".
     * @param bName branch name. */
    public void branch(String bName) {
        if (_branchMap.containsKey(bName)) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        _branchMap.put(bName, _parentID);
        _newBranchMap.put(bName, _parentID);
        _allBranch.put(bName, new ArrayList<>());
    }


    /** Checkout is a kind of general command that can do a few
     * different things depending on what its arguments are.
     * There are 3 possible use cases. In each section below,
     * you'll see 3 bullet points. Each corresponds to the
     * respective usage of checkout.
     * @param args input string. */
    public void checkout(String... args) throws IOException {
        if (args.length == 3 && args[1].equals("--")) {
            checkout1(args[2]);
        } else if (args.length == 4 && args[2].equals("--")) {
            checkout2(args[1], args[3]);
        } else if (args.length == 2) {
            checkout3(args[1]);
        } else {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }

    /** Takes the version of the file as it exists in the head commit,
     * the front of the current branch, and puts it in the working
     * directory, overwriting the version of the file that's already
     * there if there is one. The new version of the file is not staged.
     * @param fileName file name. */
    private void checkout1(String fileName) {
        HashMap<String, Blob> blobs = new HashMap<>();
        for (Commit commit : _allBranch.get(_dir)) {
            blobs.putAll(commit.getBlobsMap());
        }
        for (Blob bb : blobs.values()) {
            if (!bb.getName().equals(fileName)) {
                System.out.println("File does not exist in that commit.");
                System.exit(0);
            }
            File save = new File(fileName);
            Utils.writeContents(save, bb.getContentString());
        }

    }

    /** Takes the version of the file as it exists in the commit
     * with the given id, and puts it in the working directory,
     * overwriting the version of the file that's already there
     * if there is one. The new version of the file is not staged.
     * @param fileName file name
     * @param commitID the first 6 digits of commit id. */
    private void checkout2(String commitID, String fileName) {
        boolean contains = false;
        List<String> commits = Utils.plainFilenamesIn(Main.COMMIT);
        String foundCommit = "";
        ArrayList<String> count = new ArrayList<>();
        for (String c : commits) {
            if (c.matches(commitID)) {
                contains = true;
                count.add(c);
            }
        }

        if (!contains) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        File file = Utils.join(Main.COMMIT, commitID);
        Commit target = Utils.readObject(file, Commit.class);
        HashMap<String, Blob> blobs = new HashMap<>(target.getBlobsMap());
        for (Blob bb : blobs.values()) {
            if (!bb.getName().equals(fileName)) {
                System.out.println("File does not exist in that commit.");
                System.exit(0);
            }
            File save = new File(fileName);
            Utils.writeContents(save, bb.getContentString());
        }

    }

    /** Takes all files in the commit at the head of the given
     * branch, and puts them in the working directory, overwriting
     * the versions of the files that are already there if they
     * exist. Also, at the end of this command, the given branch
     * will now be considered the current branch (HEAD). Any
     * files that are tracked in the current branch but are not
     * present in the checked-out branch are deleted. The staging
     * area is cleared, unless the checked-out branch is the current
     * branch
     * @param bName branch name. */
    private void checkout3(String bName) throws IOException {
        boolean contains = false;
        for (String b : _branchMap.keySet()) {
            if (b.equals(bName)) {
                contains = true;
            }
        }
        if (!contains) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        if (bName.equals(_dir)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        HashMap<String, Commit> currCommits = new HashMap<>();

        _dir = bName;
        _parentID = _branchMap.get(bName);
        _parent = bName;
        Commit bCommits = Utils.readObject(
                Utils.join(Main.COMMIT, _branchMap.get(bName)),
                Commit.class);
        ArrayList<Commit> commits = _allBranch.get(_dir);
        List<String> allFiles = Utils.plainFilenamesIn(Main.CWD);
        for (String fileName : allFiles) {
            Blob file = new Blob(fileName);
            if (!_staging.containsKey(fileName)
                    && !_blobs.containsKey(fileName)
                    && bCommits.getBlobsMap().get(fileName) != null
                    && !fileName.startsWith(".")) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first. ");
                System.exit(0);
            }
        }
        HashMap<String, Blob> currBlobs = new HashMap<>(
                bCommits.getBlobsMap());
        HashMap<String, Blob> overWrite = new HashMap<>();
        for (Blob bb : currBlobs.values()) {
            File save = Utils.join(Main.CWD, bb.getName());
            save.createNewFile();
            Utils.writeContents(save, bb.getContentString());
            overWrite.put(bb.getName(), bb);
        }
        for (String fileName : _blobs.keySet()) {
            if (!currBlobs.containsKey(fileName)) {
                File deleted = Utils.join(Main.CWD, fileName);
                deleted.delete();
            }
        }
        _blobs.clear();
        _blobs.putAll(overWrite);
        for (File stageFile : Objects.requireNonNull(Main.STAGE.listFiles())) {
            Utils.restrictedDelete(stageFile);
        }
        _staging = new HashMap<>();

    }

    /** Checks out all the files tracked by the given commit.
     * Removes tracked files that are not present in that commit.
     * Also moves the current branch's head to that commit node.
     * See the intro for an example of what happens to the head
     * pointer after using reset. The [commit id] may be abbreviated
     * as for checkout. The staging area is cleared. The command is
     * essentially checkout of an arbitrary commit that also changes
     * the current branch head.
     * @param commitID commit id.
     */
    public void reset(String commitID) throws IOException {
        boolean contains = false;
        List<String> commits = Utils.plainFilenamesIn(Main.COMMIT);
        String foundCommit = "";
        for (String c : commits) {
            if (c.matches(commitID)) {
                contains = true;
                foundCommit = c;
            }
        }
        if (!contains) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        File file = Utils.join(Main.COMMIT, commitID);
        Commit target = Utils.readObject(file, Commit.class);
        List<String> allFiles = Utils.plainFilenamesIn(Main.CWD);
        for (String fileName : allFiles) {
            Blob f = new Blob(fileName);
            if (!_staging.containsKey(fileName)
                    && !_blobs.containsKey(fileName)
                    && target.getBlobsMap().get(fileName) != null
                    && !fileName.startsWith(".")) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first. ");
                System.exit(0);
            }
        }
        for (String head : _allBranch.keySet()) {
            if (_allBranch.get(head).contains(target)) {
                _dir = head;
            }
        }
        _parentID = _dir;
        _parent = _dir;
        _branchMap.put(_dir, commitID);
        HashMap<String, Blob> currBlobs = new HashMap<>(
                target.getBlobsMap());
        HashMap<String, Blob> overWrite = new HashMap<>();
        for (Blob bb : currBlobs.values()) {
            File save = Utils.join(Main.CWD, bb.getName());
            save.createNewFile();
            Utils.writeContents(save, bb.getContentString());
            overWrite.put(bb.getName(), bb);
        }
        for (String fileName : _blobs.keySet()) {
            if (!currBlobs.containsKey(fileName)) {
                File deleted = Utils.join(Main.CWD, fileName);
                deleted.delete();
            }
        }
        _blobs.clear();
        _blobs.putAll(overWrite);
        for (File stageFile : Objects.requireNonNull(Main.STAGE.listFiles())) {
            Utils.restrictedDelete(stageFile);
        }
        _staging = new HashMap<>();

    }

    /** Deletes the branch with the given name. This only means
     * to delete the pointer associated with the branch; it does
     * not mean to delete all commits that were created under
     * the branch, or anything like that.
     * @param bName  branch name. */
    public void rmBranch(String bName) {
        if (!_branchMap.containsKey(bName)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (_dir.equals(bName)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        _branchMap.remove(bName);
        _newBranchMap.remove(bName);

    }



    /** Merges files from the given branch into the current branch.
     * First consider what might be called the split point of the
     * current branch and the given branch.
     * Any files that have been modified in the given branch since
     * the split point, but not modified in the current branch since
     * the split point should be changed to their versions in the
     * given branch (checked out from the commit at the front of
     * the given branch). These files should then all be automatically
     * staged. To clarify, if a file is "modified in the given branch
     * since the split point" this means the version of the file as
     * it exists in the commit at the front of the given branch has
     * different content from the version of the file at the split
     * point.
     * @param bName branch name. */
    public void merge(String bName) throws IOException {
        if (!_staging.isEmpty() || !_toRemove.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        if (!_allBranch.keySet().contains(bName)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (bName.equals(_dir)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
        Commit curr = Utils.readObject(Utils.join(Main.COMMIT, _parentID),
                Commit.class);

        List<String> allFiles = Utils.plainFilenamesIn(Main.CWD);
        for (String fileName : allFiles) {
            if (!_staging.containsKey(fileName)
                    && !_blobs.containsKey(fileName)
                    && curr.getBlobsMap().get(fileName) != null
                    && !fileName.startsWith(".")) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first. ");
                System.exit(0);
            }
        }





    }


    /** get the HEAD. */
    private String _head;
    /** get the parent. */
    private String _parent;
    /** get the sha-1 id of its parent. */
    private String _parentID;
    /** get the sha-1 id. */
    private String _shaID;
    /** hashmap for branches. */
    private HashMap<String, String> _branchMap = new HashMap<>();
    /** hashmap for  staging. */
    private HashMap<String, Blob> _staging = new HashMap<>();
    /** hashpmap for not staging. */
    private HashMap<String, Blob> _notStaging = new HashMap<>();
    /** track for removed files. */
    private HashMap<String, Blob> _trackRemove = new HashMap<>();
    /** hashmap for blob. */
    private HashMap<String, Blob> _blobs = new HashMap<>();
    /** hashpmap for all commits ignore branches. */
    private HashMap<String, Commit> _allCommit = new HashMap<>();
    /** hashmap for specified branch of all commits. */
    private HashMap<String, ArrayList<Commit>> _allBranch = new HashMap<>();
    /** all commit sha-1 ids ignore branches. */
    private ArrayList<String> _ids = new ArrayList<>();
    /** all names. */
    private ArrayList<String> _allNames = new ArrayList<>();
    /** to remove. */
    private ArrayList<String> _toRemove = new ArrayList<>();
    /** branch of remove. */
    private ArrayList<String> _toRemoveBranch = new ArrayList<>();
    /** new branch. */
    private HashMap<String, String> _newBranchMap = new HashMap<>();
    /** current working directory. */
    private String _dir;



}
