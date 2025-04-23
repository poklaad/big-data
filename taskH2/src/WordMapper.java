import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;

public class WordMapper extends Mapper<Object, Text, IntWritable, IntWritable> {
    private static final IntWritable ONE = new IntWritable(1);
    private final IntWritable wordLength = new IntWritable();
    private Analyzer analyzer;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        analyzer = new CustomRussianAnalyzer();
    }

    @Override
    public void map(Object key, Text value, Context context)
            throws IOException, InterruptedException {

        try (TokenStream ts = analyzer.tokenStream(null, value.toString())) {
            CharTermAttribute termAttr = ts.addAttribute(CharTermAttribute.class);
            ts.reset();
            while (ts.incrementToken()) {
                String term = termAttr.toString();
                if (!term.isEmpty()) {
                    wordLength.set(term.length());
                    context.write(wordLength, ONE);
                }
            }
            ts.end();
        }
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        analyzer.close();
    }
}

