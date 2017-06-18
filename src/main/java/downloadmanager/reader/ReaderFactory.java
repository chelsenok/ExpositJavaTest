package downloadmanager.reader;

import static downloadmanager.reader.ReaderFactory.Formats.CSV;
import static downloadmanager.reader.ReaderFactory.Formats.JSON;
import static downloadmanager.reader.ReaderFactory.Formats.XML;

public abstract class ReaderFactory {

    public interface Formats {

        String CSV = "csv";
        String XML = "xml";
        String JSON = "json";
    }

    public static Reader getStrategy(final String fileFormat) {
        switch (fileFormat) {
            case CSV:
                return new CsvReader();
            case XML:
                return new XmlReader();
            case JSON:
                return new JsonReader();
        }
        return null;
    }

}
