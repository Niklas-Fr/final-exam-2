/*
 * Copyright (c) 2023, KASTEL. All rights reserved.
 */

package kit.edu.informatik.commands;

import kit.edu.informatik.exceptions.InvalidArgumentException;
import kit.edu.informatik.model.FileModel;

/**
 * The class models quiting the game.
 * Solution from Task 4.
 * Implements interface {@link Command}.
 *
 * @author Programmieren-Team
 * @author uqtwh
 * @version 1.0
 * @see Command
 */
public final class QuitCommand implements Command {
    private final CommandHandler commandHandler;

    /**
     * Constructor for the QuitCommand.
     *
     * @param commandHandler the command handler
     */
    QuitCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    /**
     * Overriding the execute method from Interface {@link Command}.
     * Quits the game.
     *
     * @param ignored          required by the interface
     * @param commandArguments the arguments of the command
     * @return CommandResult of the execution with a {@link CommandResultType type} and a {@code null}
     */
    @Override
    public CommandResult execute(FileModel ignored, Arguments commandArguments) {
        try {
            commandArguments.assertNoMoreArguments();
            commandHandler.quit();
            return new CommandResult(CommandResultType.SUCCESS, null);
        } catch (InvalidArgumentException e) {
            return new CommandResult(CommandResultType.FAILURE, e.getMessage());
        }
    }
}
