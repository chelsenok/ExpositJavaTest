package downloadmanager.readstrategy;

import static downloadmanager.readstrategy.ReadStrategyFactory.Formats.CSV;
import static downloadmanager.readstrategy.ReadStrategyFactory.Formats.JSON;
import static downloadmanager.readstrategy.ReadStrategyFactory.Formats.XML;

public abstract class ReadStrategyFactory {

    public interface Formats {

        String CSV = "csv";
        String XML = "xml";
        String JSON = "json";
    }

    public static ReadStrategy getStrategy(final String fileFormat, final InputAskable askable) {
        if (fileFormat.equals(CSV)) {
            return new CsvReader();
        } else if (fileFormat.equals(XML)) {
            return new XmlReader(
                    askable.ask(XmlReader.ROOT_MESSAGE),
                    askable.ask(XmlReader.REFERENCE_MESSAGE),
                    askable.ask(XmlReader.PATH_MESSAGE)
            );
        } else if (fileFormat.equals(JSON)) {
            return new JsonReader(
                    askable.ask(JsonReader.ROOT_MESSAGE),
                    askable.ask(JsonReader.REFERENCE_MESSAGE),
                    askable.ask(JsonReader.PATH_MESSAGE)
            );
        }
        return null;
    }

}
