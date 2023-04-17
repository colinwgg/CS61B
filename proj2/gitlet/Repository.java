package gitlet;

import java.io.*;
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
    /*
      List all instance variables of the Repository class here with a useful
      comment above them describing what that variable represents and how that
      variable is used. We've provided two examples for you.
     */

    /*
      .gitlet
      -- staging
      -- [stage]
      -- blobs
      -- commits
      -- ref
        -- heads -> [master][branch name]
        -- remotes
          -- [remote git repo name] -> [branch name]
      -- [HEAD]
      -- [config]
     */
    /**
     * The current working directory.
     */
    private final File CWD;
    private File GITLET_DIR;
    private File STAGING_DIR;
    private File STAGE;
    private File BLOBS_DIR;
    private File COMMITS_DIR;
    private File REFS_DIR;
    private File HEADS_DIR;
    private File REMOTES_DIR;
    private File HEAD;
    private File CONFIG;

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
        String id = inititalCommit.getID();

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
            System.out.println("File does not exist.");
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
                stage.getRemoved().remove(filename);
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
        if (message.equals("")) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
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
            stage.getAdded().remove(filename);
        } else {
            stage.getRemoved().add(filename);
        }

        Blob blob = new Blob(filename, CWD);
        String blobId = blob.getId();

        if (blob.exists() && blobId.equals(headId)) {
            restrictedDelete(file);
        }

        writeStage(stage);
    }

    public void log() {
        StringBuffer sb = new StringBuffer();
        Commit head = getHead();
        while (head != null) {
            sb.append(head.getCommitAsString());
            head = getCommitFromId(head.getFirstParentId());
        }
        System.out.print(sb);
    }

    public void globalLog() {
        StringBuffer sb = new StringBuffer();
        List<String> commitIds = plainFilenamesIn(COMMITS_DIR);
        assert commitIds != null;
        for (String commitId : commitIds) {
            Commit commit = getCommitFromId(commitId);
            assert commit != null;
            sb.append(commit.getCommitAsString());
        }
        System.out.println(sb);
    }

    public void find(String commitMessage) {
        StringBuffer sb = new StringBuffer();
        List<String> commitIds = plainFilenamesIn(COMMITS_DIR);
        assert commitIds != null;
        for (String commitId : commitIds) {
            Commit commit = getCommitFromId(commitId);
            assert commit != null;
            if (commit.getMessage().contains(commitMessage)) {
                sb.append(commit.getID()).append("\n");
            }
        }
        if (sb.length() == 0) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
        System.out.println(sb);
    }

    public void status() {
        StringBuffer sb = new StringBuffer();

        sb.append("=== Branches ===\n");
        String headBranchName = getHeadBranchName();
        List<String> branches = plainFilenamesIn(HEADS_DIR);
        assert branches != null;
        for (String branch : branches) {
            if (branch.equals(headBranchName)) {
                sb.append("*").append(headBranchName).append("\n");
            } else {
                sb.append(branch).append("\n");
            }
        }
        sb.append("\n");

        sb.append("=== Staged Files ===\n");
        Stage stage = readStage();
        for (String filename : stage.getAdded().keySet()) {
            sb.append(filename).append("\n");
        }
        sb.append("\n");
        sb.append("=== Removed Files ===\n");
        for (String filename : stage.getRemoved()) {
            sb.append(filename).append("\n");
        }
        sb.append("\n");

        sb.append("=== Modifications Not Staged For Commit ===\n");
        sb.append("\n");
        sb.append("=== Untracked Files ===\n");
        sb.append("\n");

        System.out.println(sb);
    }

    public void checkoutFileFromHead(String filename) {
        Commit head = getHead();
        checkoutFileFromCommit(head, filename);
    }

    public void checkoutFileFromCommitId(String commitId, String filename) {
        String fullId = getCompleteCommitId(commitId);
        Commit commit = getCommitFromId(fullId);
        if (commit == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        checkoutFileFromCommit(commit, filename);
    }

    public void checkoutBranch(String branchName) {
        File branchFile = getBranchFile(branchName);
        String headBranchName = getHeadBranchName();
        if (!branchFile.exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        } else if (headBranchName.equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        Commit otherCommit = getCommitFromBranchName(branchName);
        validUntrackedFiles(otherCommit.getBlobs());
        clearStage();
        replaceWorkingPlaceWithCommit(otherCommit);
        writeContents(HEAD, branchName);
    }

    public void branch(String branchName) {
        File branch = join(HEADS_DIR, branchName);
        if (branch.exists()) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        String commitId = getHeadCommitId();
        writeContents(branch, commitId);
    }

    public void rmBranch(String branchName) {
        File branch = join(HEADS_DIR, branchName);
        if (!branch.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        String headBranchName = getHeadBranchName();
        if (headBranchName.equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        branch.delete();
    }

    public void reset(String commitId) {
        File file = join(COMMITS_DIR, commitId);
        if (!file.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commit commit = getCommitFromId(commitId);
        assert commit != null;
        validUntrackedFiles(commit.getBlobs());
        replaceWorkingPlaceWithCommit(commit);
        clearStage();
        String headBranchName = getHeadBranchName();
        writeContents(join(HEADS_DIR, headBranchName), commitId);
    }

    /**
     * helper functions
     */
    private String getHeadCommitId() {
        return getHead().getID();
    }

    private void replaceWorkingPlaceWithCommit(Commit commit) {
        clearWorkingPlace();
        for (Map.Entry<String, String> item : commit.getBlobs().entrySet()) {
            String filename = item.getKey();
            String blobId = item.getValue();
            File file = join(CWD, filename);
            Blob blob = readObject(join(BLOBS_DIR, blobId), Blob.class);
            writeContents(file, (Object) blob.getContent());
        }
    }

    private void clearWorkingPlace() {
        File[] files = CWD.listFiles(gitletFilter);
        assert files != null;
        for (File file : files) {
            delFile(file);
        }
    }

    private final FilenameFilter gitletFilter = (dir, name) -> !name.equals(".gitlet");

    private void delFile(File file) {
        if (file.isDirectory()) {
            for (File f : Objects.requireNonNull(file.listFiles())) {
                delFile(f);
            }
        }
        file.delete();
    }

    private void validUntrackedFiles(Map<String, String> blobs) {
        List<String> untrackedFiles = getUntrackedFiles();
        if (untrackedFiles.isEmpty()) {
            return;
        }
        for (String filename : untrackedFiles) {
            String blobId = new Blob(filename, CWD).getId();
            String otherId = blobs.getOrDefault(filename, "");
            if (!otherId.equals(blobId)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
    }

    private List<String> getUntrackedFiles() {
        List<String> res = new ArrayList<>();
        List<String> stageFiles = readStage().getStagedFilename();
        Set<String> headFiles = getHead().getBlobs().keySet();
        for (String filename : Objects.requireNonNull(plainFilenamesIn(CWD))) {
            if (!stageFiles.contains(filename) && !headFiles.contains(filename)) {
                res.add(filename);
            }
        }
        Collections.sort(res);
        return res;
    }

    private void checkoutFileFromCommit(Commit commit, String filename) {
        File file = join(CWD, filename);
        String blobId = commit.getBlobs().getOrDefault(filename, "");
        if (blobId.equals("")) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        Blob blob = getBlobFromId(blobId);
        writeContents(file, (Object) blob.getContent());
    }

    private String getCompleteCommitId(String commitId) {
        if (commitId.length() == UID_LENGTH) {
            return commitId;
        }

        for (String id : Objects.requireNonNull(COMMITS_DIR.list())) {
            if (id.startsWith(commitId)) {
                return id;
            }
        }
        return null;
    }

    private void commitWith(String message, List<Commit> parents) {
        Stage stage = readStage();
        if (stage.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        Commit commit = new Commit(message, parents, stage);
        clearStage();
        writeCommitToFile(commit);

        String branchName = getHeadBranchName();
        File head = getBranchFile(branchName);
        String commitId = commit.getID();
        writeContents(head, commitId);
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
        File file = join(COMMITS_DIR, commit.getID());
        writeObject(file, commit);
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

    private Blob getBlobFromId(String blobId) {
        File file = join(BLOBS_DIR, blobId);
        return readObject(file, Blob.class);
    }

    private File getBranchFile(String branchName) {
        return join(HEADS_DIR, branchName);
    }

    private Commit getCommitFromBranchName(String branchName) {
        File branchFile = getBranchFile(branchName);
        return getCommitFromBranchFile(branchFile);
    }

    private String getHeadBranchName() {
        return readContentsAsString(HEAD);
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

    void checkEqual(String actual) {
        if (!actual.equals("--")) {
            messageIncorrectOperands();
        }
    }

    void messageIncorrectOperands() {
        System.out.println("Incorrect operands.");
        System.exit(0);
    }

    void checkIfInitDirectoryExists() {
        if (!GITLET_DIR.isDirectory()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }
}
