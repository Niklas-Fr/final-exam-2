package kit.edu.informatik.model.documenttags;

import kit.edu.informatik.model.DefaultValueRange;

/**
 * A binary tag is a tag without a value.
 * Inherits from {@link Tag}.
 *
 * @author uqtwh
 * @version 1.0
 * @see Tag
 */
public final class BinaryTag extends Tag {
    private final String identifier;

    /**
     * Constructor for a binary tag.
     * @param identifier the identifier of the tag.
     */
    public BinaryTag(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Getter for the {@link #identifier} of the tag.
     * @return the identifier of the tag.
     */
    @Override
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Returns the value of the tag, since being binary, the value is always DEFINED.
     * @return the value of the tag
     */
    @Override
    public String getValue() {
        return String.valueOf(DefaultValueRange.DEFINED);
    }

    /**
     * Returns whether the the Tag is multivalent, so false.
     * @return {@code false}
     */
    @Override
    public boolean isMultivalent() {
        return false;
    }

    @Override
    public String toString() {
        return identifier;
    }
}
