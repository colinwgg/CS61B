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

    public Blob(String filename, File CWD) {
        this.filename = filename;
        File file = join(CWD, filename);
        if (file.exists()) {
            this.content = readContents(filename);
            this.id = sha1(filename, content);
        } else {
            this.content = null;
            this.id = sha1(filename);
        }
    }

    public boolean exists() {
        return this.content == null;
    }

    public String getFilename() {
        return filename;
    }

    public byte[] getContent() {
        return content;
    }

    public String getContendAsString() {
        return new String(content, StandardCharsets.UTF_8);    
    }

    public String getId() {
        return id;
    }
}
