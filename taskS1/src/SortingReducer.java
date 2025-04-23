import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;

public class SortingReducer extends Reducer<NextWordInfo, NullWritable, Text, IntWritable> {

    @Override
    protected void reduce(NextWordInfo key, Iterable<NullWritable> values, Context context)
            throws IOException, InterruptedException {
        String wordPair = key.getWordPair().toString();
        int frequency   = key.getFrequency().get();
        context.write(new Text(wordPair), new IntWritable(frequency));
    }
}

