package kit.edu.informatik.model.documents;

import kit.edu.informatik.exceptions.InvalidArgumentException;
import kit.edu.informatik.model.DocumentTypes;
import kit.edu.informatik.model.defaults.TextDefaults;
import kit.edu.informatik.model.documenttags.MultivalentTag;
import kit.edu.informatik.model.documenttags.Tag;

import java.util.List;

/**
 * The class models an TextDocument.
 * Inherits from {@link Document}.
 *
 * @author uqtwh
 * @version 1.0
 * @see Document
 */
public final class TextDocument extends Document {
    private static final String OLD_LENGTH_TAG = "words";
    private static final String NEW_LENGTH_TAG = "textlength";
    private static final String NEW_GENRE_TAG = "TextGenre";
    private static final int SHORT_SIZE_THRESHOLD = 100;
    private static final int MEDIUM_SIZE_THRESHOLD = 1000;

    /**
     * Constructor of the TextDocument.
     * @param filePath     the file path of the document
     * @param accessAmount the access amount
     * @param tags         the tags
     */
    public TextDocument(String filePath, int accessAmount, List<Tag> tags) {
        super(filePath, accessAmount, tags);
    }

    @Override
    public void transformDefaultTags() throws InvalidArgumentException {
        if (getSpecificTag(OLD_LENGTH_TAG).isPresent() && getSpecificTag(NEW_LENGTH_TAG.toLowerCase()).isPresent()) {
            throw new InvalidArgumentException(INVALID_TAGS.formatted(OLD_LENGTH_TAG, NEW_LENGTH_TAG));
        }
        if (getSpecificTag(OLD_LENGTH_TAG).isPresent()) {
            int length;
            try {
                length = Integer.parseInt(getSpecificTag(OLD_LENGTH_TAG).get().getValue());
            } catch (NumberFormatException e) {
                throw new InvalidArgumentException(INVALID_VALUE.formatted(OLD_LENGTH_TAG));
            }
            removeTag(OLD_LENGTH_TAG);
            addTag(new MultivalentTag(NEW_LENGTH_TAG, getVideoLength(length).toString().toLowerCase()));
        }

        if (getSpecificTag(OLD_GENRE_TAG).isPresent() && getSpecificTag(NEW_GENRE_TAG.toLowerCase()).isPresent()) {
            throw new InvalidArgumentException(INVALID_TAGS.formatted(OLD_GENRE_TAG, NEW_GENRE_TAG));
        }
        if (getSpecificTag(OLD_GENRE_TAG).isPresent()) {
            String genre = getSpecificTag(OLD_GENRE_TAG).get().getValue();
            removeTag(OLD_GENRE_TAG);
            addTag(new MultivalentTag(NEW_GENRE_TAG.toLowerCase(), genre));
        }
    }

    /**
     * Transforms a given length into a {@link TextDefaults}.
     * @param length the length
     * @return the text default
     */
    private TextDefaults getVideoLength(int length) {
        if (length < SHORT_SIZE_THRESHOLD) {
            return TextDefaults.SHORT;
        } else if (length < MEDIUM_SIZE_THRESHOLD) {
            return TextDefaults.MEDIUM;
        } else {
            return TextDefaults.LONG;
        }
    }

    @Override
    public String getDocumentType() {
        return DocumentTypes.TEXT.toString();
    }
}
