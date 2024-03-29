package kit.edu.informatik.commands;

import kit.edu.informatik.exceptions.InvalidArgumentException;
import kit.edu.informatik.model.Data;
import kit.edu.informatik.model.DocumentTypes;
import kit.edu.informatik.model.FileModel;
import kit.edu.informatik.model.documents.AudioDocument;
import kit.edu.informatik.model.documents.Document;
import kit.edu.informatik.model.documents.ImageDocument;
import kit.edu.informatik.model.documents.ProgramDocument;
import kit.edu.informatik.model.documents.TextDocument;
import kit.edu.informatik.model.documents.VideoDocument;
import kit.edu.informatik.model.documenttags.BinaryTag;
import kit.edu.informatik.model.documenttags.MultivalentTag;
import kit.edu.informatik.model.documenttags.NumericTag;
import kit.edu.informatik.model.documenttags.Tag;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;


/**
 * Command to load data from a file into the {@link FileModel}.
 * Implements interface {@link Command}.
 *
 * @author uqtwh
 * @version 1.0
 * @see Command
 */
public final class LoadCommand implements Command {
    private static final String INVALID_FILE_PATH = "File \"%s\" does not exist.";
    private static final String DEFAULT_PATH = "src/kit/edu/informatik/%s";
    private static final String INVALID_FILE = "File is empty.";
    private static final String FILE_ERROR = "File could not be read.";
    private static final String INVALID_DOCUMENT = "Invalid document \"%s\" read.";
    private static final String INVALID_DOCUMENT_PATH = "Path %s contains invalid character \"%s\".";
    private static final String INVALID_DOCUMENT_TYPE = "Document \"%s\" has an invalid type.";
    private static final String INVALID_ACCESS_AMOUNT = "Document \"%s\" has an invalid access amount %d, has to be non negative.";
    private static final String DUPLICATE_TAG = "Tag \"%s\" already exists, possibly as another Type.";
    private static final String INVALID_ACCESS_AMOUNTS = "There has to be atleast one Document with access amount > 0";
    private static final String TAG_PATTERN = "^[A-Za-z][A-Za-z0-9]*$";
    private static final String INVALID_TAG_NAME = "Tag \"%s\" has to match following pattern: "
            + TAG_PATTERN.substring(1, TAG_PATTERN.length() - 2);
    private static final String DELIMITER = ",";
    private static final String INVALID_CHARACTER = " ";
    private static final String TAG_DELIMITER = "=";
    private static final int MINIMUM_DOCUMNET_ARGUMENTS = 3;

    /**
     * Loads a text data from a given file directory in the program. Checks for valid input.
     * @param fileModel        the {@link FileModel} to execute the command on
     * @param commandArguments the arguments of the command
     * @return a {@link CommandResult} with a corresponding message
     */
    @Override
    public CommandResult execute(FileModel fileModel, Arguments commandArguments) {
        try {
            String filePath = commandArguments.nextString();
            commandArguments.assertNoMoreArguments();

            List<String> input = readData(filePath);
            List<Document> documents = validateList(input);
            Data data = new Data(documents, -1);
            return new CommandResult(CommandResultType.SUCCESS, fileModel.loadData(data, filePath));
        } catch (InvalidArgumentException e) {
            return new CommandResult(CommandResultType.FAILURE, e.getMessage());
        }
    }

    /**
     * Private helper method for {@link #execute(FileModel, Arguments) execute}, reading the data from a given file.
     * Returns the list of lines read from the file.
     * @param path the path of the file
     * @return the list of lines read from the file
     * @throws InvalidArgumentException if the file does not exist
     * @throws InvalidArgumentException if the file could not be read
     */
    private List<String> readData(String path) throws InvalidArgumentException {
        List<String> lines = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                lines.add(line);
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            throw new InvalidArgumentException(INVALID_FILE_PATH.formatted(path.replace(DEFAULT_PATH, "")));
        } catch (IOException e) {
            throw new InvalidArgumentException(FILE_ERROR);
        }
        return lines;
    }

    /**
     * Private helper method for {@link #execute(FileModel, Arguments) execute}, validating the list of documents
     * read from the file and converting them to a list of {@link Document}s.
     * @param input the list of documents as Strings read from the file
     * @return the list of documents as {@link Document}
     * @throws InvalidArgumentException if the file is empty
     * @throws InvalidArgumentException if a document is invalid
     */
    private List<Document> validateList(List<String> input) throws InvalidArgumentException {
        List<Document> result = new ArrayList<>();
        if (input.isEmpty()) {
            throw new InvalidArgumentException(INVALID_FILE);
        }

        for (String line : input) {
            String[] data = line.split(DELIMITER);
            Arguments arguments = new Arguments(data);
            if (arguments.getLength() < MINIMUM_DOCUMNET_ARGUMENTS) {
                throw new InvalidArgumentException(INVALID_DOCUMENT.formatted(String.join(DELIMITER, data)));
            }

            String path = arguments.nextString();
            if (path.length() != path.replace(INVALID_CHARACTER, "").length()) {
                throw new InvalidArgumentException(INVALID_DOCUMENT_PATH.formatted(path, INVALID_CHARACTER));
            }

            Optional<DocumentTypes> documentType = value(arguments.nextString().toLowerCase());
            if (documentType.isEmpty()) {
                throw new InvalidArgumentException(INVALID_DOCUMENT_TYPE.formatted(String.join(DELIMITER, data)));
            }

            int accessAmount = arguments.nextInt();
            if (accessAmount < 0) {
                throw new InvalidArgumentException(INVALID_ACCESS_AMOUNT.formatted(String.join(DELIMITER, data), accessAmount));
            }

            List<String> tagStrings = new ArrayList<>();
            while (arguments.hasNext()) {
                tagStrings.add(arguments.nextString());
            }
            List<Tag> tags = validateTags(tagStrings, documentType.get());
            Document currentDocument = instantiateDocument(path, documentType.get(), accessAmount, tags);
            currentDocument.transformDefaultTags();
            result.add(currentDocument);
        }
        Optional<Tag> clashingTag = differentTagTypes(result);
        if (clashingTag.isPresent()) {
            throw new InvalidArgumentException(DUPLICATE_TAG.formatted(clashingTag.get().getIdentifier()));
        }

        if (result.stream().map(Document::getAccessAmount).reduce(0, Integer::sum) == 0) {
            throw new InvalidArgumentException(INVALID_ACCESS_AMOUNTS);
        }
        return result;
    }

    /**
     * Private helper method for {@link #validateList(List) validateList}, validating the tags of a document and returning
     * a List of {@link Tag}s.
     * @param tagStrings   the list of tags as Strings
     * @param documentType the type of the document
     * @return the List of Tags
     * @throws InvalidArgumentException if a tag is invalid
     */
    private List<Tag> validateTags(List<String> tagStrings, DocumentTypes documentType) throws InvalidArgumentException {
        List<Tag> tags = new ArrayList<>();
        for (String tagString : tagStrings) {
            String[] tagData = tagString.split(TAG_DELIMITER);
            if (!matchesPattern(tagData[0])) {
                throw new InvalidArgumentException(INVALID_TAG_NAME.formatted(tagData[0]));
            }
            if (tagExisits(tags, tagData[0]).isPresent()) {
                throw new InvalidArgumentException(DUPLICATE_TAG.formatted(tagData[0]));
            }
            if (tagData.length == 1) {
                tags.add(new BinaryTag(tagData[0]));
            } else {
                try {
                    int value = Integer.parseInt(tagData[1]);
                    tags.add(new NumericTag(tagData[0], value));
                } catch (NumberFormatException e) {
                    if (!matchesPattern(tagData[1])) {
                        throw new InvalidArgumentException(INVALID_TAG_NAME.formatted(tagData[1]));
                    }
                    tags.add(new MultivalentTag(tagData[0], tagData[1]));
                }
            }
        }
        return tags;
    }

    /**
     * Private helper method for {@link #validateList(List)}, converting a String to a {@link DocumentTypes}.
     * @param documentType the String to be converted
     * @return Optional of the {@link DocumentTypes type}, otherwise an empty Optional
     */
    private Optional<DocumentTypes> value(String documentType) {
        return Arrays.stream(DocumentTypes.values()).filter(type -> type.name().toLowerCase().equals(documentType)).findFirst();
    }

    /**
     * Private helper method for {@link #validateTags(List, DocumentTypes)}, checking if a String matches {@link #TAG_PATTERN}.
     * @param value the line to be checked
     * @return true if the line matches the pattern, otherwise false
     */
    private boolean matchesPattern(String value) {
        return Pattern.matches(TAG_PATTERN, value.replace(INVALID_CHARACTER, ""));
    }

    /**
     * Private helper method for {@link #validateTags(List, DocumentTypes)}, checking if a tag with a given identifier
     * already exists.
     * @param tags       the list of tags
     * @param identifier the identifier to be checked
     * @return Optional of the Tag if it exists, otherwise an empty Optional
     */
    private Optional<Tag> tagExisits(List<Tag> tags, String identifier) {
        return tags.stream().filter(tag -> tag.getIdentifier().equals(identifier)).findFirst();
    }

    /**
     * Private helper method for {@link #validateList(List)}, checking if a list of documents contains tags of different types.
     * @param documents the list of documents
     * @return Optional of the first tag with different types, otherwise an empty Optional
     */
    private Optional<Tag> differentTagTypes(List<Document> documents) {
        List<Tag> allTags = new ArrayList<>();
        documents.forEach(document -> allTags.addAll(document.getTags()));
        return allTags.stream().filter(tag -> tagExisits(allTags, tag.getIdentifier()).isPresent()
                && tag.isMultivalent() != tagExisits(allTags, tag.getIdentifier()).get().isMultivalent()).findFirst();
    }

    /**
     * Private helper method for {@link #validateList(List)}, instantiating a subclass object of {@link Document},
     * depending on the document type.
     * @param path         the file path of the document
     * @param documentType the type of the document
     * @param accessAmount the access amount of the document
     * @param tags         the list of tags of the document
     * @return the instantiated Document
     */
    private Document instantiateDocument(String path, DocumentTypes documentType, int accessAmount, List<Tag> tags) {
        if (documentType == DocumentTypes.TEXT) {
            return new TextDocument(path, accessAmount, tags);
        } else if (documentType == DocumentTypes.IMAGE) {
            return new ImageDocument(path, accessAmount, tags);
        } else if (documentType == DocumentTypes.AUDIO) {
            return new AudioDocument(path, accessAmount, tags);
        } else if (documentType == DocumentTypes.VIDEO) {
            return new VideoDocument(path, accessAmount, tags);
        } else if (documentType == DocumentTypes.PROGRAM) {
            return new ProgramDocument(path, accessAmount, tags);
        } else {
            throw new IllegalStateException();
        }
    }
}
