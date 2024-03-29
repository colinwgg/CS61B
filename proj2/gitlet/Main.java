package gitlet;


/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Colin Wang
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        Repository repo = new Repository();
        String firstArg = args[0];
        switch (firstArg) {
            case "init" -> {
                repo.checkCommandLength(args.length, 1);
                repo.init();
            }
            case "add" -> {
                repo.checkCommandLength(args.length, 2);
                repo.checkIfInitDirectoryExists();
                repo.add(args[1]);
            }
            case "rm" -> {
                repo.checkCommandLength(args.length, 2);
                repo.checkIfInitDirectoryExists();
                repo.rm(args[1]);
            }
            case "commit" -> {
                repo.checkCommandLength(args.length, 2);
                repo.checkIfInitDirectoryExists();
                repo.commit(args[1]);
            }
            case "log" -> {
                repo.checkCommandLength(args.length, 1);
                repo.checkIfInitDirectoryExists();
                repo.log();
            }
            case "global-log" -> {
                repo.checkCommandLength(args.length, 1);
                repo.checkIfInitDirectoryExists();
                repo.globalLog();
            }
            case "find" -> {
                repo.checkCommandLength(args.length, 2);
                repo.checkIfInitDirectoryExists();
                repo.find(args[1]);
            }
            case "status" -> {
                repo.checkCommandLength(args.length, 1);
                repo.checkIfInitDirectoryExists();
                repo.status();
            }
            case "checkout" -> {
                int length = args.length;
                if (length < 2 || length > 4) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                if (length == 2) {                              // checkout branch
                    repo.checkoutBranch(args[1]);
                } else if (length == 3) {                       // checkout -- filename
                    repo.checkEqual(args[1]);
                    repo.checkoutFileFromHead(args[2]);
                } else {                                        // checkout commitId -- filename
                    repo.checkEqual(args[2]);
                    repo.checkoutFileFromCommitId(args[1], args[3]);
                }
            }
            case "branch" -> {
                repo.checkCommandLength(args.length, 2);
                repo.checkIfInitDirectoryExists();
                repo.branch(args[1]);
            }
            case "rm-branch" -> {
                repo.checkCommandLength(args.length, 2);
                repo.checkIfInitDirectoryExists();
                repo.rmBranch(args[1]);
            }
            case "reset" -> {
                repo.checkCommandLength(args.length, 2);
                repo.checkIfInitDirectoryExists();
                repo.reset(args[1]);
            }
            case "merge" -> {
                repo.checkCommandLength(args.length, 2);
                repo.checkIfInitDirectoryExists();
                repo.merge(args[1]);
            }
            default -> {
                System.out.println("No command with that name exists.");
                System.exit(0);
            }
        }
    }
}
