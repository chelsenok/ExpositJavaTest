package downloadmanager.reader;

import static downloadmanager.reader.ReadStrategyFactory.Formats.CSV;
import static downloadmanager.reader.ReadStrategyFactory.Formats.JSON;
import static downloadmanager.reader.ReadStrategyFactory.Formats.XML;

public abstract class ReadStrategyFactory {

    public interface Formats {

        String CSV = "csv";
        String XML = "xml";
        String JSON = "json";
    }

    public static ReadStrategy getStrategy(final String fileFormat, final Input askable) {
        switch (fileFormat) {
            case CSV:
                return new CsvReader();
            case XML:
                return new XmlReader(
                        askable.ask(XmlReader.ROOT_MESSAGE),
                        askable.ask(XmlReader.REFERENCE_MESSAGE),
                        askable.ask(XmlReader.PATH_MESSAGE)
                );
            case JSON:
                return new JsonReader(
                        askable.ask(JsonReader.REFERENCE_MESSAGE),
                        askable.ask(JsonReader.PATH_MESSAGE)
                );
        }
        return null;
    }

}
