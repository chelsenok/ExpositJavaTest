package downloadmanager.reader;

import downloadmanager.File;

public interface ReadStrategy {

    File[] readFiles(final String path);
}
