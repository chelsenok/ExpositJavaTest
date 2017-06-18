package downloadmanager.reader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JsonReader implements Reader {

    private static final String LINK = "link";
    private static final String PATH = "path";

    public String[][] readContent(final String path) {
        try {
            final List<String[]> files = new ArrayList<>();
            final StringBuilder builder = new StringBuilder();
            java.nio.file.Files.lines(Paths.get(path)).forEach(builder::append);
            final JsonParser parser = new JsonParser();
            final JsonArray pItem = parser.parse(builder.toString()).getAsJsonArray();
            for (final JsonElement user : pItem) {
                final JsonObject userObject = user.getAsJsonObject();
                files.add(new String[]{
                        userObject.get(LINK).getAsString(),
                        userObject.get(PATH).getAsString()
                });
            }
            return files.toArray(new String[files.size()][2]);
        } catch (final IOException ignored) {
            return null;
        }
    }
}
