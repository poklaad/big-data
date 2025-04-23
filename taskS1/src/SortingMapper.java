import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;

public class SortingMapper
        extends Mapper<LongWritable, Text, NextWordInfo, NullWritable> {

    @Override
    protected void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {

        String line = value.toString().trim();
        if (line.isEmpty()) {
            return;
        }

        String[] parts = line.split("\\t");
        String firstWord, nextWord, freqStr;

        if (parts.length == 3) {
            firstWord = parts[0];
            nextWord  = parts[1];
            freqStr   = parts[2];
        } else if (parts.length == 2 && parts[1].contains(":")) {
            int idx = parts[1].lastIndexOf(':');
            firstWord = parts[0];
            nextWord  = parts[1].substring(0, idx);
            freqStr   = parts[1].substring(idx + 1);
        } else {
            return;
        }

        int frequency;
        try {
            frequency = Integer.parseInt(freqStr);
        } catch (NumberFormatException e) {
            return;
        }

        NextWordInfo info = new NextWordInfo(frequency,
                                             firstWord + "\t" + nextWord);
        context.write(info, NullWritable.get());
    }
}

