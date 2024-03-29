package kit.edu.informatik.model.documents;

import kit.edu.informatik.exceptions.InvalidArgumentException;
import kit.edu.informatik.model.DefaultValueRange;
import kit.edu.informatik.model.documenttags.Tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * The class models a document in the file system.
 *
 * @author utqwh
 * @version 1.0
 */
public abstract class Document {

    /**
     * Error message for {@link #transformDefaultTags()}, printed when a Document has both tags set.
     */
    protected static final String INVALID_TAGS = "Document has a \"%s\" and \"%s\" tag";

    /**
     * Error message for {@link #transformDefaultTags()}, printed when an expected numeric tag has a non
     * Integer value;
     */
    protected static final String INVALID_VALUE = "Tag \"%s\" has an invalid value, has to be Integer";

    /**
     * The old tag identifier "length";
     */
    protected static final String OLD_LENGTH_TAG = "length";

    /**
     * The old tag identifier "genre";
     */
    protected static final String OLD_GENRE_TAG = "Genre";
    private static final String DELIMITER = ",";
    private final String filePath;
    private final String stringRepresentation;
    private final List<Tag> tags;
    private int accessAmount;


    /**
     * Constructor for the Document class.
     * @param filePath     the filepath of the document
     * @param accessAmount the amount of access of the document
     * @param tags         the tags of the document
     */
    protected Document(String filePath, int accessAmount, List<Tag> tags) {
        this.filePath = filePath;
        this.accessAmount = accessAmount;
        this.tags = new ArrayList<>(tags);
        this.stringRepresentation = getDocumentAsString();
    }

    private String getDocumentAsString() {
        StringJoiner tagsResult = new StringJoiner(DELIMITER);
        for (Tag tag : tags) {
            tagsResult.add(tag.toString());
        }
        StringJoiner result = new StringJoiner(DELIMITER);

        result.add(filePath);
        result.add(getDocumentType());
        result.add(String.valueOf(getAccessAmount()));
        if (tagsResult.length() != 0) {
            result.add(tagsResult.toString());
        }

        return result.toString();
    }

    /**
     * Returns the {@link #filePath} of the document.
     * @return file path of the document
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Returns the {@link #accessAmount} of the document.
     * @return access amount of the document
     */
    public int getAccessAmount() {
        return accessAmount;
    }

    /**
     * Sets the {@link #accessAmount} of the document.
     * @param accessAmount the new access amount
     */
    public void setAccessAmount(int accessAmount) {
        this.accessAmount = accessAmount;
    }

    /**
     * Returns the List {@link #tags} of the document.
     * @return tags of the document
     */
    public List<Tag> getTags() {
        return Collections.unmodifiableList(tags);
    }

    /**
     * Adds a tag to the List of Tags {@link #tags}.
     * @param tag the tag to be added
     */
    public void addTag(Tag tag) {
        tags.add(tag);
    }

    /**
     * Removes a tag with a given identifier from the {@link #tags}.
     * @param identifier the identifier of the tag to be removed
     */
    public void removeTag(String identifier) {
        tags.removeIf(tag -> tag.getIdentifier().equalsIgnoreCase(identifier));
    }

    /**
     * Returns the tag of the List of Tags {@link #tags} or an Tag with an UNDEFINED identifier from {@link DefaultValueRange}.
     * @param identifier the identifier of the wanted tag
     * @return the tag or an undefined tag
     */
    public Optional<Tag> getSpecificTag(String identifier) {
        return tags.stream().filter(tag -> tag.getIdentifier().equalsIgnoreCase(identifier)).findFirst();
    }

    /**
     * Abstract method to return the {@link kit.edu.informatik.model.DocumentTypes}.
     * @return the type of document
     */
    public abstract String getDocumentType();

    /**
     * Abstract method to transform tags into default presets.
     * @throws InvalidArgumentException when the transformationen runs into an error
     */
    public abstract void transformDefaultTags() throws InvalidArgumentException;

    /**
     * Overrides the toString method of the Object class, returning a string representation of the document.
     * @return a string representation of the document
     */
    @Override
    public String toString() {
        return stringRepresentation;
    }
}
