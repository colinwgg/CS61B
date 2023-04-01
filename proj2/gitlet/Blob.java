package gitlet;

import java.io.File;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

import static gitlet.Utils.*;

/**
 * Represent a file object.
 */
public class Blob {
    private String filename;
    private String id;
    private byte[] content;

    public Blob(String filename, File file) {
        this.filename = filename;
        
    }
}
