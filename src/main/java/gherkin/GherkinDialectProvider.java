package gherkin;

import com.google.gson.Gson;
import gherkin.ast.Location;

import java.util.Map;

public class GherkinDialectProvider implements IGherkinDialectProvider {
    private final String default_dialect_name;
    private Map dialects;

    public GherkinDialectProvider(String default_dialect_name, String dialectFile) {
        this.default_dialect_name = default_dialect_name;
        if (dialectFile != null)
            this.loadDialects(dialectFile);
    }

    public GherkinDialectProvider() {
        this("en", null);
    }

    public void loadDialects(String source) {
        Gson gson = new Gson();
        this.dialects = gson.fromJson(source, Map.class);
    }

    public GherkinDialect getDefaultDialect() {
        return getDialect(default_dialect_name, null);
    }

    @Override
    public GherkinDialect getDialect(String language, Location location) {
        Map map = (Map) dialects.get(language);
        if (map == null) {
            throw new ParserException.NoSuchLanguageException(language, location);
        }

        return new GherkinDialect(language, map);
    }

}
