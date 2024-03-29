/*
 * Copyright (c) 2023, KASTEL. All rights reserved.
 */

package kit.edu.informatik.commands;

import kit.edu.informatik.model.FileModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

/**
 * This class handles the user input and executes the {@link Command}s.
 *
 * @author Programmieren-Team
 * @author uqtwh
 * @version 1.0
 */
public final class CommandHandler {
    private static final String COMMAND_SEPARATOR_REGEX = " ";
    private static final String ERROR_PREFIX = "ERROR: ";
    private static final String COMMAND_NOT_FOUND_FORMAT = ERROR_PREFIX + "command '%s' not found";
    private static final String QUIT_COMMAND_NAME = "quit";
    private static final Command[] COMMANDS = {new LoadCommand(), new ChangeCommand(), new RunCommand()};
    private final Map<String, Command> commands;
    private final FileModel fileModel;
    private boolean running = false;

    /**
     * Constructs a new CommandHandler.
     * @param fileModel the model
     */
    public CommandHandler(FileModel fileModel) {
        this.fileModel = Objects.requireNonNull(fileModel);
        this.commands = new HashMap<>();
        this.initCommands();
    }

    /**
     * Starts the interaction with the user.
     */
    public void handleUserInput() {
        this.running = true;

        try (Scanner scanner = new Scanner(System.in)) {
            while (running && scanner.hasNextLine()) {
                executeCommand(scanner.nextLine());
            }
        }
    }

    /**
     * Quits the interaction with the user.
     */
    public void quit() {
        this.running = false;
    }

    /**
     * Exectutes a {@link Command} from a given String
     * @param commandWithArguments String with command
     */
    private void executeCommand(String commandWithArguments) {
        String[] splittedCommand = commandWithArguments.trim().split(COMMAND_SEPARATOR_REGEX);
        String commandName = splittedCommand[0];
        String[] commandArguments = Arrays.copyOfRange(splittedCommand, 1, splittedCommand.length);

        executeCommand(commandName, new Arguments(commandArguments));
    }

    /**
     * Exectutes a given command with given arguments.
     * @param commandName      name of the command to be executed
     * @param commandArguments arguments for the command
     */
    private void executeCommand(String commandName, Arguments commandArguments) {
        if (!commands.containsKey(commandName)) {
            System.err.println(COMMAND_NOT_FOUND_FORMAT.formatted(commandName));
        } else {
            CommandResult result = commands.get(commandName).execute(fileModel, commandArguments);
            if (result.getMessage() != null) {
                switch (result.getType()) {
                    case SUCCESS -> System.out.println(result.getMessage());
                    case FAILURE -> System.err.println(ERROR_PREFIX + result.getMessage());
                    default -> throw new IllegalStateException();
                }
            }
        }
    }

    /**
     * Initializes the {@link Command}s of the game.
     */
    private void initCommands() {
        this.addCommand(QUIT_COMMAND_NAME, new QuitCommand(this));
        for (int i = 0; i < COMMANDS.length; i++) {
            this.addCommand(String.valueOf(UserCommands.values()[i]).toLowerCase(), COMMANDS[i]);
        }
    }

    /**
     * Adds a {@link Command} to the command handler.
     * @param commandName the name of the command
     * @param command     the command
     */
    private void addCommand(String commandName, Command command) {
        this.commands.put(commandName, command);
    }
}

