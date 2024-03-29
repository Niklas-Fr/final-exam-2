package kit.edu.informatik.model.documents;

import kit.edu.informatik.exceptions.InvalidArgumentException;
import kit.edu.informatik.model.DocumentTypes;
import kit.edu.informatik.model.defaults.ImageDefaults;
import kit.edu.informatik.model.documenttags.MultivalentTag;
import kit.edu.informatik.model.documenttags.Tag;

import java.util.List;

/**
 * The class models an ImageDocument.
 * Inherits from {@link Document}.
 *
 * @author uqtwh
 * @version 1.0
 * @see Document
 */
public final class ImageDocument extends Document {
    private static final String OLD_TAG = "size";
    private static final String NEW_TAG = "imagesize";
    private static final int ICON_SIZE_THRESHOLD = 10000;
    private static final int SMALL_SIZE_THRESHOLD = 40000;
    private static final int MEDIUM_SIZE_THRESHOLD = 80000;

    /**
     * Constructor of the ImageDocument.
     * @param filePath     the file path of the document
     * @param accessAmount the access amount
     * @param tags         the tags
     */
    public ImageDocument(String filePath, int accessAmount, List<Tag> tags) {
        super(filePath, accessAmount, tags);
    }

    @Override
    public void transformDefaultTags() throws InvalidArgumentException {
        if (getSpecificTag(OLD_TAG).isPresent() && getSpecificTag(NEW_TAG.toLowerCase()).isPresent()) {
            throw new InvalidArgumentException(INVALID_TAGS.formatted(OLD_TAG, NEW_TAG));
        }
        if (getSpecificTag(OLD_TAG).isPresent()) {
            int size;
            try {
                size = Integer.parseInt(getSpecificTag(OLD_TAG).get().getValue());
            } catch (NumberFormatException e) {
                throw new InvalidArgumentException(INVALID_VALUE.formatted(OLD_TAG));
            }
            removeTag(OLD_TAG);
            addTag(new MultivalentTag(NEW_TAG, getImageSize(size).toString().toLowerCase()));
        }
    }

    /**
     * Transforms a given size into a {@link ImageDefaults}.
     * @param size the length
     * @return the image default
     */
    private ImageDefaults getImageSize(int size) {
        if (size < ICON_SIZE_THRESHOLD) {
            return ImageDefaults.ICON;
        } else if (size < SMALL_SIZE_THRESHOLD) {
            return ImageDefaults.SMALL;
        } else if (size < MEDIUM_SIZE_THRESHOLD) {
            return ImageDefaults.MEDIUM;
        } else {
            return ImageDefaults.LARGE;
        }
    }

    @Override
    public String getDocumentType() {
        return DocumentTypes.IMAGE.toString();
    }
}
