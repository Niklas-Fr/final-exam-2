package kit.edu.informatik.model.filetree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The class models a Node in the tree of the file directory.
 *
 * @author uqtwh
 * @version 1.0
 * @see kit.edu.informatik.model.FileModel
 */
public final class Node {
    private final String tagName;
    private final String filePath;
    private final String value;
    private final List<Node> children = new ArrayList<>();

    /**
     * Contructor of the Node.
     * @param tagName  the name of the tag to part
     * @param value    the value of the tag to part
     * @param filePath the document
     */
    public Node(String tagName, String value, String filePath) {
        this.tagName = tagName;
        this.value = value;
        this.filePath = filePath;
    }

    /**
     * Returns the {@link #tagName}, meaning the identifier of the tag.
     * @return the identifier of the tag
     */
    public String getTagName() {
        return tagName.toLowerCase();
    }

    /**
     * Returns the {@link #value} of the tag.
     * @return the value of the tag
     */
    public String getValue() {
        return value;
    }

    /**
     * Returns the {@link #filePath} of the document.
     * @return the file path
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Adds a List of {@link Node}s, representing children, to the Node.
     * @param newChildren the new children
     */
    public void addChildren(List<Node> newChildren) {
        this.children.addAll(newChildren);
    }

    /**
     * Returns an unmodifiable List of the {@link #children}.
     * @return list of children
     */
    public List<Node> getChildren() {
        return Collections.unmodifiableList(children);
    }

    /**
     * Returns wether the Node has children.
     * @return {@code true} if the Node has children, {@code false} otherwise
     */
    public boolean hasChildren() {
        return !children.isEmpty();
    }
}
