package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/** Combinations of log messages, other metadata (commit date, author, etc.),
 * a reference to a tree, and references to parent commits. The repository also
 * maintains a mapping from branch heads (in this course, we've used names like
 * master, proj2, etc.) to references to commits, so that certain important
 * commits have symbolic names.
 * @author yulan
 */
public class Commit implements Serializable {
    /** this message. */
    private String message;
    /** this time stamp. */
    private String timestamp;

    /** save this commit into commit directory. */
    private File savingCommit;
    /** this parent. */
    private String parent;

    /** this commit's sha-1 id. */
    private String shaID;

    /** this commit's blob. */
    private HashMap<String, Blob> blobsMap = new HashMap<>();

    /** this commit's blob names. */
    private ArrayList<String> name = new ArrayList<>();

    /** this commit's blob contents. */
    private ArrayList<String> contents = new ArrayList<>();

    /** check if it's a merge commit. */
    private boolean _merge = false;

    /** commit constructor.
     * @param msg commit message.
     * @param par parent of this commit.
     * @param blob blob.
     */
    public Commit(String msg, String par,
                  HashMap<String, Blob> blob) {
        this.message = msg;
        this.parent = par;
        if (this.parent == null) {
            this.timestamp = "Thu Jan 1 00:00:00 1970 -0800";
            this.shaID = Utils.sha1(this.message, this.timestamp);
        } else {
            this.timestamp = dateFormat();
            this.blobsMap = blob;
            for (String b : blob.keySet()) {
                this.name.add(blob.get(b).getName());
                this.contents.add(blob.get(b).getContentString());
            }
            List<Object> obj = new ArrayList<>();
            obj.add(this.message);
            obj.add(this.timestamp);
            obj.add(this.parent);
            obj.addAll(this.name);
            obj.addAll(this.contents);
            this.shaID = Utils.sha1(obj);
        }
        this.savingCommit =  Utils.join(Main.COMMIT, shaID);
    }

    /** get the correct format of the time stamp.
     * @return formated date. */
    private String dateFormat() {
        Date date = new Date();
        SimpleDateFormat formated = new SimpleDateFormat(
                "EEE MMM d HH:mm:ss yyyy Z");
        formated.setTimeZone(TimeZone.getTimeZone("PST"));
        return formated.format(date);
    }


    /** get the commit message.
     * @return commit message. */
    public String getMessage() {
        return this.message;
    }

    /** get the commit time stamp.
     * @return time stamp. */
    public String getTimestamp() {
        return this.timestamp;
    }

    /** get the commit parent.
     * @return parent. */
    public String getParent() {
        return this.parent;
    }

    /** get the commit sha-1 id.
     * @return sha-1. */
    public String getID() {
        return this.shaID;
    }

    /** get the saving file of this commit.
     * @return saving commit file. */
    public File getSavingCommit() {
        return this.savingCommit;
    }

    /** get the names in the blob map.
     * @return name. */
    public ArrayList<String> getNames() {
        return this.name;
    }

    /** get the contents in the blob map.
     * @return contents. */
    public ArrayList<String> getContents() {
        return this.contents;
    }

    /** get the mapping of blobs.
     * @return blob's map. */
    public HashMap<String, Blob> getBlobsMap() {
        return this.blobsMap;
    }

    /** get the boolean of whether it's a merge commit.
     * @return true of false. */
    public boolean isMerge() {
        return _merge;
    }

}
