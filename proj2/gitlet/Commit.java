package gitlet;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *
 *  @author Colin Wang
 */
public class Commit implements Serializable {
    /*
      List all instance variables of the Commit class here with a useful
      comment above them describing what that variable represents and how that
      variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private final String message;

    /** The timestamp of this Commit */
    private final Date timestamp;

    private final List<String> parents;

    /** The file this Commit tracks (filename, blobId)*/
    private final HashMap<String, String> blobs;

    private final String id;

    public Commit() {
        this.message = "initial commit";
        this.timestamp = new Date(0);
        this.parents = new LinkedList<>();
        this.blobs = new HashMap<>();
        this.id = sha1(message, timestamp.toString());
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
        return this.blobs;
    }

    public String getMessage() {
        return this.message;
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
        sb.append("commit ").append(this.id).append("\n");
        if (parents.size() == 2) {
            sb.append("Merge: ").append(parents.get(0), 0, 7).append(" ").
                    append(parents.get(1), 0, 7).append("\n");
        }
        sb.append("Date: ").append(this.getDateString()).append("\n");
        sb.append(this.message).append("\n\n");
        return sb.toString();
    }
}
