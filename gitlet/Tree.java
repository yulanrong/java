package gitlet;


import java.io.Serializable;

/** Directory structures mapping names to references to blobs
 * and other trees (subdirectories).
 * @author yulan
 */
public class Tree implements Serializable {
    /** the root of Tree. */
    private String _root;
    /** the head of Tree. */
    private String _head;
    /** sha-1 id. */
    private String _shaID;
    /** this contents. */
    private byte[] contents;

    /** Tree constructor.
     * @param head head.
     * @param root root. */
    public Tree(String head, String root) {
        this._head = head;
        this._root = root;
    }

    /** get the head.
     * @return head. */
    public String  getHead() {
        return _head;
    }

    /** get the root.
     * @return root. */
    public String  getRoot() {
        return _root;
    }

    /** get the sha-1 Id.
     * @return sha-1. */
    public String  getID() {
        return _shaID;
    }

    /** get the content.
     * @return content. */
    public byte[] getContents() {
        return contents;
    }
}
