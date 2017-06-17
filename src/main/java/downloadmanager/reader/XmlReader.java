package downloadmanager.reader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import downloadmanager.File;

public class XmlReader implements Reader {

    static final String ROOT_MESSAGE = "XML-item element name: ";
    static final String REFERENCE_MESSAGE = "XML-reference element name: ";
    static final String PATH_MESSAGE = "XML-path element name: ";

    private final String mRoot;
    private final String mReference;
    private final String mPath;

    XmlReader(final String root, final String reference, final String path) {
        mRoot = root;
        mReference = reference;
        mPath = path;
    }

    public File[] readFiles(final String path, final String downloadPath) {
        try {
            final List<File> fileList = new ArrayList<>();
            final java.io.File inputFile = new java.io.File(path);
            final DocumentBuilderFactory dbFactory
                    = DocumentBuilderFactory.newInstance();
            final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            final Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            final NodeList nList = doc.getElementsByTagName(mRoot);
            for (int temp = 0; temp < nList.getLength(); temp++) {
                final Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    final Element eElement = (Element) nNode;
                    fileList.add(new File(
                            eElement.getElementsByTagName(mReference)
                                    .item(0)
                                    .getTextContent(),
                            downloadPath + eElement.getElementsByTagName(mPath)
                                    .item(0)
                                    .getTextContent()
                    ));
                }
            }
            return fileList.toArray(new File[fileList.size()]);
        } catch (final SAXException | IOException | ParserConfigurationException ignored) {
            return null;
        }
    }
}
