package downloadmanager.readstrategy;

import downloadmanager.File;

public class JsonReader implements ReadStrategy {

    public static final String ROOT_MESSAGE = "JSON-root element key: ";
    public static final String REFERENCE_MESSAGE = "JSON-reference element key: ";
    public static final String PATH_MESSAGE = "JSON-path element key: ";

    private final String mRoot;
    private final String mReference;
    private final String mPath;

    public JsonReader(final String root, final String reference, final String path) {
        mRoot = root;
        mReference = reference;
        mPath = path;
    }

    public File[] readFiles(final String path) {
        return new File[0];
    }
}
