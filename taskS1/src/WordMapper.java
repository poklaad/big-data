import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class WordMapper extends Mapper<Object, Text, Text, Text> {

    private static final Pattern SENTENCE_END = Pattern.compile("[.!?]+");
    private final Text cur  = new Text();
    private final Text next = new Text();

    private Analyzer analyzer;

    @Override
    protected void setup(Context ctx) throws IOException {
        analyzer = new CustomRussianAnalyzer();
    }

    @Override
    public void map(Object key, Text value, Context ctx)
            throws IOException, InterruptedException {

        String[] sentences = SENTENCE_END.split(value.toString());

        for (String sentence : sentences) {
            List<String> tokens = tokenize(sentence);

            for (int i = 0; i < tokens.size() - 1; i++) {
                cur.set(tokens.get(i));
                next.set(tokens.get(i + 1));
                ctx.write(cur, next);
            }
        }
    }

    private List<String> tokenize(String text) throws IOException {
        List<String> out = new ArrayList<>();
        TokenStream ts = analyzer.tokenStream("f", new StringReader(text));
        CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
        ts.reset();
        while (ts.incrementToken()) {
            out.add(term.toString());
        }
        ts.end();
        ts.close();
        return out;
    }

    @Override
    protected void cleanup(Context ctx) throws IOException {
        analyzer.close();
    }
}

