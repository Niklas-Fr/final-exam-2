package kit.edu.informatik.commands;

import kit.edu.informatik.model.FileModel;

/**
 * This interface represents an executable command.
 *
 * @author Programmieren-Team
 * @author uqtwh
 * @version 1.0
 */
public interface Command {

    /**
     * Executes the command.
     * @param fileModel           the {@link FileModel} to execute the command on
     * @param commandArguments the arguments of the command
     * @return the result of the command
     */
    CommandResult execute(FileModel fileModel, Arguments commandArguments);
}
