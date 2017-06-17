package downloadmanager.reader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import downloadmanager.file.File;

public class JsonReader implements Reader {

    static final String REFERENCE_MESSAGE = "JSON-reference element key: ";
    static final String PATH_MESSAGE = "JSON-path element key: ";

    private final String mReference;
    private final String mPath;

    JsonReader(final String reference, final String path) {
        mReference = reference;
        mPath = path;
    }

    public File[] readFiles(final String path) {
        try {
            final List<File> files = new ArrayList<>();
            final StringBuilder builder = new StringBuilder();
            java.nio.file.Files.lines(Paths.get(path)).forEach(builder::append);
            final JsonParser parser = new JsonParser();
            final JsonArray pItem = parser.parse(builder.toString()).getAsJsonArray();
            for (final JsonElement user : pItem) {
                final JsonObject userObject = user.getAsJsonObject();
                files.add(new File(
                        userObject.get(mReference).getAsString(),
                        userObject.get(mPath).getAsString()
                ));
            }
            return files.toArray(new File[files.size()]);
        } catch (final Exception ignored) {
            return null;
        }
    }
}
