import com.codeforces.iomarkup.antlr.IoMarkupLexer;
import com.codeforces.iomarkup.antlr.IoMarkupParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class IoMarkupGrammarTest {
    private static final String REAL_MARKUPS_PREFIX = "/realMarkups/";
    private static final String LIST_FILENAME = "list.txt";

    @Test
    public void testRealMarkupsParseable() throws IOException {
        for (String resourceName : getIoMarkupResourceNames(REAL_MARKUPS_PREFIX)) {
            CharStream charStream = CharStreams.fromStream(getRealIoMarkupBytes(resourceName));
            var ioMarkupLexer = new IoMarkupLexer(charStream);
            var tokenStream = new CommonTokenStream(ioMarkupLexer);
            var ioMarkup = new IoMarkupParser(tokenStream);

            ioMarkup.ioMarkup(); // todo
        }
    }

    private List<String> getIoMarkupResourceNames(String pathPrefix) throws IOException {
        String path = pathPrefix + LIST_FILENAME;
        InputStream inputStream = getClass().getResourceAsStream(path);
        if (inputStream == null)
            throw new FileNotFoundException("List of IoMarkup resources not found: " + path);

        try (var lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
            return lines.collect(Collectors.toList());
        }
    }

    private InputStream getRealIoMarkupBytes(String name) throws IOException {
        String path = REAL_MARKUPS_PREFIX + name;
        InputStream inputStream = getClass().getResourceAsStream(path);
        if (inputStream == null)
            throw new FileNotFoundException("IoMarkup resource not found: " + path);

        return new BufferedInputStream(inputStream);
    }
}
