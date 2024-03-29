package kit.edu.informatik.model.documenttags;

/**
 * A multivalent tag is a tag with a identifier and a value.
 * Inherits from {@link Tag}.
 *
 * @author uqtwh
 * @version 1.0
 * @see Tag
 */
public final class NumericTag extends Tag {
    private final String identifier;
    private final int value;

    /**
     * Constructor for a multivalent tag.
     * @param identifier The identifier of the tag.
     * @param value The value of the tag.
     */
    public NumericTag(String identifier, int value) {
        this.identifier = identifier;
        this.value = value;
    }

    /**
     * Getter for the identifier of the tag.
     * @return the identifier of the tag.
     */
    @Override
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Getter for the {@link #value} of the tag.
     * @return the value of the tag.
     */
    @Override
    public String getValue() {
        return String.valueOf(value);
    }

    /**
     * Returns whether the tag is multivalent, so true.
     * @return {@code true}
     */
    @Override
    public boolean isMultivalent() {
        return true;
    }

    @Override
    public String toString() {
        return identifier + TAG_DELIMITER + value;
    }
}
