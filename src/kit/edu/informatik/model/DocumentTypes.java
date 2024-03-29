package kit.edu.informatik.model;

/**
 * Enum for the different types of documents.
 *
 * @author uqtwh
 * @version 1.0
 */
public enum DocumentTypes {

    /**
     * The type of an image.
     */
    IMAGE,

    /**
     * The type of an audio file.
     */
    AUDIO,

    /**
     * The type of a video file.
     */
    VIDEO,

    /**
     * The type of a text file.
     */
    TEXT,

    /**
     * The type of a program file.
     */
    PROGRAM;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
