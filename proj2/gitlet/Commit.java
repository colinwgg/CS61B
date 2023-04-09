package gitlet;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *
 *  @author Colin Wang
 */
public class Commit implements Serializable {
    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;

    /** The timestamp of this Commit */
    private Date timestamp;

    private List<String> parents;

    /** The file this Commit tracks */
    private HashMap<String, String> blobs;

    private String id;

    public Commit() {
        message = "initial commit";
        timestamp = new Date(0);
        parents = new LinkedList<>();
        blobs = new HashMap<>();
        id = sha1(message, timestamp.toString());
    }

    public Commit(String message, List<Commit> parents, Stage stage) {
        this.message = message;
        this.timestamp = new Date();
        this.parents = new ArrayList<>(2);
        for (Commit p : parents) {
            this.parents.add(p.getID());
        }
        this.blobs = parents.get(0).getBlobs();
        for (Map.Entry<String, String> item : stage.getAdded().entrySet()) {
            String filename = item.getKey();
            String blobId = item.getValue();
            blobs.put(filename, blobId);
        }
        for (String filename : stage.getRemoved()) {
            blobs.remove(filename);
        }
        this.id = sha1(message, timestamp.toString(), parents.toString(), blobs.toString());
    }

    public String getID() {
        return id;
    }

    public HashMap<String, String> getBlobs() {
        return blobs;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getParents() {
        return parents;
    }

    public String getFirstParentId() {
        if (parents.isEmpty()) {
            return "null";
        }
        return parents.get(0);
    }

    public String getDateString() {
        // Thu Nov 9 20:00:05 2017 -0800
        DateFormat df = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);
        return df.format(timestamp);
    }

    public String getCommitAsString() {
        StringBuffer sb = new StringBuffer();
        sb.append("===\n");
        sb.append("commit " + this.id + "\n");
        if (parents.size() == 2) {
            sb.append("Merge: " + parents.get(0).substring(0, 7) + " " +parents.get(1).substring(0, 7) + "\n");
        }
        sb.append("Date: " + getDateString() + "\n");
        sb.append(this.message + "\n\n");
        return sb.toString();
    }
}
