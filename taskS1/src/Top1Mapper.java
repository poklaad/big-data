import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;

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

public class Top1Mapper extends Mapper<LongWritable, Text, IntWritable, Text> {
    private final static IntWritable k = new IntWritable(0);
    public void map(LongWritable key, Text val, Context ctx)
            throws IOException, InterruptedException {
        if (key.get() == 0) ctx.write(k, val);
    }
}
