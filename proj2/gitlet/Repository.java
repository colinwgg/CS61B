package gitlet;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;

import static gitlet.MyUtils.*;
import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *
 *  @author Colin Wang
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /* TODO: fill in the rest of this class. */
}
