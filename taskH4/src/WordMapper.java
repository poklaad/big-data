import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;

import java.io.*;
import java.io.StringReader;
import java.util.*;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class WordMapper extends Mapper<Object, Text, Text, Text> {

    private final Text currentWord = new Text();
    private final Text nextWord = new Text();

    private static final Pattern SENTENCE_END = Pattern.compile("[.!?]+");

    private Analyzer analyzer;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        this.analyzer = new CustomRussianAnalyzer();
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        this.analyzer.close();
    }

    @Override
    public void map(Object key, Text value, Context context)
            throws IOException, InterruptedException {

        String[] sentences = SENTENCE_END.split(value.toString());

        for (String sentence : sentences) {
            List<String> tokens = tokenize(sentence);

            for (int i = 0; i < tokens.size() - 1; i++) {
                currentWord.set(tokens.get(i));
                nextWord.set(tokens.get(i + 1));
                context.write(currentWord, nextWord);
            }
        }
    }

    private List<String> tokenize(String text) throws IOException {
        List<String> tokens = new ArrayList<>();
        try (TokenStream tokenStream = analyzer.tokenStream("field", new StringReader(text))) {
            CharTermAttribute termAttr = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                tokens.add(termAttr.toString());
            }
            tokenStream.end();
        }
        return tokens;
    }
}

