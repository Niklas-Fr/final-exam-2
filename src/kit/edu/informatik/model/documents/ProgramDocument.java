package kit.edu.informatik.model.documents;

import kit.edu.informatik.exceptions.InvalidArgumentException;
import kit.edu.informatik.model.DocumentTypes;
import kit.edu.informatik.model.defaults.ProgramDefaults;
import kit.edu.informatik.model.documenttags.BinaryTag;
import kit.edu.informatik.model.documenttags.Tag;

import java.util.List;

/**
 * The class models an ProgramDocument.
 * Inherits from {@link Document}.
 *
 * @author uqtwh
 * @version 1.0
 * @see Document
 */
public final class ProgramDocument extends Document {
    private static final String INVALID_TAG = "Tag %s cannot be mutlivalent.";

    /**
     * Constructor of the ProgramDocument.
     * @param filePath     the file path of the document
     * @param accessAmount the access amount
     * @param tags         the tags
     */
    public ProgramDocument(String filePath, int accessAmount, List<Tag> tags) {
        super(filePath, accessAmount, tags);
    }

    @Override
    public void transformDefaultTags() throws InvalidArgumentException {
        String programDefault = ProgramDefaults.EXECUTABLE.toString().toLowerCase();
        if (getSpecificTag(programDefault).isPresent() && getSpecificTag(programDefault).get().isMultivalent()) {
            throw new InvalidArgumentException(INVALID_TAG.formatted(programDefault));
        }
        addTag(new BinaryTag(programDefault));
    }

    @Override
    public String getDocumentType() {
        return DocumentTypes.PROGRAM.toString();
    }
}
