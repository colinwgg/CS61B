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
    /**
     * The current working directory.
     */
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

    /**
     * 1. Staging an already-staged file overwrites the previous entry in the staging area with the new contents.
     * 2. If the current working version of the file is identical to the version in the current commit,
     * do not stage it to be added, and remove it from the staging area if it is already there
     * (as can happen when a file is changed, added, and then changed back to its original version).
     * 3. The file will no longer be staged for removal (see gitlet rm), if it was at the time of the command.
     */
    public void add(String filename) {
        File file = join(CWD, filename);
        if (!file.exists()) {
            System.out.println("File doesn't exist");
            System.exit(0);
        }

        Commit head = getHead();
        Stage stage = readStage();
        String headId = head.getBlobs().getOrDefault(filename, "");
        String stageId = stage.getAdded().getOrDefault(filename, "");
        Blob blob = new Blob(filename, CWD);
        String blobId = blob.getId();

        if (blobId.equals(headId)) {
            if (!blobId.equals(stageId)) {
                join(STAGING_DIR, stageId).delete();
                stage.getAdded().remove(stageId);
                stage.getRemoved().add(filename);
                writeStage(stage);
            }
        } else if (!blobId.equals(stageId)) {
            if (!stageId.equals("")) {
                join(STAGING_DIR, stageId).delete();
            }
            writeObject(join(STAGING_DIR, blobId), blob);
            stage.addFile(filename, blobId);
            writeStage(stage);
        }
    }

    public void commit(String message) {
        if (message == null) {
            System.out.println("Please enter a commit message.");
        }
        Commit head = getHead();
        commitWith(message, List.of(head));
    }

    public void rm(String filename) {
        File file = join(CWD, filename);
        Commit head = getHead();
        Stage stage = readStage();
        String headId = head.getBlobs().getOrDefault(filename, "");
        String stageId = stage.getAdded().getOrDefault(filename, "");

        if (headId.equals("") && stageId.equals("")) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }

        // Un_stage the file if it currently staged for addition
        if (!stageId.equals("")) {
            stage.getAdded().remove(stageId);
        } else {
            stage.getRemoved().add(stageId);
        }

        Blob blob = new Blob(filename, CWD);
        String blobId = blob.getId();

        if (blob.exists() && headId.equals(blobId)) {
            restrictedDelete(file);
        }

        writeStage(stage);
    }

    /**
     * helper functions
     */

    private void commitWith(String message, List<Commit> parents) {
        Stage stage = readStage();
        if (stage.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        Commit commit = new Commit(message, parents, stage);
        clearStage();
    }

    private Stage readStage() {
        return readObject(STAGE, Stage.class);
    }

    private void writeStage(Stage stage) {
        writeObject(STAGE, stage);
    }

    private void clearStage() {
        File[] files = STAGING_DIR.listFiles();
        if (files == null) {
            return;
        }
        Path targetDir = BLOBS_DIR.toPath();
        for (File file : files) {
            Path source = file.toPath();
            try {
                Files.move(source, targetDir.resolve(source.getFileName()), REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        writeStage(new Stage());
    }

    private void writeCommitToFile(Commit commit) {
        File file = join(COMMITS_DIR, commit.getId());
        writeObject(file, commit);
    }

    private String getHeadBranchName() {
        return readContentsAsString(HEAD);
    }

    private Commit getCommitFromId(String commitId) {
        File file = join(COMMITS_DIR, commitId);
        if (commitId.equals("null") || !file.exists()) {
            return null;
        }
        return readObject(file, Commit.class);
    }

    private Commit getCommitFromBranchFile(File file) {
        String commitId = readContentsAsString(file);
        return getCommitFromId(commitId);
    }

    /** [branch]
     *  [R1/branch]
     */
    private File getBranchFile(String branchName) {
        File file = null;
        String[] branch = branchName.split("/");
        if (branch.length == 1) {
            file = join(HEADS_DIR, branchName);
        } else if(branch.length == 2) {
            file = join(REMOTES_DIR, branch[0], branch[1]);
        }
        return file;
    }

    private Commit getCommitFromBranchName(String branchName) {
        File branchFile = getBranchFile(branchName);
        return getCommitFromBranchFile(branchFile);
    }

    private Commit getHead() {
        String branchName = getHeadBranchName();
        File branchFile = getBranchFile(branchName);
        Commit head = getCommitFromBranchFile(branchFile);
        if (head == null) {
            System.out.println("error! cannot find HEAD!");
            System.exit(0);
        }
        return head;
    }

    /**
     * check methods
     */
    void checkCommandLength(int actual, int expected) {
        if (actual != expected) {
            messageIncorrectOperands();
        }
    }

    void checkEqual(String actual, String expected) {
        if (!actual.equals(expected)) {
            messageIncorrectOperands();
        }
    }

    void messageIncorrectOperands() {
        System.out.println("Incorrect operands.");
        System.exit(0);
    }

    void checkIfInitDirectoryExist() {
        if (!GITLET_DIR.isDirectory()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }
}
