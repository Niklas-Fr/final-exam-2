package kit.edu.informatik.commands;

import kit.edu.informatik.exceptions.InvalidArgumentException;

/**
 * Class for modeling the command arguments, avoiding repetitive type checking.
 * Presented in Tutorium 06.
 *
 * @author uqtwh
 * @version 1.0
 */
public final class Arguments {
    private static final String ARGUMENT_MISMATCH1 = "Expected another argument.";
    private static final String ARGUEMENT_MISMATCH2 = "Expected no more arguments but got \"%s\".";
    private static final String INVALID_TYPES = "Expected an %s but got %s.";
    private static final String EXPECTED_TYPE = "Integer";
    private final String[] arguments;
    private int index = 0;

    /**
     * Constructor for the arguments.
     * @param arguments the arguments
     */
    public Arguments(String[] arguments) {
        this.arguments = arguments.clone();
    }

    /**
     * Returns the next argument as a String..
     * @return the next argument as a String
     * @throws InvalidArgumentException if there are no more arguments
     */
    public String nextString() throws InvalidArgumentException {
        index += 1;
        if (index - 1 >= arguments.length) {
            throw new InvalidArgumentException(ARGUMENT_MISMATCH1);
        }
        return arguments[index - 1];
    }

    /**
     * Returns the next argument as an int.
     * @return the next argument as an int
     * @throws InvalidArgumentException if there is no Integer argument
     */
    public int nextInt() throws InvalidArgumentException {
        try {
            return Integer.parseInt(nextString());
        } catch (NumberFormatException e) {
            index--;
            throw new InvalidArgumentException(INVALID_TYPES.formatted(EXPECTED_TYPE, nextString()));
        }
    }

    /**
     * Asserts that there are no more arguments.
     * @throws InvalidArgumentException if there are more arguments
     */
    public void assertNoMoreArguments() throws InvalidArgumentException {
        if (index < arguments.length) {
            throw new InvalidArgumentException(ARGUEMENT_MISMATCH2.formatted(nextString()));
        }
    }

    /**
     * Returns whether there are more arguments.
     * @return {@code true} if there are more arguments, {@code false} otherwise
     */
    public boolean hasNext() {
        return index < arguments.length;
    }

    /**
     * Returns the length of the arguments.
     * @return length of the arguments
     */
    public int getLength() {
        return arguments.length;
    }
}
