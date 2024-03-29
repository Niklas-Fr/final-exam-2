package kit.edu.informatik;

import kit.edu.informatik.commands.CommandHandler;
import kit.edu.informatik.model.FileModel;

/**
 * The class is the entry point of the programm.
 *
 * @author uqtwh
 * @version 1.0
 */
public final class Application {
    private static final String SUCCESSFUL_START = "Use one of the following "
            + "commands: load <path>, run <id>, change <id> <file> <number>, quit";

    /**
     * Private constructor to avoid instantiation.
     */
    private Application() {
        throw new UnsupportedOperationException();
    }

    /**
     * Main method to start the program.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println(SUCCESSFUL_START);
        FileModel fileModel = new FileModel();
        CommandHandler commandHandler = new CommandHandler(fileModel);
        commandHandler.handleUserInput();
    }
}
