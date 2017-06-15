package downloadmanager.readstrategy;

import downloadmanager.File;

public class XmlReader implements ReadStrategy {

    public static final String ROOT_MESSAGE = "XML-root element name: ";
    public static final String REFERENCE_MESSAGE = "XML-reference element name: ";
    public static final String PATH_MESSAGE = "XML-path element name: ";

    private final String mRoot;
    private final String mReference;
    private final String mPath;

    public XmlReader(final String root, final String reference, final String path) {
        mRoot = root;
        mReference = reference;
        mPath = path;
    }

    public File[] readFiles(final String path) {
        return new File[0];
    }
}
