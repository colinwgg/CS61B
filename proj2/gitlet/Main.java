package gitlet;


/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Colin Wang
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        Repository repo = new Repository();
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                
            case "add":
                // TODO: handle the `add [filename]` command
                break;
            // TODO: FILL THE REST IN
        }
    }
}
