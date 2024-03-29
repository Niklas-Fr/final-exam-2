package kit.edu.informatik.model.documenttags;

/**
 * Superclass, modeling Tags of Documents.
 *
 * @author uqtwh
 * @version 1.0
 */
public abstract class Tag {

    /**
     * The delimiter between the tag identifier and the tag value.
     */
    protected static final String TAG_DELIMITER = "=";

    /**
     * Constructor for a tag.
     */
    protected Tag() {
    }

    /**
     * Getter for the identifier of the tag.
     * @return the identifier of the tag.
     */
    public abstract String getIdentifier();

    /**
     * Getter for the value of the tag.
     * @return the value of the tag.
     */
    public abstract String getValue();

    /**
     * Abstract method, returns wether a tag is multivalent.
     * @return {@code true} if the tag is multivalent, {@code false} otherwise
     */
    public abstract boolean isMultivalent();
}
