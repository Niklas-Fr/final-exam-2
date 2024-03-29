package kit.edu.informatik.model;

import kit.edu.informatik.model.documents.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * The class represents a data object with a List of {@link Document}s.
 *
 * @author uqtwh
 * @version 1.0
 * @see Document
 */
public final class Data {
    private final List<Document> documents;
    private int id;

    /**
     * Constructor for the Data class.
     * @param documents the list of documents
     * @param id        the id of the data
     */
    public Data(List<Document> documents, int id) {
        this.documents = new ArrayList<>(documents);
        this.id = id;
    }

    /**
     * Returns the an unmodifiable copy of {@link #documents}.
     * @return the documents of the data
     */
    public List<Document> getDocuments() {
        return Collections.unmodifiableList(documents);
    }

    /**
     * Returns the {@link #id} of the data.
     * @return the id of the data
     */
    public int getID() {
        return id;
    }

    /**
     * Setter for the {@link #id} of the data.
     * @param id the new id
     */
    public void setID(int id) {
        this.id = id;
    }

    /**
     * Returns an Optional of the Document with the given file path.
     * @param filePath the file path of the document
     * @return Optional of the document, empty if not found
     */
    public Optional<Document> getDocument(String filePath) {
        return documents.stream().filter(document -> document.getFilePath().equals(filePath)).findFirst();
    }
}
