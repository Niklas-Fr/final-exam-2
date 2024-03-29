package kit.edu.informatik.model;

import kit.edu.informatik.model.documents.Document;
import kit.edu.informatik.model.documenttags.MultivalentTag;
import kit.edu.informatik.model.documenttags.Tag;
import kit.edu.informatik.model.filetree.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * The class models the file model, managing the documents.
 *
 * @author uqtwh
 * @version 1.0
 */
public final class FileModel {
    private static final String SUCCESSFUL_LOADING = "Loaded %s with id: %d%s";
    private static final String SUCCESSFUL_CHANGE = "Change %d to %d for %s";
    private static final String KEY_VALUE_PATTERN = File.separator + "%s=%s";
    private static final String FILE_PATH_OUTPUT = File.separator + "\"%s\"";
    private static final String ROUNDING_PATTERN = "%.2f";
    private static final String OUTPUT_SEPERATOR = "---";
    private static final double MINIMUM_EXPECTED_INFORMATION_GAIN = Math.pow(10, -3);
    private final List<Data> dataList = new ArrayList<>();
    private Node fileTreeRoot;

    /**
     * Loads a {@link Data} Object into the model.
     * @param data     the data object
     * @param filePath the file path of the loaded documents
     * @return a string representation of the loaded documents
     */
    public String loadData(Data data, String filePath) {
        StringJoiner result = new StringJoiner(System.lineSeparator());
        dataList.add(data);

        data.getDocuments().stream().map(Document::toString).toList().forEach(result::add);
        data.setID(dataList.size() - 1);
        return SUCCESSFUL_LOADING.formatted(filePath, data.getID(), System.lineSeparator() + result);
    }

    /**
     * Changes the access amount of a document.
     * @param document          the document to be changed
     * @param newAccessNumber   the new access number
     * @return Success message
     */
    public String changeData(Document document, int newAccessNumber) {
        int oldAccessAmount = document.getAccessAmount();
        document.setAccessAmount(newAccessNumber);
        return SUCCESSFUL_CHANGE.formatted(oldAccessAmount, newAccessNumber, document.getFilePath());
    }

    /**
     * Sorts the {@link Document}s given {@link Data} object and returns the result, showcasing the information gain of
     * each tag to sort by.
     * @param data the data object
     * @return String representation of the sorted documents
     */
    public String run(Data data) {
        sortingAlgorithm(data.getDocuments());

        StringJoiner result = new StringJoiner(System.lineSeparator());
        showInformationGain(fileTreeRoot, data.getDocuments()).forEach(result::add);
        result.add(OUTPUT_SEPERATOR);
        showFilePathOfTree(fileTreeRoot).forEach(result::add);
        return result.toString();
    }

    /**
     * Returns a map with keys being the tags in a list of documents and the values the information gain for each tag.
     * @param documents the list of documents
     * @return the map of information gains
     */
    private Map<String, Double> getInformationGain(List<Document> documents) {
        int sumOfAccesses = documents.stream().map(Document::getAccessAmount).reduce(0, Integer::sum);
        // Putting the access probabilites of each document in a map
        Map<String, Double> accessProbabilities = new HashMap<>();
        documents.forEach(document -> accessProbabilities.put(document.getFilePath(),
                (double) document.getAccessAmount() / sumOfAccesses));

        // Collects all tags in the list of documents
        List<Tag> allTags = new ArrayList<>();
        documents.forEach(document -> document.getTags().
                stream().filter(tag -> !containsTag(allTags, tag.getIdentifier())).forEach(allTags::add));

        double generalUncertainty = calculateUncertainty(documents);

        // Putting the information gain of each tag  in a map
        Map<String, Double> informationGain = new HashMap<>();
        for (Tag tag : allTags) {
            documents.stream().filter(document -> !containsTag(document.getTags(), tag.getIdentifier())).
                    forEach(document -> document.addTag(new MultivalentTag(tag.getIdentifier(), DefaultValueRange.UNDEFINED.toString())));

            double informationGained = generalUncertainty - calculateValuedUncertainty(documents, accessProbabilities, tag);
            informationGain.put(tag.getIdentifier(), informationGained);
        }
        return informationGain;
    }

    /**
     * Calculates the Uncertainty from a List of documents, meaning H(D).
     * @param documents the list of documents
     * @return the uncertainty
     */
    private double calculateUncertainty(List<Document> documents) {
        int sumAccess = documents.stream().map(Document::getAccessAmount).reduce(0, Integer::sum);
        Map<String, Double> accessProbabilities = new HashMap<>();
        documents.forEach(document -> accessProbabilities.put(document.getFilePath(), (double) document.getAccessAmount() / sumAccess));

        // Getting the log base 2 of the numbers, meaning taking log(x) / log(2) since Math.log has base e.
        return documents.stream().map(document -> -accessProbabilities.get(document.getFilePath())
                * (Math.log(accessProbabilities.get(document.getFilePath())) / Math.log(2))).reduce(0.0, Double::sum);
    }

    /**
     * Caculates the valued Uncertainty, meaning H(D t=v)
     * @param documents           the list of documents
     * @param accessProbabilities the access probabilites of the documents
     * @param tag                 the tag
     * @return the valued uncertainty
     */
    private double calculateValuedUncertainty(List<Document> documents, Map<String, Double> accessProbabilities, Tag tag) {
        return getValues(documents, tag.getIdentifier()).stream().
                map(value -> probabilityBeingInSubset(tag.getIdentifier(), value, accessProbabilities, documents)
                        * calculateUncertainty(getListWithValue(tag.getIdentifier(), value, documents))).reduce(0.0, Double::sum);
    }

    /**
     * Returns the probability of a Document being in a Subset of Documents, where each documents has a given tag
     * with a given value, meaning p(D t=v, D)
     * @param tagIdentifier       the identifier of the tag
     * @param tagValue            the value of the tag
     * @param accessProbabilities the access probablilies as a mpa
     * @param documents           the list of documents
     * @return the probability
     */
    private double probabilityBeingInSubset(String tagIdentifier, String tagValue,
                                            Map<String, Double> accessProbabilities, List<Document> documents) {
        // No isPresent() check since getListWithTag already asserts that the documents have the specific tag
        return getListWithTag(tagIdentifier, documents).stream().
                filter(document -> document.getSpecificTag(tagIdentifier).get().getValue().equals(tagValue)).
                map(document -> accessProbabilities.get(document.getFilePath())).reduce(0.0, Double::sum);
    }

    private boolean containsTag(List<Tag> tags, String tagIdentifier) {
        return tags.stream().anyMatch(tag -> tag.getIdentifier().equalsIgnoreCase(tagIdentifier));
    }

    /**
     * Returns a subset of a List of Documents, where each Document has a given tag.
     * @param tagIdentifier the identifier of the tag
     * @param documents     the list of documents
     * @return the subset
     */
    private List<Document> getListWithTag(String tagIdentifier, List<Document> documents) {
        return documents.stream().filter(document -> document.getSpecificTag(tagIdentifier).isPresent()).toList();
    }

    /**
     * Returns a subset of a List of Documents, where each Document has a given tag with a given value.
     * @param tagIdentifier the identifier of the tag
     * @param tagValue      the value of the tag
     * @param documents     the list of documents
     * @return the subset
     */
    private List<Document> getListWithValue(String tagIdentifier, String tagValue, List<Document> documents) {
        // No isPresent() check since getListWithTag already asserts that the documents have the specific tag
        return  getListWithTag(tagIdentifier, documents).stream().
                filter(document -> document.getSpecificTag(tagIdentifier).get().getValue().equals(tagValue)).toList();
    }

    private void sortingAlgorithm(List<Document> documents) {
        fileTreeRoot = new Node("", "", "");
        fileTreeRoot.addChildren(createFileTree(documents));
    }

    /**
     * Creates a file tree via recursion with {@link Node}s for a list of documents which is sorted by information gain.
     * @param documents the list of documents
     * @return the List of Nodes
     */
    private List<Node> createFileTree(List<Document> documents) {
        String tagIdentifier = getHighestInformationGain(documents);
        // If there is no information gain, return each document in the list
        if (tagIdentifier.isEmpty()) {
            List<Node> nodes = new ArrayList<>();
            documents.forEach(document -> nodes.add(new Node("", "", document.getFilePath())));
            return nodes;
        }

        // Splitting the List into sublists over the tag with the highest information gain
        List<String> values = getValues(getListWithTag(tagIdentifier, documents), tagIdentifier);
        Map<List<Document>, String> sublists = new HashMap<>();
        values.forEach(value -> sublists.put(getListWithValue(tagIdentifier, value, getListWithTag(tagIdentifier, documents)), value));

        // For each sublist: either just add the documents, or if the information gain is high enough, call the method via recursion
        List<Node> currentNodes = new ArrayList<>();
        for (Entry<List<Document>, String> entry : sublists.entrySet()) {
            Map<String, Double> informationGain = getInformationGain(entry.getKey());
            double tagInformationGain = informationGain.values().stream().reduce(0.0, Double::sum);

            if (tagInformationGain > MINIMUM_EXPECTED_INFORMATION_GAIN) {
                Node node = new Node(tagIdentifier, entry.getValue(), "");
                node.addChildren(createFileTree(entry.getKey()));
                currentNodes.add(node);
            } else {
                for (Document document : entry.getKey()) {
                    currentNodes.add(new Node(tagIdentifier, entry.getValue(), document.getFilePath()));
                }
            }
        }

        // Sort the entrySet first via access probability, then alpabetically by tag value, then alphabetically by file path
        currentNodes = currentNodes.stream().sorted(Comparator.<Node>comparingDouble(node -> {
            double sumOfValuedAccesses = getListWithValue(tagIdentifier, node.getValue(), documents).stream().
                    mapToDouble(Document::getAccessAmount).sum();
            double sumOfAccesses = documents.stream().mapToDouble(Document::getAccessAmount).sum();
            return -sumOfValuedAccesses / sumOfAccesses;
        }
        ).thenComparing(Node::getValue).thenComparing(Node::getFilePath)).toList();
        return currentNodes;
    }

    /**
     * Returns a String representation of the information gain of each node of the file tree of a list of
     * documents via recursion.
     * @param currentNode      the node used for recursion
     * @param currentDocuments the list of document
     * @return the String representation
     */
    private List<String> showInformationGain(Node currentNode, List<Document> currentDocuments) {
        if (currentDocuments.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> result = new ArrayList<>();
        Map<String, Double> entries = getInformationGain(currentDocuments);

        // Sort the entries of the map with information gains by information gain
        List<Entry<String, Double>> sortedEntries = entries.entrySet().stream().
                sorted(Comparator.comparingDouble(entry -> -entry.getValue())).toList();

        // If the informatino gain is high enough, add the entry with a pattern to the result list
        sortedEntries.stream().
                filter(entry -> entry.getValue() > MINIMUM_EXPECTED_INFORMATION_GAIN).
                forEach(entry -> result.add(KEY_VALUE_PATTERN.formatted(entry.getKey().toLowerCase(),
                        ROUNDING_PATTERN.formatted(entry.getValue()))));

        // For each child, add the recursively called information gains to the list
        currentNode.getChildren().forEach(child -> showInformationGain(child,
                getListWithValue(child.getTagName(), child.getValue(), currentDocuments)).
                forEach(childPath -> result.add(
                        KEY_VALUE_PATTERN.formatted(child.getTagName(), getLowerCase(child.getValue())) + childPath)));
        return result;
    }

    /**
     * Returns the optimal file sorting of the documents via recursion.
     * @param currentNode the node used for recursion
     * @return the List of lines, each representing a file directory
     */
    private List<String> showFilePathOfTree(Node currentNode) {
        List<String> result = new ArrayList<>();
        if (currentNode.hasChildren()) {
            // Put the path of each child with the pattern in the list
            currentNode.getChildren().forEach(children -> showFilePathOfTree(children).forEach(childrenPath -> {
                String format = KEY_VALUE_PATTERN.formatted(children.getTagName(), getLowerCase(children.getValue()) + childrenPath);
                result.add(children.getTagName().isEmpty() ? childrenPath : format);
            }));
        } else {
            // Put the document in the list, since the node is a leaf
            result.add(FILE_PATH_OUTPUT.formatted(currentNode.getFilePath()));
        }
        return result;
    }

    /**
     * Returns the value range of a Tag with a given identifier in a list of documents.
     * @param documents       the list of documents
     * @param tagIdentifier   the identifier of the tag
     * @return the value range as a List of Strings
     */
    private List<String> getValues(List<Document> documents, String tagIdentifier) {
        List<String> values = new ArrayList<>();
        documents.forEach(document -> document.getTags().stream().filter(tag -> tag.getIdentifier().equalsIgnoreCase(tagIdentifier)).
                filter(tag -> !values.contains(tag.getValue())).forEach(tag -> values.add(tag.getValue())));
        return values;
    }

    /**
     * Returns the identifier of the tag with the highest information gain from a list of {@link Document}s.
     * If there is none, an empty String is returned.
     * @param documents the list of documents
     * @return the identifier of the tag or an empty String
     */
    private String getHighestInformationGain(List<Document> documents) {
        Optional<Entry<String, Double>> result =
                getInformationGain(documents).entrySet().stream().max(Comparator.comparingDouble(Entry::getValue));
        return result.isPresent() ? result.get().getKey() : "";
    }

    /**
     * Returns a word transformed for the output. If the word is equals to one of the labels, it transformed
     * into lowercase, otherwise nothing happens.
     * @param word the word to transform
     * @return the transformed word
     */
    private String getLowerCase(String word) {
        return word.equals(DefaultValueRange.DEFINED.toString())
                || word.equals(DefaultValueRange.UNDEFINED.toString()) ? word.toLowerCase() : word;
    }

    /**
     * Returns an Optional of the Data with the given id.
     * @param id the id of the data
     * @return Optional of the data, empty if not found
     */
    public Optional<Data> getData(int id) {
        return dataList.stream().filter(data -> data.getID() == id).findFirst();
    }
}
