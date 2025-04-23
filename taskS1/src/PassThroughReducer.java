import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;


public class PassThroughReducer
        extends Reducer<IntWritable, Text, Text, NullWritable> {
    public void reduce(IntWritable k, Iterable<Text> vals, Context ctx)
            throws IOException, InterruptedException {
        for (Text v : vals) { ctx.write(v, NullWritable.get()); break; }
    }
}
