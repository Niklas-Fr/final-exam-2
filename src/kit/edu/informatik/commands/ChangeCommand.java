package kit.edu.informatik.commands;

import kit.edu.informatik.exceptions.InvalidArgumentException;
import kit.edu.informatik.model.Data;
import kit.edu.informatik.model.documents.Document;
import kit.edu.informatik.model.FileModel;

import java.util.Optional;

/**
 * Command to change the access amount of a document in a given data.
 * Implements inteface {@link Command}.
 *
 * @author uqtwh
 * @version 1.0
 * @see Command
 */
public final class ChangeCommand implements Command {
    private static final String INVALID_ID = "No data with ID %d found.";
    private static final String INVALID_FILE_PATH = "No document with file path \"%s\" found.";
    private static final String INVALID_ACCESS_NUMBER = "New access number is expected to be > 0, but was %d";

    /**
     * Changes the access amount of a given document in a data with a given ID.
     * @param fileModel        the {@link FileModel} to execute the command on
     * @param commandArguments the arguments of the command
     * @return a {@link CommandResult} with a corresponding message
     */
    @Override
    public CommandResult execute(FileModel fileModel, Arguments commandArguments) {
        try {
            int documentID = commandArguments.nextInt();
            String filePath = commandArguments.nextString();
            int accessNumber = commandArguments.nextInt();
            commandArguments.assertNoMoreArguments();

            if (accessNumber < 0) {
                return new CommandResult(CommandResultType.FAILURE, INVALID_ACCESS_NUMBER.formatted(accessNumber));
            }
            Optional<Data> data = fileModel.getData(documentID);
            if (data.isEmpty()) {
                return new CommandResult(CommandResultType.FAILURE, INVALID_ID.formatted(documentID));
            }
            Optional<Document> document = data.get().getDocument(filePath);
            return document.
                    map(value -> new CommandResult(CommandResultType.SUCCESS,
                    fileModel.changeData(value, accessNumber))).
                    orElseGet(() -> new CommandResult(CommandResultType.FAILURE, INVALID_FILE_PATH.formatted(filePath)));
        } catch (InvalidArgumentException e) {
            return new CommandResult(CommandResultType.FAILURE, e.getMessage());
        }
    }
}

