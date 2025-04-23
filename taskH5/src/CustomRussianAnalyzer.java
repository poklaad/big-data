import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class CustomRussianAnalyzer extends Analyzer {
    private final CharArraySet stopWords;

public CustomRussianAnalyzer() throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/russian_stopwords.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            
            List<String> words = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line.trim());
            }
            this.stopWords = new CharArraySet(words, true);
        }
    }
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        StandardTokenizer src = new StandardTokenizer();
        TokenStream result = new LowerCaseFilter(src);
        result = new StopFilter(result, stopWords);
        return new TokenStreamComponents(src, result);
    }
}
