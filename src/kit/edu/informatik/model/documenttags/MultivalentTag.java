package kit.edu.informatik.model.documenttags;

/**
 * A multivalent tag is a tag with a identifier and a value.
 * Inherits from {@link Tag}.
 *
 * @author uqtwh
 * @version 1.0
 * @see Tag
 */
public final class MultivalentTag extends Tag {
    private final String identifier;
    private final String value;

    /**
     * Constructor for a multivalent tag.
     * @param identifier the identifier of the tag.
     * @param value      the value of the tag.
     */
    public MultivalentTag(String identifier, String value) {
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
     * Getter for the value of the tag.
     * @return the value of the tag.
     */
    @Override
    public String getValue() {
        return value;
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
