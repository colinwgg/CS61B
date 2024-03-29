package gitlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Representing a staging area.
 */
public class Stage implements Serializable {
    // <filename, blobId>
    private final HashMap<String, String> added;
    private final HashSet<String> removed;

    public Stage() {
        added = new HashMap<>();
        removed = new HashSet<>();
    }

    public void addFile(String filename, String blobId) {
        added.put(filename, blobId);
        removed.remove(filename);
    }

    public void removeFile(String filename) {
        added.remove(filename);
        removed.add(filename);
    }

    public boolean isEmpty() {
        return added.isEmpty() && removed.isEmpty();
    }

    public HashMap<String, String> getAdded() {
        return added;
    }

    public HashSet<String> getRemoved() {
        return removed;
    }

    public ArrayList<String> getStagedFilename() {
        ArrayList<String> res = new ArrayList<>();
        res.addAll(added.keySet());
        res.addAll(removed);
        return res;
    }
}
