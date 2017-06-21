package reader;

public abstract class ReaderFactory {

    private static final String CSV = "csv";
    private static final String XML = "xml";
    private static final String JSON = "json";

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
