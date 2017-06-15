package downloadmanager.readstrategy;

import downloadmanager.File;

public interface ReadStrategy {

    File[] readFiles(final String path);
}
