package kit.edu.informatik.commands;

import kit.edu.informatik.exceptions.InvalidArgumentException;
import kit.edu.informatik.model.Data;
import kit.edu.informatik.model.FileModel;

import java.util.Optional;

/**
 * Command to run a document, sorting it efficiently.
 * Implements interface {@link Command}.
 *
 * @author utqwh
 * @version 1.0
 * @see Command
 */
public final class RunCommand implements Command {
    private static final String INVALID_ID = "Data with ID %d does not exist.";

    /**
     * The command takes an ID of a data which has been loaded into the program and efficiently sorts it via
     * information gains.
     * @param fileModel        the {@link FileModel} to execute the command on
     * @param commandArguments the arguments of the command
     * @return a {@link CommandResult} with a corresponding message
     */
    @Override
    public CommandResult execute(FileModel fileModel, Arguments commandArguments) {
        try {
            int id = commandArguments.nextInt();
            commandArguments.assertNoMoreArguments();
            Optional<Data> data = fileModel.getData(id);
            return data.map(value -> new CommandResult(CommandResultType.SUCCESS, fileModel.run(value))).
                    orElseGet(() -> new CommandResult(CommandResultType.FAILURE, INVALID_ID.formatted(id)));
        } catch (InvalidArgumentException e) {
            return new CommandResult(CommandResultType.FAILURE, e.getMessage());
        }
    }
}

