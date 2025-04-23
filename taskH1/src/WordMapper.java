import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import java.io.*;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.tokenattributes.*;

public class WordMapper extends Mapper<Object, Text, Text, IntWritable> {
    private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();
    
    public void map(Object key, Text value, Context context)
            throws IOException, InterruptedException {
        String text = value.toString();
        Analyzer analyzer = new CustomRussianAnalyzer();
        TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(text));
        CharTermAttribute termAtt = tokenStream.addAttribute(CharTermAttribute.class);
        
        try {
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                String term = termAtt.toString();
                word.set(term);
                context.write(word, one);
            }
            tokenStream.end();
        } finally {
            tokenStream.close();
            analyzer.close();
        }
    }
}
