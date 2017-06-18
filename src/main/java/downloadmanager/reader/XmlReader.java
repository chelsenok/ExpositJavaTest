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

public class XmlReader implements Reader {

    private static final String FILE = "file ";
    private static final String LINK = "link";
    private static final String PATH = "path";

    public String[][] readContent(final String path) {
        try {
            final List<String[]> fileList = new ArrayList<>();
            final java.io.File inputFile = new java.io.File(path);
            final DocumentBuilderFactory dbFactory
                    = DocumentBuilderFactory.newInstance();
            final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            final Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            final NodeList nList = doc.getElementsByTagName(FILE);
            for (int temp = 0; temp < nList.getLength(); temp++) {
                final Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    final Element eElement = (Element) nNode;
                    fileList.add(new String[]{
                            eElement.getElementsByTagName(LINK)
                                    .item(0)
                                    .getTextContent(),
                            eElement.getElementsByTagName(PATH)
                                    .item(0)
                                    .getTextContent()
                    });
                }
            }
            return fileList.toArray(new String[fileList.size()][2]);
        } catch (final SAXException | IOException | ParserConfigurationException ignored) {
            return null;
        }
    }
}
