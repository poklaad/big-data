import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WordMapper extends Mapper<Object, Text, Text, Text> {

    private final Text currentWord = new Text();
    private final Text nextWord = new Text();
    private Analyzer analyzer;

    @Override
    protected void setup(Context context) throws IOException {
        analyzer = new CustomRussianAnalyzer();
    }

    @Override
    public void map(Object key, Text value, Context context)
            throws IOException, InterruptedException {

        List<String> tokens = tokenize(value.toString());

        for (int i = 0; i < tokens.size() - 1; i++) {
            currentWord.set(tokens.get(i));
            nextWord.set(tokens.get(i + 1));
            context.write(currentWord, nextWord);
        }
    }

    @Override
    protected void cleanup(Context context) throws IOException {
        if (analyzer != null) {
            analyzer.close();
        }
    }

    private List<String> tokenize(String text) throws IOException {
        List<String> result = new ArrayList<>();

        try (TokenStream ts = analyzer.tokenStream("field", text)) {
            CharTermAttribute termAttr = ts.addAttribute(CharTermAttribute.class);
            ts.reset();

            while (ts.incrementToken()) {
                result.add(termAttr.toString());
            }
            ts.end();
        }

        return result;
    }
}

