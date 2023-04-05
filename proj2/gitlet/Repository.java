package gitlet;

import java.io.*;
import java.io.ObjectInputFilter.Config;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.*;

import static gitlet.Utils.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/** Represents a gitlet repository.
 *
 *  @author Colin Wang
 */
public class Repository {
    /**
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

     /**
      * .gitlet
      * -- staging
      * -- [stage]
      * -- blobs
      * -- commits
      * -- ref
      *   -- heads -> [master][branch name]
      *   -- remotes 
      *     -- [remote git repo name] -> [branch name]
      * -- [HEAD]
      * -- [config]
      */
    /** The current working directory. */
    public File CWD;
    public File GITLET_DIR;
    public File STAGING_DIR;
    public File STAGE;
    public File BLOBS_DIR;
    public File COMMITS_DIR;
    public File REFS_DIR;
    public File HEADS_DIR;
    public File REMOTES_DIR;
    public File HEAD;
    public File CONFIG;

    public Repository() {
        this.CWD = new File(System.getProperty("user.dir"));
        configDIRS();
    }
    
    public Repository(String cwd) {
        this.CWD = new File(cwd);
        configDIRS();
    }

    private void configDIRS() {
        this.GITLET_DIR = join(CWD, ".gitlet");
        this.STAGING_DIR = join(GITLET_DIR, "staging");
        this.STAGE = join(GITLET_DIR, "stage");
        this.BLOBS_DIR = join(GITLET_DIR, "blobs");
        this.COMMITS_DIR = join(GITLET_DIR, "commits");
        this.REFS_DIR = join(GITLET_DIR, "refs");
        this.HEADS_DIR = join(REFS_DIR, "heads");
        this.REMOTES_DIR = join(REFS_DIR, "remotes");
        this.HEAD = join(GITLET_DIR, "HEAD");
        this.CONFIG = join(GITLET_DIR, "config");
    }

    public void init() {
        if (GITLET_DIR.exists() && GITLET_DIR.isDirectory()) {
            System.out.println("A Gitlet version-control system already exists in the current directory");
            System.exit(0);
        }

        GITLET_DIR.mkdir();
        STAGING_DIR.mkdir();
        writeObject(STAGE, new Stage());
        BLOBS_DIR.mkdir();
        COMMITS_DIR.mkdir();
        REFS_DIR.mkdir();
        HEADS_DIR.mkdir();
        REMOTES_DIR.mkdir();
        
        Commit inititalCommit = new Commit();
        writeCommitToFile(inititalCommit);
        String id = inititalCommit.getId();

        String branchName = "master";
        File master = join(HEADS_DIR, branchName);
        writeContents(HEAD, branchName);
        writeContents(master, id);

        writeContents(CONFIG, "");
    }

    private void writeCommitToFile(Commit commit) {
        File file = join(COMMITS_DIR, commit.getId());
        writeObject(file, commit);
    }


    /** check methods */
    void checkCommandLength(int actual, int expect) {
        if (actual != expect) {
            messageIncorrectOperands();
        }
    }

    void messageIncorrectOperands() {
        System.out.println("Incorrect operands.");
        System.exit(0);
    }
}
