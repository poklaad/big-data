import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;
import org.apache.hadoop.util.*;

import java.io.*;
import java.util.*;

public class Summer
extends Reducer<Text, Text, Text, Text>
{
    private Text result = new Text();

    public void reduce(Text key, Iterable<Text> values, Context context)
	throws IOException, InterruptedException
    {
	Map<String, Integer> nextWordCounts = new HashMap<>();

        for (Text val : values) {
            String word = val.toString();
            nextWordCounts.put(word, nextWordCounts.getOrDefault(word, 0) + 1);
        }

        if (!nextWordCounts.isEmpty()) {
            String mostFrequentNextWord = Collections.max(
                nextWordCounts.entrySet(),
                Map.Entry.comparingByValue()
            ).getKey();

            result.set(mostFrequentNextWord);
            context.write(key, result);
        }
    }
}
