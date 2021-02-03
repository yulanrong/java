package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;


/** Blob class to handle contents of files.
 * @author Yulan Rong
 */
public class Blob implements Serializable {
    /** Blob constructor.
     * construct blob's name, file, sha-1, contents in
     * byte and in string, and save blob.
     * @param fileName the name of file.
     */
    public Blob(String fileName) throws IOException {
        _name = fileName;
        _file = Utils.join(Main.CWD, fileName);
        _contentByte = Utils.readContents(_file);
        _shaID = Utils.sha1(_contentByte);
        _savingBlob = Utils.join(Main.BLOB, _shaID);
        _contentAsString = Utils.readContentsAsString(_file);
        Utils.writeContents(_savingBlob, _contentByte);
    }

    /** get the sha-1 id.
     * @return sha-1. */
    public String getID() {
        return _shaID;
    }

    /** get the name of file.
     * @return name. */
    public String getName() {
        return _name;
    }

    /** get the string of content of file.
     * @return content in string. */
    public String getContentString() {
        return _contentAsString;
    }

    /** get the content of file.
     * @return content in byte. */
    public byte[] getContent() {
        return _contentByte;
    }

    /** get the saving file for this blob.
     * @return saving blob. */
    public File getSavingBlob() {
        return _savingBlob;
    }


    /** this file. */
    private File _file;

    /** this content in byte. */
    private byte[] _contentByte;

    /** this sha-1 id. */
    private String _shaID;

    /** this name. */
    private String _name;

    /** this content in string format. */
    private String _contentAsString;

    /** the file to save this blob. */
    private File _savingBlob;

}
